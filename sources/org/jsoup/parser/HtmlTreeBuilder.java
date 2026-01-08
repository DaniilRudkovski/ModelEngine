/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.jsoup.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.FormElement;
import org.jsoup.nodes.LeafNode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.HtmlTreeBuilderState;
import org.jsoup.parser.ParseError;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.parser.Token;
import org.jsoup.parser.TokeniserState;
import org.jsoup.parser.TreeBuilder;
import org.jspecify.annotations.Nullable;

public class HtmlTreeBuilder
extends TreeBuilder {
    static final String[] TagsSearchInScope = new String[]{"applet", "caption", "html", "marquee", "object", "table", "td", "th"};
    static final String[] TagSearchList = new String[]{"ol", "ul"};
    static final String[] TagSearchButton = new String[]{"button"};
    static final String[] TagSearchTableScope = new String[]{"html", "table"};
    static final String[] TagSearchSelectScope = new String[]{"optgroup", "option"};
    static final String[] TagSearchEndTags = new String[]{"dd", "dt", "li", "optgroup", "option", "p", "rb", "rp", "rt", "rtc"};
    static final String[] TagThoroughSearchEndTags = new String[]{"caption", "colgroup", "dd", "dt", "li", "optgroup", "option", "p", "rb", "rp", "rt", "rtc", "tbody", "td", "tfoot", "th", "thead", "tr"};
    static final String[] TagSearchSpecial = new String[]{"address", "applet", "area", "article", "aside", "base", "basefont", "bgsound", "blockquote", "body", "br", "button", "caption", "center", "col", "colgroup", "command", "dd", "details", "dir", "div", "dl", "dt", "embed", "fieldset", "figcaption", "figure", "footer", "form", "frame", "frameset", "h1", "h2", "h3", "h4", "h5", "h6", "head", "header", "hgroup", "hr", "html", "iframe", "img", "input", "isindex", "li", "link", "listing", "marquee", "menu", "meta", "nav", "noembed", "noframes", "noscript", "object", "ol", "p", "param", "plaintext", "pre", "script", "section", "select", "style", "summary", "table", "tbody", "td", "textarea", "tfoot", "th", "thead", "title", "tr", "ul", "wbr", "xmp"};
    static final String[] TagMathMlTextIntegration = new String[]{"mi", "mn", "mo", "ms", "mtext"};
    static final String[] TagSvgHtmlIntegration = new String[]{"desc", "foreignObject", "title"};
    public static final int MaxScopeSearchDepth = 100;
    private HtmlTreeBuilderState state;
    private HtmlTreeBuilderState originalState;
    private boolean baseUriSetFromDoc;
    private @Nullable Element headElement;
    private @Nullable FormElement formElement;
    private @Nullable Element contextElement;
    private ArrayList<Element> formattingElements;
    private ArrayList<HtmlTreeBuilderState> tmplInsertMode;
    private List<Token.Character> pendingTableCharacters;
    private Token.EndTag emptyEnd;
    private boolean framesetOk;
    private boolean fosterInserts;
    private boolean fragmentParsing;
    private static final int maxQueueDepth = 256;
    private final String[] specificScopeTarget = new String[]{null};
    private static final int maxUsedFormattingElements = 12;

    @Override
    ParseSettings defaultSettings() {
        return ParseSettings.htmlDefault;
    }

    @Override
    HtmlTreeBuilder newInstance() {
        return new HtmlTreeBuilder();
    }

    @Override
    protected void initialiseParse(Reader input, String baseUri, Parser parser) {
        super.initialiseParse(input, baseUri, parser);
        this.state = HtmlTreeBuilderState.Initial;
        this.originalState = null;
        this.baseUriSetFromDoc = false;
        this.headElement = null;
        this.formElement = null;
        this.contextElement = null;
        this.formattingElements = new ArrayList();
        this.tmplInsertMode = new ArrayList();
        this.pendingTableCharacters = new ArrayList<Token.Character>();
        this.emptyEnd = new Token.EndTag(this);
        this.framesetOk = true;
        this.fosterInserts = false;
        this.fragmentParsing = false;
    }

    @Override
    List<Node> parseFragment(String inputFragment, @Nullable Element context, String baseUri, Parser parser) {
        this.state = HtmlTreeBuilderState.Initial;
        this.initialiseParse(new StringReader(inputFragment), baseUri, parser);
        this.contextElement = context;
        this.fragmentParsing = true;
        Node root = null;
        if (context != null) {
            String contextTag;
            if (context.ownerDocument() != null) {
                this.doc.quirksMode(context.ownerDocument().quirksMode());
            }
            switch (contextTag = context.normalName()) {
                case "title": 
                case "textarea": {
                    this.tokeniser.transition(TokeniserState.Rcdata);
                    break;
                }
                case "iframe": 
                case "noembed": 
                case "noframes": 
                case "style": 
                case "xmp": {
                    this.tokeniser.transition(TokeniserState.Rawtext);
                    break;
                }
                case "script": {
                    this.tokeniser.transition(TokeniserState.ScriptData);
                    break;
                }
                case "plaintext": {
                    this.tokeniser.transition(TokeniserState.PLAINTEXT);
                    break;
                }
                case "template": {
                    this.tokeniser.transition(TokeniserState.Data);
                    this.pushTemplateMode(HtmlTreeBuilderState.InTemplate);
                    break;
                }
                default: {
                    this.tokeniser.transition(TokeniserState.Data);
                }
            }
            root = new Element(this.tagFor(contextTag, this.settings), baseUri);
            this.doc.appendChild(root);
            this.push((Element)root);
            this.resetInsertionMode();
            for (Element formSearch = context; formSearch != null; formSearch = formSearch.parent()) {
                if (!(formSearch instanceof FormElement)) continue;
                this.formElement = (FormElement)formSearch;
                break;
            }
        }
        this.runParser();
        if (context != null) {
            List<Node> nodes = root.siblingNodes();
            if (!nodes.isEmpty()) {
                ((Element)root).insertChildren(-1, nodes);
            }
            return root.childNodes();
        }
        return this.doc.childNodes();
    }

    @Override
    protected boolean process(Token token) {
        HtmlTreeBuilderState dispatch = this.useCurrentOrForeignInsert(token) ? this.state : HtmlTreeBuilderState.ForeignContent;
        return dispatch.process(token, this);
    }

    boolean useCurrentOrForeignInsert(Token token) {
        if (this.stack.isEmpty()) {
            return true;
        }
        Element el = this.currentElement();
        String ns = el.tag().namespace();
        if ("http://www.w3.org/1999/xhtml".equals(ns)) {
            return true;
        }
        if (HtmlTreeBuilder.isMathmlTextIntegration(el)) {
            if (token.isStartTag() && !"mglyph".equals(token.asStartTag().normalName) && !"malignmark".equals(token.asStartTag().normalName)) {
                return true;
            }
            if (token.isCharacter()) {
                return true;
            }
        }
        if ("http://www.w3.org/1998/Math/MathML".equals(ns) && el.nameIs("annotation-xml") && token.isStartTag() && "svg".equals(token.asStartTag().normalName)) {
            return true;
        }
        if (HtmlTreeBuilder.isHtmlIntegration(el) && (token.isStartTag() || token.isCharacter())) {
            return true;
        }
        return token.isEOF();
    }

    static boolean isMathmlTextIntegration(Element el) {
        return "http://www.w3.org/1998/Math/MathML".equals(el.tag().namespace()) && StringUtil.inSorted(el.normalName(), TagMathMlTextIntegration);
    }

    static boolean isHtmlIntegration(Element el) {
        String encoding;
        if ("http://www.w3.org/1998/Math/MathML".equals(el.tag().namespace()) && el.nameIs("annotation-xml") && ((encoding = Normalizer.normalize(el.attr("encoding"))).equals("text/html") || encoding.equals("application/xhtml+xml"))) {
            return true;
        }
        return "http://www.w3.org/2000/svg".equals(el.tag().namespace()) && StringUtil.in(el.tagName(), TagSvgHtmlIntegration);
    }

    boolean process(Token token, HtmlTreeBuilderState state) {
        return state.process(token, this);
    }

    void transition(HtmlTreeBuilderState state) {
        this.state = state;
    }

    HtmlTreeBuilderState state() {
        return this.state;
    }

    void markInsertionMode() {
        this.originalState = this.state;
    }

    HtmlTreeBuilderState originalState() {
        return this.originalState;
    }

    void framesetOk(boolean framesetOk) {
        this.framesetOk = framesetOk;
    }

    boolean framesetOk() {
        return this.framesetOk;
    }

    Document getDocument() {
        return this.doc;
    }

    String getBaseUri() {
        return this.baseUri;
    }

    void maybeSetBaseUri(Element base) {
        if (this.baseUriSetFromDoc) {
            return;
        }
        String href = base.absUrl("href");
        if (href.length() != 0) {
            this.baseUri = href;
            this.baseUriSetFromDoc = true;
            this.doc.setBaseUri(href);
        }
    }

    boolean isFragmentParsing() {
        return this.fragmentParsing;
    }

    void error(HtmlTreeBuilderState state) {
        if (this.parser.getErrors().canAddError()) {
            this.parser.getErrors().add(new ParseError(this.reader, "Unexpected %s token [%s] when in state [%s]", new Object[]{this.currentToken.tokenType(), this.currentToken, state}));
        }
    }

    Element createElementFor(Token.StartTag startTag, String namespace, boolean forcePreserveCase) {
        int dupes;
        Attributes attributes = startTag.attributes;
        if (!forcePreserveCase) {
            attributes = this.settings.normalizeAttributes(attributes);
        }
        if (attributes != null && !attributes.isEmpty() && (dupes = attributes.deduplicate(this.settings)) > 0) {
            this.error("Dropped duplicate attribute(s) in tag [%s]", startTag.normalName);
        }
        Tag tag = this.tagFor(startTag.tagName, namespace, forcePreserveCase ? ParseSettings.preserveCase : this.settings);
        return tag.normalName().equals("form") ? new FormElement(tag, null, attributes) : new Element(tag, null, attributes);
    }

    Element insertElementFor(Token.StartTag startTag) {
        Element el = this.createElementFor(startTag, "http://www.w3.org/1999/xhtml", false);
        this.doInsertElement(el, startTag);
        if (startTag.isSelfClosing()) {
            Tag tag = el.tag();
            if (tag.isKnownTag()) {
                if (!tag.isEmpty()) {
                    this.tokeniser.error("Tag [%s] cannot be self closing; not a void tag", tag.normalName());
                }
            } else {
                tag.setSelfClosing();
            }
            this.tokeniser.transition(TokeniserState.Data);
            this.tokeniser.emit(this.emptyEnd.reset().name(el.tagName()));
        }
        return el;
    }

    Element insertForeignElementFor(Token.StartTag startTag, String namespace) {
        Element el = this.createElementFor(startTag, namespace, true);
        this.doInsertElement(el, startTag);
        if (startTag.isSelfClosing()) {
            el.tag().setSelfClosing();
            this.pop();
        }
        return el;
    }

    Element insertEmptyElementFor(Token.StartTag startTag) {
        Element el = this.createElementFor(startTag, "http://www.w3.org/1999/xhtml", false);
        this.doInsertElement(el, startTag);
        this.pop();
        return el;
    }

    FormElement insertFormElement(Token.StartTag startTag, boolean onStack, boolean checkTemplateStack) {
        FormElement el = (FormElement)this.createElementFor(startTag, "http://www.w3.org/1999/xhtml", false);
        if (checkTemplateStack) {
            if (!this.onStack("template")) {
                this.setFormElement(el);
            }
        } else {
            this.setFormElement(el);
        }
        this.doInsertElement(el, startTag);
        if (!onStack) {
            this.pop();
        }
        return el;
    }

    private void doInsertElement(Element el, @Nullable Token token) {
        if (el.tag().isFormListed() && this.formElement != null) {
            this.formElement.addElement(el);
        }
        if (el.hasAttr("xmlns") && !el.attr("xmlns").equals(el.tag().namespace())) {
            this.error("Invalid xmlns attribute [%s] on tag [%s]", el.attr("xmlns"), el.tagName());
        }
        if (this.isFosterInserts() && StringUtil.inSorted(this.currentElement().normalName(), HtmlTreeBuilderState.Constants.InTableFoster)) {
            this.insertInFosterParent(el);
        } else {
            this.currentElement().appendChild(el);
        }
        this.push(el);
    }

    void insertCommentNode(Token.Comment token) {
        Comment node = new Comment(token.getData());
        this.currentElement().appendChild(node);
        this.onNodeInserted(node);
    }

    void insertCharacterNode(Token.Character characterToken) {
        Element el = this.currentElement();
        this.insertCharacterToElement(characterToken, el);
    }

    void insertCharacterToElement(Token.Character characterToken, Element el) {
        String tagName = el.normalName();
        String data = characterToken.getData();
        LeafNode node = characterToken.isCData() ? new CDataNode(data) : (this.isContentForTagData(tagName) ? new DataNode(data) : new TextNode(data));
        el.appendChild(node);
        this.onNodeInserted(node);
    }

    ArrayList<Element> getStack() {
        return this.stack;
    }

    boolean onStack(Element el) {
        return HtmlTreeBuilder.onStack(this.stack, el);
    }

    boolean onStack(String elName) {
        return this.getFromStack(elName) != null;
    }

    private static boolean onStack(ArrayList<Element> queue, Element element) {
        int bottom = queue.size() - 1;
        int upper = bottom >= 256 ? bottom - 256 : 0;
        for (int pos = bottom; pos >= upper; --pos) {
            Element next = queue.get(pos);
            if (next != element) continue;
            return true;
        }
        return false;
    }

    @Nullable Element getFromStack(String elName) {
        int bottom = this.stack.size() - 1;
        int upper = bottom >= 256 ? bottom - 256 : 0;
        for (int pos = bottom; pos >= upper; --pos) {
            Element next = (Element)this.stack.get(pos);
            if (!next.elementIs(elName, "http://www.w3.org/1999/xhtml")) continue;
            return next;
        }
        return null;
    }

    boolean removeFromStack(Element el) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element next = (Element)this.stack.get(pos);
            if (next != el) continue;
            this.stack.remove(pos);
            this.onNodeClosed(el);
            return true;
        }
        return false;
    }

    @Nullable Element popStackToClose(String elName) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element el = this.pop();
            if (!el.elementIs(elName, "http://www.w3.org/1999/xhtml")) continue;
            return el;
        }
        return null;
    }

    @Nullable Element popStackToCloseAnyNamespace(String elName) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element el = this.pop();
            if (!el.nameIs(elName)) continue;
            return el;
        }
        return null;
    }

    void popStackToClose(String ... elNames) {
        Element el;
        for (int pos = this.stack.size() - 1; !(pos < 0 || StringUtil.inSorted((el = this.pop()).normalName(), elNames) && "http://www.w3.org/1999/xhtml".equals(el.tag().namespace())); --pos) {
        }
    }

    void clearStackToTableContext() {
        this.clearStackToContext("table", "template");
    }

    void clearStackToTableBodyContext() {
        this.clearStackToContext("tbody", "tfoot", "thead", "template");
    }

    void clearStackToTableRowContext() {
        this.clearStackToContext("tr", "template");
    }

    private void clearStackToContext(String ... nodeNames) {
        Element next;
        for (int pos = this.stack.size() - 1; !(pos < 0 || "http://www.w3.org/1999/xhtml".equals((next = (Element)this.stack.get(pos)).tag().namespace()) && (StringUtil.in(next.normalName(), nodeNames) || next.nameIs("html"))); --pos) {
            this.pop();
        }
    }

    @Nullable Element aboveOnStack(Element el) {
        assert (this.onStack(el));
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element next = (Element)this.stack.get(pos);
            if (next != el) continue;
            return (Element)this.stack.get(pos - 1);
        }
        return null;
    }

    void insertOnStackAfter(Element after, Element in) {
        int i = this.stack.lastIndexOf(after);
        Validate.isTrue(i != -1);
        this.stack.add(i + 1, in);
    }

    void replaceOnStack(Element out, Element in) {
        HtmlTreeBuilder.replaceInQueue(this.stack, out, in);
    }

    private static void replaceInQueue(ArrayList<Element> queue, Element out, Element in) {
        int i = queue.lastIndexOf(out);
        Validate.isTrue(i != -1);
        queue.set(i, in);
    }

    /*
     * Enabled aggressive block sorting
     */
    boolean resetInsertionMode() {
        boolean last = false;
        int bottom = this.stack.size() - 1;
        int upper = bottom >= 256 ? bottom - 256 : 0;
        HtmlTreeBuilderState origState = this.state;
        if (this.stack.size() == 0) {
            this.transition(HtmlTreeBuilderState.InBody);
        }
        block31: for (int pos = bottom; pos >= upper; --pos) {
            String name;
            Element node = (Element)this.stack.get(pos);
            if (pos == upper) {
                last = true;
                if (this.fragmentParsing) {
                    node = this.contextElement;
                }
            }
            String string = name = node != null ? node.normalName() : "";
            if (!"http://www.w3.org/1999/xhtml".equals(node.tag().namespace())) continue;
            switch (name) {
                case "select": {
                    this.transition(HtmlTreeBuilderState.InSelect);
                    break block31;
                }
                case "td": 
                case "th": {
                    if (last) break;
                    this.transition(HtmlTreeBuilderState.InCell);
                    break block31;
                }
                case "tr": {
                    this.transition(HtmlTreeBuilderState.InRow);
                    break block31;
                }
                case "tbody": 
                case "thead": 
                case "tfoot": {
                    this.transition(HtmlTreeBuilderState.InTableBody);
                    break block31;
                }
                case "caption": {
                    this.transition(HtmlTreeBuilderState.InCaption);
                    break block31;
                }
                case "colgroup": {
                    this.transition(HtmlTreeBuilderState.InColumnGroup);
                    break block31;
                }
                case "table": {
                    this.transition(HtmlTreeBuilderState.InTable);
                    break block31;
                }
                case "template": {
                    HtmlTreeBuilderState tmplState = this.currentTemplateMode();
                    Validate.notNull((Object)tmplState, "Bug: no template insertion mode on stack!");
                    this.transition(tmplState);
                    break block31;
                }
                case "head": {
                    if (last) break;
                    this.transition(HtmlTreeBuilderState.InHead);
                    break block31;
                }
                case "body": {
                    this.transition(HtmlTreeBuilderState.InBody);
                    break block31;
                }
                case "frameset": {
                    this.transition(HtmlTreeBuilderState.InFrameset);
                    break block31;
                }
                case "html": {
                    this.transition(this.headElement == null ? HtmlTreeBuilderState.BeforeHead : HtmlTreeBuilderState.AfterHead);
                    break block31;
                }
            }
            if (!last) continue;
            this.transition(HtmlTreeBuilderState.InBody);
            break;
        }
        if (this.state == origState) return false;
        return true;
    }

    void resetBody() {
        if (!this.onStack("body")) {
            this.stack.add(this.doc.body());
        }
        this.transition(HtmlTreeBuilderState.InBody);
    }

    private boolean inSpecificScope(String targetName, String[] baseTypes, String[] extraTypes) {
        this.specificScopeTarget[0] = targetName;
        return this.inSpecificScope(this.specificScopeTarget, baseTypes, extraTypes);
    }

    private boolean inSpecificScope(String[] targetNames, String[] baseTypes, @Nullable String[] extraTypes) {
        int bottom = this.stack.size() - 1;
        int top = bottom > 100 ? bottom - 100 : 0;
        for (int pos = bottom; pos >= top; --pos) {
            Element el = (Element)this.stack.get(pos);
            if (!el.tag().namespace().equals("http://www.w3.org/1999/xhtml")) continue;
            String elName = el.normalName();
            if (StringUtil.inSorted(elName, targetNames)) {
                return true;
            }
            if (StringUtil.inSorted(elName, baseTypes)) {
                return false;
            }
            if (extraTypes == null || !StringUtil.inSorted(elName, extraTypes)) continue;
            return false;
        }
        return false;
    }

    boolean inScope(String[] targetNames) {
        return this.inSpecificScope(targetNames, TagsSearchInScope, null);
    }

    boolean inScope(String targetName) {
        return this.inScope(targetName, null);
    }

    boolean inScope(String targetName, String[] extras) {
        return this.inSpecificScope(targetName, TagsSearchInScope, extras);
    }

    boolean inListItemScope(String targetName) {
        return this.inScope(targetName, TagSearchList);
    }

    boolean inButtonScope(String targetName) {
        return this.inScope(targetName, TagSearchButton);
    }

    boolean inTableScope(String targetName) {
        return this.inSpecificScope(targetName, TagSearchTableScope, null);
    }

    boolean inSelectScope(String targetName) {
        for (int pos = this.stack.size() - 1; pos >= 0; --pos) {
            Element el = (Element)this.stack.get(pos);
            String elName = el.normalName();
            if (elName.equals(targetName)) {
                return true;
            }
            if (StringUtil.inSorted(elName, TagSearchSelectScope)) continue;
            return false;
        }
        Validate.fail("Should not be reachable");
        return false;
    }

    boolean onStackNot(String[] allowedTags) {
        int bottom = this.stack.size() - 1;
        int top = bottom > 100 ? bottom - 100 : 0;
        for (int pos = bottom; pos >= top; --pos) {
            String elName = ((Element)this.stack.get(pos)).normalName();
            if (StringUtil.inSorted(elName, allowedTags)) continue;
            return true;
        }
        return false;
    }

    void setHeadElement(Element headElement) {
        this.headElement = headElement;
    }

    Element getHeadElement() {
        return this.headElement;
    }

    boolean isFosterInserts() {
        return this.fosterInserts;
    }

    void setFosterInserts(boolean fosterInserts) {
        this.fosterInserts = fosterInserts;
    }

    @Nullable FormElement getFormElement() {
        return this.formElement;
    }

    void setFormElement(FormElement formElement) {
        this.formElement = formElement;
    }

    void resetPendingTableCharacters() {
        this.pendingTableCharacters.clear();
    }

    List<Token.Character> getPendingTableCharacters() {
        return this.pendingTableCharacters;
    }

    void addPendingTableCharacters(Token.Character c2) {
        Token.Character clone = c2.clone();
        this.pendingTableCharacters.add(clone);
    }

    void generateImpliedEndTags(String excludeTag) {
        while (StringUtil.inSorted(this.currentElement().normalName(), TagSearchEndTags) && (excludeTag == null || !this.currentElementIs(excludeTag))) {
            this.pop();
        }
    }

    void generateImpliedEndTags() {
        this.generateImpliedEndTags(false);
    }

    void generateImpliedEndTags(boolean thorough) {
        String[] search;
        String[] stringArray = search = thorough ? TagThoroughSearchEndTags : TagSearchEndTags;
        while ("http://www.w3.org/1999/xhtml".equals(this.currentElement().tag().namespace()) && StringUtil.inSorted(this.currentElement().normalName(), search)) {
            this.pop();
        }
    }

    void closeElement(String name) {
        this.generateImpliedEndTags(name);
        if (!name.equals(this.currentElement().normalName())) {
            this.error(this.state());
        }
        this.popStackToClose(name);
    }

    static boolean isSpecial(Element el) {
        String name = el.normalName();
        return StringUtil.inSorted(name, TagSearchSpecial);
    }

    Element lastFormattingElement() {
        return this.formattingElements.size() > 0 ? this.formattingElements.get(this.formattingElements.size() - 1) : null;
    }

    int positionOfElement(Element el) {
        for (int i = 0; i < this.formattingElements.size(); ++i) {
            if (el != this.formattingElements.get(i)) continue;
            return i;
        }
        return -1;
    }

    Element removeLastFormattingElement() {
        int size = this.formattingElements.size();
        if (size > 0) {
            return this.formattingElements.remove(size - 1);
        }
        return null;
    }

    void pushActiveFormattingElements(Element in) {
        this.checkActiveFormattingElements(in);
        this.formattingElements.add(in);
    }

    void pushWithBookmark(Element in, int bookmark) {
        this.checkActiveFormattingElements(in);
        try {
            this.formattingElements.add(bookmark, in);
        }
        catch (IndexOutOfBoundsException e) {
            this.formattingElements.add(in);
        }
    }

    void checkActiveFormattingElements(Element in) {
        Element el;
        int numSeen = 0;
        int size = this.formattingElements.size() - 1;
        int ceil = size - 12;
        if (ceil < 0) {
            ceil = 0;
        }
        for (int pos = size; pos >= ceil && (el = this.formattingElements.get(pos)) != null; --pos) {
            if (HtmlTreeBuilder.isSameFormattingElement(in, el)) {
                ++numSeen;
            }
            if (numSeen != 3) continue;
            this.formattingElements.remove(pos);
            break;
        }
    }

    private static boolean isSameFormattingElement(Element a, Element b2) {
        return a.normalName().equals(b2.normalName()) && a.attributes().equals(b2.attributes());
    }

    void reconstructFormattingElements() {
        if (this.stack.size() > 256) {
            return;
        }
        Element last = this.lastFormattingElement();
        if (last == null || this.onStack(last)) {
            return;
        }
        Element entry = last;
        int size = this.formattingElements.size();
        int ceil = size - 12;
        if (ceil < 0) {
            ceil = 0;
        }
        int pos = size - 1;
        boolean skip = false;
        do {
            if (pos != ceil) continue;
            skip = true;
            break;
        } while ((entry = this.formattingElements.get(--pos)) != null && !this.onStack(entry));
        do {
            if (!skip) {
                entry = this.formattingElements.get(++pos);
            }
            Validate.notNull(entry);
            skip = false;
            Element newEl = new Element(this.tagFor(entry.normalName(), this.settings), null, entry.attributes().clone());
            this.doInsertElement(newEl, null);
            this.formattingElements.set(pos, newEl);
        } while (pos != size - 1);
    }

    void clearFormattingElementsToLastMarker() {
        Element el;
        while (!this.formattingElements.isEmpty() && (el = this.removeLastFormattingElement()) != null) {
        }
    }

    void removeFromActiveFormattingElements(Element el) {
        for (int pos = this.formattingElements.size() - 1; pos >= 0; --pos) {
            Element next = this.formattingElements.get(pos);
            if (next != el) continue;
            this.formattingElements.remove(pos);
            break;
        }
    }

    boolean isInActiveFormattingElements(Element el) {
        return HtmlTreeBuilder.onStack(this.formattingElements, el);
    }

    @Nullable Element getActiveFormattingElement(String nodeName) {
        Element next;
        for (int pos = this.formattingElements.size() - 1; pos >= 0 && (next = this.formattingElements.get(pos)) != null; --pos) {
            if (!next.nameIs(nodeName)) continue;
            return next;
        }
        return null;
    }

    void replaceActiveFormattingElement(Element out, Element in) {
        HtmlTreeBuilder.replaceInQueue(this.formattingElements, out, in);
    }

    void insertMarkerToFormattingElements() {
        this.formattingElements.add(null);
    }

    void insertInFosterParent(Node in) {
        Element fosterParent;
        Element lastTable = this.getFromStack("table");
        boolean isLastTableParent = false;
        if (lastTable != null) {
            if (lastTable.parent() != null) {
                fosterParent = lastTable.parent();
                isLastTableParent = true;
            } else {
                fosterParent = this.aboveOnStack(lastTable);
            }
        } else {
            fosterParent = (Element)this.stack.get(0);
        }
        if (isLastTableParent) {
            Validate.notNull(lastTable);
            lastTable.before(in);
        } else {
            fosterParent.appendChild(in);
        }
    }

    void pushTemplateMode(HtmlTreeBuilderState state) {
        this.tmplInsertMode.add(state);
    }

    @Nullable HtmlTreeBuilderState popTemplateMode() {
        if (this.tmplInsertMode.size() > 0) {
            return this.tmplInsertMode.remove(this.tmplInsertMode.size() - 1);
        }
        return null;
    }

    int templateModeSize() {
        return this.tmplInsertMode.size();
    }

    @Nullable HtmlTreeBuilderState currentTemplateMode() {
        return this.tmplInsertMode.size() > 0 ? this.tmplInsertMode.get(this.tmplInsertMode.size() - 1) : null;
    }

    public String toString() {
        return "TreeBuilder{currentToken=" + this.currentToken + ", state=" + (Object)((Object)this.state) + ", currentElement=" + this.currentElement() + '}';
    }

    @Override
    protected boolean isContentForTagData(String normalName) {
        return normalName.equals("script") || normalName.equals("style");
    }
}

