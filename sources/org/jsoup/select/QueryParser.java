/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.select;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.internal.StringUtil;
import org.jsoup.parser.TokenQueue;
import org.jsoup.select.CombiningEvaluator;
import org.jsoup.select.Evaluator;
import org.jsoup.select.Selector;
import org.jsoup.select.StructuralEvaluator;

public class QueryParser {
    private static final char[] Combinators = new char[]{',', '>', '+', '~', ' '};
    private static final String[] AttributeEvals = new String[]{"=", "!=", "^=", "$=", "*=", "~="};
    private final TokenQueue tq;
    private final String query;
    private final List<Evaluator> evals = new ArrayList<Evaluator>();
    private static final Pattern NTH_AB = Pattern.compile("(([+-])?(\\d+)?)n(\\s*([+-])?\\s*\\d+)?", 2);
    private static final Pattern NTH_B = Pattern.compile("([+-])?(\\d+)");

    private QueryParser(String query) {
        Validate.notEmpty(query);
        this.query = query = query.trim();
        this.tq = new TokenQueue(query);
    }

    public static Evaluator parse(String query) {
        try {
            QueryParser p = new QueryParser(query);
            return p.parse();
        }
        catch (IllegalArgumentException e) {
            throw new Selector.SelectorParseException(e.getMessage());
        }
    }

    Evaluator parse() {
        this.tq.consumeWhitespace();
        if (this.tq.matchesAny(Combinators)) {
            this.evals.add(new StructuralEvaluator.Root());
            this.combinator(this.tq.consume());
        } else {
            this.evals.add(this.consumeEvaluator());
        }
        while (!this.tq.isEmpty()) {
            boolean seenWhite = this.tq.consumeWhitespace();
            if (this.tq.matchesAny(Combinators)) {
                this.combinator(this.tq.consume());
                continue;
            }
            if (seenWhite) {
                this.combinator(' ');
                continue;
            }
            this.evals.add(this.consumeEvaluator());
        }
        if (this.evals.size() == 1) {
            return this.evals.get(0);
        }
        return new CombiningEvaluator.And(this.evals);
    }

    private void combinator(char combinator) {
        Evaluator rootEval;
        Evaluator currentEval;
        this.tq.consumeWhitespace();
        String subQuery = this.consumeSubQuery();
        Evaluator newEval = QueryParser.parse(subQuery);
        boolean replaceRightMost = false;
        if (this.evals.size() == 1) {
            currentEval = this.evals.get(0);
            rootEval = currentEval;
            if (rootEval instanceof CombiningEvaluator.Or && combinator != ',') {
                currentEval = ((CombiningEvaluator.Or)currentEval).rightMostEvaluator();
                assert (currentEval != null);
                replaceRightMost = true;
            }
        } else {
            rootEval = currentEval = new CombiningEvaluator.And(this.evals);
        }
        this.evals.clear();
        switch (combinator) {
            case '>': {
                StructuralEvaluator.ImmediateParentRun run = currentEval instanceof StructuralEvaluator.ImmediateParentRun ? (StructuralEvaluator.ImmediateParentRun)currentEval : new StructuralEvaluator.ImmediateParentRun(currentEval);
                run.add(newEval);
                currentEval = run;
                break;
            }
            case ' ': {
                currentEval = new CombiningEvaluator.And(new StructuralEvaluator.Parent(currentEval), newEval);
                break;
            }
            case '+': {
                currentEval = new CombiningEvaluator.And(new StructuralEvaluator.ImmediatePreviousSibling(currentEval), newEval);
                break;
            }
            case '~': {
                currentEval = new CombiningEvaluator.And(new StructuralEvaluator.PreviousSibling(currentEval), newEval);
                break;
            }
            case ',': {
                CombiningEvaluator.Or or;
                if (currentEval instanceof CombiningEvaluator.Or) {
                    or = (CombiningEvaluator.Or)currentEval;
                } else {
                    or = new CombiningEvaluator.Or();
                    or.add(currentEval);
                }
                or.add(newEval);
                currentEval = or;
                break;
            }
            default: {
                throw new Selector.SelectorParseException("Unknown combinator '%s'", Character.valueOf(combinator));
            }
        }
        if (replaceRightMost) {
            ((CombiningEvaluator.Or)rootEval).replaceRightMostEvaluator(currentEval);
        } else {
            rootEval = currentEval;
        }
        this.evals.add(rootEval);
    }

    private String consumeSubQuery() {
        StringBuilder sq = StringUtil.borrowBuilder();
        boolean seenClause = false;
        while (!this.tq.isEmpty()) {
            if (this.tq.matchesAny(Combinators)) {
                if (seenClause) break;
                sq.append(this.tq.consume());
                continue;
            }
            seenClause = true;
            if (this.tq.matches("(")) {
                sq.append("(").append(this.tq.chompBalanced('(', ')')).append(")");
                continue;
            }
            if (this.tq.matches("[")) {
                sq.append("[").append(this.tq.chompBalanced('[', ']')).append("]");
                continue;
            }
            sq.append(this.tq.consume());
        }
        return StringUtil.releaseBuilder(sq);
    }

