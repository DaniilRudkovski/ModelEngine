/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.Range;
import org.jsoup.parser.CharacterReader;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.parser.Token;
import org.jsoup.parser.Tokeniser;

abstract class TreeBuilder {
    protected Parser parser;
    CharacterReader reader;
    Tokeniser tokeniser;
    Document doc;
    ArrayList<Element> stack;
    String baseUri;
    Token currentToken;
    ParseSettings settings;
    Map<String, Tag> seenTags;
    private Token.StartTag start;
    private final Token.EndTag end = new Token.EndTag(this);
    boolean trackSourceRange;

    TreeBuilder() {
    }

    abstract ParseSettings defaultSettings();

    void initialiseParse(Reader input, String baseUri, Parser parser) {
        Validate.notNullParam(input, "input");
        Validate.notNullParam(baseUri, "baseUri");
        Validate.notNull(parser);
        this.doc = new Document(parser.defaultNamespace(), baseUri);
        this.doc.parser(parser);
        this.parser = parser;
        this.settings = parser.settings();
        this.reader = new CharacterReader(input);
        this.trackSourceRange = parser.isTrackPosition();
        this.reader.trackNewlines(parser.isTrackErrors() || this.trackSourceRange);
        this.tokeniser = new Tokeniser(this);
        this.stack = new ArrayList(32);
        this.seenTags = new HashMap<String, Tag>();
        this.start = new Token.StartTag(this);
        this.currentToken = this.start;
        this.baseUri = baseUri;
    }

    Document parse(Reader input, String baseUri, Parser parser) {
        this.initialiseParse(input, baseUri, parser);
        this.runParser();
        this.reader.close();
        this.reader = null;
        this.tokeniser = null;
        this.stack = null;
        this.seenTags = null;
        return this.doc;
    }

    abstract TreeBuilder newInstance();

    abstract List<Node> parseFragment(String var1, Element var2, String var3, Parser var4);

    void runParser() {
        Tokeniser tokeniser = this.tokeniser;
        Token.TokenType eof = Token.TokenType.EOF;
        while (true) {
            Token token;
            this.currentToken = token = tokeniser.read();
            this.process(token);
            if (token.type == eof) break;
            token.reset();
        }
        while (!this.stack.isEmpty()) {
            this.pop();
        }
    }

    abstract boolean process(Token var1);

    boolean processStartTag(String name) {
        Token.StartTag start = this.start;
        if (this.currentToken == start) {
            return this.process(new Token.StartTag(this).name(name));
        }
        return this.process(start.reset().name(name));
    }

    boolean processStartTag(String name, Attributes attrs) {
        Token.StartTag start = this.start;
        if (this.currentToken == start) {
            return this.process(new Token.StartTag(this).nameAttr(name, attrs));
        }
        start.reset();
        start.nameAttr(name, attrs);
        return this.process(start);
    }

    boolean processEndTag(String name) {
        if (this.currentToken == this.end) {
            return this.process(new Token.EndTag(this).name(name));
        }
        return this.process(this.end.reset().name(name));
    }

    final Element pop() {
        int size = this.stack.size();
        Element removed = this.stack.remove(size - 1);
        this.onNodeClosed(removed);
        return removed;
    }

    final void push(Element element) {
        this.stack.add(element);
        this.onNodeInserted(element);
    }

    Element currentElement() {
        int size = this.stack.size();
        return size > 0 ? this.stack.get(size - 1) : this.doc;
    }

    boolean currentElementIs(String normalName) {
        if (this.stack.size() == 0) {
            return false;
        }
        Element current = this.currentElement();
        return current != null && current.normalName().equals(normalName) && current.tag().namespace().equals("http://www.w3.org/1999/xhtml");
    }

    boolean currentElementIs(String normalName, String namespace) {
        if (this.stack.size() == 0) {
            return false;
        }
        Element current = this.currentElement();
        return current != null && current.normalName().equals(normalName) && current.tag().namespace().equals(namespace);
    }

    void error(String msg) {
        this.error(msg, null);
    }

    void error(String msg, Object ... args) {
        ParseErrorList errors = this.parser.getErrors();
        if (errors.canAddError()) {
            errors.add(new ParseError(this.reader, msg, args));
        }
    }

    boolean isContentForTagData(String normalName) {
        return false;
    }

    Tag tagFor(String tagName, String namespace, ParseSettings settings) {
        Tag cached = this.seenTags.get(tagName);
        if (cached == null || !cached.namespace().equals(namespace)) {
            Tag tag = Tag.valueOf(tagName, namespace, settings);
            this.seenTags.put(tagName, tag);
            return tag;
        }
        return cached;
    }

    Tag tagFor(String tagName, ParseSettings settings) {
        return this.tagFor(tagName, this.defaultNamespace(), settings);
    }

    String defaultNamespace() {
        return "http://www.w3.org/1999/xhtml";
    }

    void onNodeInserted(Node node) {
        this.trackNodePosition(node, true);
    }

    void onNodeClosed(Node node) {
        this.trackNodePosition(node, false);
    }

    private void trackNodePosition(Node node, boolean isStart) {
        if (!this.trackSourceRange) {
            return;
        }
        Token token = this.currentToken;
        int startPos = token.startPos();
        int endPos = token.endPos();
        if (node instanceof Element) {
            Element el = (Element)node;
            if (token.isEOF()) {
                if (el.endSourceRange().isTracked()) {
                    return;
                }
                startPos = endPos = this.reader.pos();
            } else if (isStart) {
                if (!token.isStartTag() || !el.normalName().equals(token.asStartTag().normalName)) {
                    endPos = startPos;
                }
            } else if (!(el.tag().isEmpty() || el.tag().isSelfClosing() || token.isEndTag() && el.normalName().equals(token.asEndTag().normalName))) {
                endPos = startPos;
            }
        }
        Range.Position startPosition = new Range.Position(startPos, this.reader.lineNumber(startPos), this.reader.columnNumber(startPos));
        Range.Position endPosition = new Range.Position(endPos, this.reader.lineNumber(endPos), this.reader.columnNumber(endPos));
        Range range = new Range(startPosition, endPosition);
        node.attributes().userData(isStart ? "jsoup.start" : "jsoup.end", range);
    }
}

