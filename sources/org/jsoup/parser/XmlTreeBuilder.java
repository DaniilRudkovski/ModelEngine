/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.CDataNode;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.LeafNode;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.nodes.XmlDeclaration;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.Parser;
import org.jsoup.parser.Tag;
import org.jsoup.parser.Token;
import org.jsoup.parser.TreeBuilder;

public class XmlTreeBuilder
extends TreeBuilder {
    private static final int maxQueueDepth = 256;

    @Override
    ParseSettings defaultSettings() {
        return ParseSettings.preserveCase;
    }

    @Override
    protected void initialiseParse(Reader input, String baseUri, Parser parser) {
        super.initialiseParse(input, baseUri, parser);
        this.stack.add(this.doc);
        this.doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml).escapeMode(Entities.EscapeMode.xhtml).prettyPrint(false);
    }

    Document parse(Reader input, String baseUri) {
        return this.parse(input, baseUri, new Parser(this));
    }

    Document parse(String input, String baseUri) {
        return this.parse(new StringReader(input), baseUri, new Parser(this));
    }

    @Override
    XmlTreeBuilder newInstance() {
        return new XmlTreeBuilder();
    }

    @Override
    public String defaultNamespace() {
        return "http://www.w3.org/XML/1998/namespace";
    }

    @Override
    protected boolean process(Token token) {
        this.currentToken = token;
        switch (token.type) {
            case StartTag: {
                this.insertElementFor(token.asStartTag());
                break;
            }
            case EndTag: {
                this.popStackToClose(token.asEndTag());
                break;
            }
            case Comment: {
                this.insertCommentFor(token.asComment());
                break;
            }
            case Character: {
                this.insertCharacterFor(token.asCharacter());
                break;
            }
            case Doctype: {
                this.insertDoctypeFor(token.asDoctype());
                break;
            }
            case EOF: {
                break;
            }
            default: {
                Validate.fail("Unexpected token type: " + (Object)((Object)token.type));
            }
        }
        return true;
    }

    void insertElementFor(Token.StartTag startTag) {
        Tag tag = this.tagFor(startTag.name(), this.settings);
        if (startTag.attributes != null) {
            startTag.attributes.deduplicate(this.settings);
        }
        Element el = new Element(tag, null, this.settings.normalizeAttributes(startTag.attributes));
        this.currentElement().appendChild(el);
        this.push(el);
        if (startTag.isSelfClosing()) {
            tag.setSelfClosing();
            this.pop();
        }
    }

    void insertLeafNode(LeafNode node) {
        this.currentElement().appendChild(node);
        this.onNodeInserted(node);
    }

    void insertCommentFor(Token.Comment commentToken) {
        XmlDeclaration decl;
        Comment comment;
        LeafNode insert = comment = new Comment(commentToken.getData());
        if (commentToken.bogus && comment.isXmlDeclaration() && (decl = comment.asXmlDeclaration()) != null) {
            insert = decl;
        }
        this.insertLeafNode(insert);
    }

    void insertCharacterFor(Token.Character token) {
        String data = token.getData();
        this.insertLeafNode(token.isCData() ? new CDataNode(data) : new TextNode(data));
    }

    void insertDoctypeFor(Token.Doctype token) {
        DocumentType doctypeNode = new DocumentType(this.settings.normalizeTag(token.getName()), token.getPublicIdentifier(), token.getSystemIdentifier());
        doctypeNode.setPubSysKey(token.getPubSysKey());
        this.insertLeafNode(doctypeNode);
    }

    @Deprecated
    protected void insertNode(Node node) {
        this.currentElement().appendChild(node);
        this.onNodeInserted(node);
    }

    @Deprecated
    protected void insertNode(Node node, Token token) {
        this.currentElement().appendChild(node);
        this.onNodeInserted(node);
    }

    protected void popStackToClose(Token.EndTag endTag) {
        Element next;
        int pos;
        String elName = this.settings.normalizeTag(endTag.tagName);
        Element firstFound = null;
        int bottom = this.stack.size() - 1;
        int upper = bottom >= 256 ? bottom - 256 : 0;
        for (pos = this.stack.size() - 1; pos >= upper; --pos) {
            next = (Element)this.stack.get(pos);
            if (!next.nodeName().equals(elName)) continue;
            firstFound = next;
            break;
        }
        if (firstFound == null) {
            return;
        }
        for (pos = this.stack.size() - 1; pos >= 0 && (next = this.pop()) != firstFound; --pos) {
        }
    }

    List<Node> parseFragment(String inputFragment, String baseUri, Parser parser) {
        this.initialiseParse(new StringReader(inputFragment), baseUri, parser);
        this.runParser();
        return this.doc.childNodes();
    }

    @Override
    List<Node> parseFragment(String inputFragment, Element context, String baseUri, Parser parser) {
        return this.parseFragment(inputFragment, baseUri, parser);
    }
}