    private Evaluator consumeEvaluator() {
        if (this.tq.matchChomp("#")) {
            return this.byId();
        }
        if (this.tq.matchChomp(".")) {
            return this.byClass();
        }
        if (this.tq.matchesWord() || this.tq.matches("*|")) {
            return this.byTag();
        }
        if (this.tq.matches("[")) {
            return this.byAttribute();
        }
        if (this.tq.matchChomp("*")) {
            return new Evaluator.AllElements();
        }
        if (this.tq.matchChomp(":")) {
            return this.parsePseudoSelector();
        }
        throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", this.query, this.tq.remainder());
    }

    private Evaluator parsePseudoSelector() {
        String pseudo;
        switch (pseudo = this.tq.consumeCssIdentifier()) {
            case "lt": {
                return new Evaluator.IndexLessThan(this.consumeIndex());
            }
            case "gt": {
                return new Evaluator.IndexGreaterThan(this.consumeIndex());
            }
            case "eq": {
                return new Evaluator.IndexEquals(this.consumeIndex());
            }
            case "has": {
                return this.has();
            }
            case "is": {
                return this.is();
            }
            case "contains": {
                return this.contains(false);
            }
            case "containsOwn": {
                return this.contains(true);
            }
            case "containsWholeText": {
                return this.containsWholeText(false);
            }
            case "containsWholeOwnText": {
                return this.containsWholeText(true);
            }
            case "containsData": {
                return this.containsData();
            }
            case "matches": {
                return this.matches(false);
            }
            case "matchesOwn": {
                return this.matches(true);
            }
            case "matchesWholeText": {
                return this.matchesWholeText(false);
            }
            case "matchesWholeOwnText": {
                return this.matchesWholeText(true);
            }
            case "not": {
                return this.not();
            }
            case "nth-child": {
                return this.cssNthChild(false, false);
            }
            case "nth-last-child": {
                return this.cssNthChild(true, false);
            }
            case "nth-of-type": {
                return this.cssNthChild(false, true);
            }
            case "nth-last-of-type": {
                return this.cssNthChild(true, true);
            }
            case "first-child": {
                return new Evaluator.IsFirstChild();
            }
            case "last-child": {
                return new Evaluator.IsLastChild();
            }
            case "first-of-type": {
                return new Evaluator.IsFirstOfType();
            }
            case "last-of-type": {
                return new Evaluator.IsLastOfType();
            }
            case "only-child": {
                return new Evaluator.IsOnlyChild();
            }
            case "only-of-type": {
                return new Evaluator.IsOnlyOfType();
            }
            case "empty": {
                return new Evaluator.IsEmpty();
            }
            case "root": {
                return new Evaluator.IsRoot();
            }
            case "matchText": {
                return new Evaluator.MatchText();
            }
        }
        throw new Selector.SelectorParseException("Could not parse query '%s': unexpected token at '%s'", this.query, this.tq.remainder());
    }

    private Evaluator byId() {
        String id = this.tq.consumeCssIdentifier();
        Validate.notEmpty(id);
        return new Evaluator.Id(id);
    }

    private Evaluator byClass() {
        String className = this.tq.consumeCssIdentifier();
        Validate.notEmpty(className);
        return new Evaluator.Class(className.trim());
    }

    private Evaluator byTag() {
        Evaluator eval;
        String tagName = Normalizer.normalize(this.tq.consumeElementSelector());
        Validate.notEmpty(tagName);
        if (tagName.startsWith("*|")) {
            String plainTag = tagName.substring(2);
            eval = new CombiningEvaluator.Or(new Evaluator.Tag(plainTag), new Evaluator.TagEndsWith(tagName.replace("*|", ":")));
        } else {
            if (tagName.contains("|")) {
                tagName = tagName.replace("|", ":");
            }
            eval = new Evaluator.Tag(tagName);
        }
        return eval;
    }

    private Evaluator byAttribute() {
        Evaluator eval;
        TokenQueue cq = new TokenQueue(this.tq.chompBalanced('[', ']'));
        String key = cq.consumeToAny(AttributeEvals);
        Validate.notEmpty(key);
        cq.consumeWhitespace();
        if (cq.isEmpty()) {
            eval = key.startsWith("^") ? new Evaluator.AttributeStarting(key.substring(1)) : (key.equals("*") ? new Evaluator.AttributeStarting("") : new Evaluator.Attribute(key));
        } else if (cq.matchChomp("=")) {
            eval = new Evaluator.AttributeWithValue(key, cq.remainder());
        } else if (cq.matchChomp("!=")) {
            eval = new Evaluator.AttributeWithValueNot(key, cq.remainder());
        } else if (cq.matchChomp("^=")) {
            eval = new Evaluator.AttributeWithValueStarting(key, cq.remainder());
        } else if (cq.matchChomp("$=")) {
            eval = new Evaluator.AttributeWithValueEnding(key, cq.remainder());
        } else if (cq.matchChomp("*=")) {
            eval = new Evaluator.AttributeWithValueContaining(key, cq.remainder());
        } else if (cq.matchChomp("~=")) {
            eval = new Evaluator.AttributeWithValueMatching(key, Pattern.compile(cq.remainder()));
        } else {
            throw new Selector.SelectorParseException("Could not parse attribute query '%s': unexpected token at '%s'", this.query, cq.remainder());
        }
        return eval;
    }

