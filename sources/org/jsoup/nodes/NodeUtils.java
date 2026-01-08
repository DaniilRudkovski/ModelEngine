/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.jsoup.helper.Validate;
import org.jsoup.helper.W3CDom;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.NodeIterator;
import org.jsoup.parser.HtmlTreeBuilder;
import org.jsoup.parser.Parser;
import org.w3c.dom.NodeList;

final class NodeUtils {
    NodeUtils() {
    }

    static Document.OutputSettings outputSettings(Node node) {
        Document owner = node.ownerDocument();
        return owner != null ? owner.outputSettings() : new Document("").outputSettings();
    }

    static Parser parser(Node node) {
        Document doc = node.ownerDocument();
        return doc != null && doc.parser() != null ? doc.parser() : new Parser(new HtmlTreeBuilder());
    }

    static <T extends Node> List<T> selectXpath(String xpath, Element el, Class<T> nodeType) {
        Validate.notEmpty(xpath);
        Validate.notNull(el);
        Validate.notNull(nodeType);
        W3CDom w3c = new W3CDom().namespaceAware(false);
        org.w3c.dom.Document wDoc = w3c.fromJsoup(el);
        org.w3c.dom.Node contextNode = w3c.contextNode(wDoc);
        NodeList nodeList = w3c.selectXpath(xpath, contextNode);
        return w3c.sourceNodes(nodeList, nodeType);
    }

    static <T extends Node> Stream<T> stream(Node start, Class<T> type) {
        NodeIterator<T> iterator = new NodeIterator<T>(start, type);
        Spliterator<T> spliterator = NodeUtils.spliterator(iterator);
        return StreamSupport.stream(spliterator, false);
    }

    static <T extends Node> Spliterator<T> spliterator(Iterator<T> iterator) {
        return Spliterators.spliteratorUnknownSize(iterator, 273);
    }
}

