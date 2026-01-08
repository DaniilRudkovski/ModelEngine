/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.HtmlTreeBuilder;
import org.jsoup.parser.ParseErrorList;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Tokeniser;
import org.jsoup.parser.TreeBuilder;
import org.jsoup.parser.XmlTreeBuilder;

public class Parser {
    public static final String NamespaceHtml = "http://www.w3.org/1999/xhtml";
    public static final String NamespaceXml = "http://www.w3.org/XML/1998/namespace";
    public static final String NamespaceMathml = "http://www.w3.org/1998/Math/MathML";
    public static final String NamespaceSvg = "http://www.w3.org/2000/svg";
    private TreeBuilder treeBuilder;
    private ParseErrorList errors;
    private ParseSettings settings;
    private boolean trackPosition = false;

    public Parser(TreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
        this.settings = treeBuilder.defaultSettings();
        this.errors = ParseErrorList.noTracking();
    }

    public Parser newInstance() {
        return new Parser(this);
    }

    private Parser(Parser copy) {
        this.treeBuilder = copy.treeBuilder.newInstance();
        this.errors = new ParseErrorList(copy.errors);
        this.settings = new ParseSettings(copy.settings);
        this.trackPosition = copy.trackPosition;
    }

    public Document parseInput(String html, String baseUri) {
        return this.treeBuilder.parse(new StringReader(html), baseUri, this);
    }

    public Document parseInput(Reader inputHtml, String baseUri) {
        return this.treeBuilder.parse(inputHtml, baseUri, this);
    }

    public List<Node> parseFragmentInput(String fragment, Element context, String baseUri) {
        return this.treeBuilder.parseFragment(fragment, context, baseUri, this);
    }

    public TreeBuilder getTreeBuilder() {
        return this.treeBuilder;
    }

    public Parser setTreeBuilder(TreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
        treeBuilder.parser = this;
        return this;
    }

    public boolean isTrackErrors() {
        return this.errors.getMaxSize() > 0;
    }

    public Parser setTrackErrors(int maxErrors) {
        this.errors = maxErrors > 0 ? ParseErrorList.tracking(maxErrors) : ParseErrorList.noTracking();
        return this;
    }

    public ParseErrorList getErrors() {
        return this.errors;
    }

    public boolean isTrackPosition() {
        return this.trackPosition;
    }

    public Parser setTrackPosition(boolean trackPosition) {
        this.trackPosition = trackPosition;
        return this;
    }

    public Parser settings(ParseSettings settings) {
        this.settings = settings;
        return this;
    }

    public ParseSettings settings() {
        return this.settings;
    }

    public boolean isContentForTagData(String normalName) {
        return this.getTreeBuilder().isContentForTagData(normalName);
    }

    public String defaultNamespace() {
        return this.getTreeBuilder().defaultNamespace();
    }

    public static Document parse(String html, String baseUri) {
        HtmlTreeBuilder treeBuilder = new HtmlTreeBuilder();
        return treeBuilder.parse(new StringReader(html), baseUri, new Parser(treeBuilder));
    }

    public static List<Node> parseFragment(String fragmentHtml, Element context, String baseUri) {
        HtmlTreeBuilder treeBuilder = new HtmlTreeBuilder();
        return treeBuilder.parseFragment(fragmentHtml, context, baseUri, new Parser(treeBuilder));
    }

    public static List<Node> parseFragment(String fragmentHtml, Element context, String baseUri, ParseErrorList errorList) {
        HtmlTreeBuilder treeBuilder = new HtmlTreeBuilder();
        Parser parser = new Parser(treeBuilder);
        parser.errors = errorList;
        return treeBuilder.parseFragment(fragmentHtml, context, baseUri, parser);
    }

    public static List<Node> parseXmlFragment(String fragmentXml, String baseUri) {
        XmlTreeBuilder treeBuilder = new XmlTreeBuilder();
        return treeBuilder.parseFragment(fragmentXml, baseUri, new Parser(treeBuilder));
    }

    public static Document parseBodyFragment(String bodyHtml, String baseUri) {
        Document doc = Document.createShell(baseUri);
        Element body = doc.body();
        List<Node> nodeList = Parser.parseFragment(bodyHtml, body, baseUri);
        Node[] nodes = nodeList.toArray(new Node[0]);
        for (int i = nodes.length - 1; i > 0; --i) {
            nodes[i].remove();
        }
        for (Node node : nodes) {
            body.appendChild(node);
        }
        return doc;
    }

    public static String unescapeEntities(String string, boolean inAttribute) {
        Parser parser = Parser.htmlParser();
        parser.treeBuilder.initialiseParse(new StringReader(string), "", parser);
        Tokeniser tokeniser = new Tokeniser(parser.treeBuilder);
        return tokeniser.unescapeEntities(inAttribute);
    }

    public static Parser htmlParser() {
        return new Parser(new HtmlTreeBuilder());
    }

    public static Parser xmlParser() {
        return new Parser(new XmlTreeBuilder());
    }
}