    private Evaluator cssNthChild(boolean backwards, boolean ofType) {
        int b2;
        int a;
        String arg = Normalizer.normalize(this.consumeParens());
        Matcher mAB = NTH_AB.matcher(arg);
        Matcher mB = NTH_B.matcher(arg);
        if ("odd".equals(arg)) {
            a = 2;
            b2 = 1;
        } else if ("even".equals(arg)) {
            a = 2;
            b2 = 0;
        } else if (mAB.matches()) {
            a = mAB.group(3) != null ? Integer.parseInt(mAB.group(1).replaceFirst("^\\+", "")) : 1;
            b2 = mAB.group(4) != null ? Integer.parseInt(mAB.group(4).replaceFirst("^\\+", "")) : 0;
        } else if (mB.matches()) {
            a = 0;
            b2 = Integer.parseInt(mB.group().replaceFirst("^\\+", ""));
        } else {
            throw new Selector.SelectorParseException("Could not parse nth-index '%s': unexpected format", arg);
        }
        Evaluator.CssNthEvaluator eval = ofType ? (backwards ? new Evaluator.IsNthLastOfType(a, b2) : new Evaluator.IsNthOfType(a, b2)) : (backwards ? new Evaluator.IsNthLastChild(a, b2) : new Evaluator.IsNthChild(a, b2));
        return eval;
    }

    private String consumeParens() {
        return this.tq.chompBalanced('(', ')');
    }

    private int consumeIndex() {
        String index = this.consumeParens().trim();
        Validate.isTrue(StringUtil.isNumeric(index), "Index must be numeric");
        return Integer.parseInt(index);
    }

    private Evaluator has() {
        String subQuery = this.consumeParens();
        Validate.notEmpty(subQuery, ":has(selector) sub-select must not be empty");
        return new StructuralEvaluator.Has(QueryParser.parse(subQuery));
    }

    private Evaluator is() {
        String subQuery = this.consumeParens();
        Validate.notEmpty(subQuery, ":is(selector) sub-select must not be empty");
        return new StructuralEvaluator.Is(QueryParser.parse(subQuery));
    }

    private Evaluator contains(boolean own) {
        String query = own ? ":containsOwn" : ":contains";
        String searchText = TokenQueue.unescape(this.consumeParens());
        Validate.notEmpty(searchText, query + "(text) query must not be empty");
        return own ? new Evaluator.ContainsOwnText(searchText) : new Evaluator.ContainsText(searchText);
    }

    private Evaluator containsWholeText(boolean own) {
        String query = own ? ":containsWholeOwnText" : ":containsWholeText";
        String searchText = TokenQueue.unescape(this.consumeParens());
        Validate.notEmpty(searchText, query + "(text) query must not be empty");
        return own ? new Evaluator.ContainsWholeOwnText(searchText) : new Evaluator.ContainsWholeText(searchText);
    }

    private Evaluator containsData() {
        String searchText = TokenQueue.unescape(this.consumeParens());
        Validate.notEmpty(searchText, ":containsData(text) query must not be empty");
        return new Evaluator.ContainsData(searchText);
    }

    private Evaluator matches(boolean own) {
        String query = own ? ":matchesOwn" : ":matches";
        String regex = this.consumeParens();
        Validate.notEmpty(regex, query + "(regex) query must not be empty");
        return own ? new Evaluator.MatchesOwn(Pattern.compile(regex)) : new Evaluator.Matches(Pattern.compile(regex));
    }

    private Evaluator matchesWholeText(boolean own) {
        String query = own ? ":matchesWholeOwnText" : ":matchesWholeText";
        String regex = this.consumeParens();
        Validate.notEmpty(regex, query + "(regex) query must not be empty");
        return own ? new Evaluator.MatchesWholeOwnText(Pattern.compile(regex)) : new Evaluator.MatchesWholeText(Pattern.compile(regex));
    }

    private Evaluator not() {
        String subQuery = this.consumeParens();
        Validate.notEmpty(subQuery, ":not(selector) subselect must not be empty");
        return new StructuralEvaluator.Not(QueryParser.parse(subQuery));
    }

    public String toString() {
        return this.query;
    }
}

