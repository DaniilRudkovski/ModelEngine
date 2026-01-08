/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.jsoup.nodes;

import java.util.Iterator;
import java.util.NoSuchElementException;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Node;
import org.jspecify.annotations.Nullable;

public class NodeIterator<T extends Node>
implements Iterator<T> {
    private Node root;
    private @Nullable T next;
    private Node current;
    private Node previous;
    private @Nullable Node currentParent;
    private final Class<T> type;

    public NodeIterator(Node start, Class<T> type) {
        Validate.notNull(start);
        Validate.notNull(type);
        this.type = type;
        this.restart(start);
    }

    public static NodeIterator<Node> from(Node start) {
        return new NodeIterator<Node>(start, Node.class);
    }

    public void restart(Node start) {
        if (this.type.isInstance(start)) {
            this.next = start;
        }
        this.previous = this.current = start;
        this.root = this.current;
        this.currentParent = this.current.parent();
    }

    @Override
    public boolean hasNext() {
        this.maybeFindNext();
        return this.next != null;
    }

    @Override
    public T next() {
        this.maybeFindNext();
        if (this.next == null) {
            throw new NoSuchElementException();
        }
        T result = this.next;
        this.previous = this.current;
        this.current = this.next;
        this.currentParent = this.current.parent();
        this.next = null;
        return result;
    }

    private void maybeFindNext() {
        if (this.next != null) {
            return;
        }
        if (this.currentParent != null && !this.current.hasParent()) {
            this.current = this.previous;
        }
        this.next = this.findNextNode();
    }

    private @Nullable T findNextNode() {
        Node node = this.current;
        do {
            if (node.childNodeSize() > 0) {
                node = node.childNode(0);
            } else if (this.root.equals(node)) {
                node = null;
            } else if (node.nextSibling() != null) {
                node = node.nextSibling();
            } else {
                do {
                    if ((node = node.parent()) != null && !this.root.equals(node)) continue;
                    return null;
                } while (node.nextSibling() == null);
                node = node.nextSibling();
            }
            if (node != null) continue;
            return null;
        } while (!this.type.isInstance(node));
        return (T)node;
    }

    @Override
    public void remove() {
        this.current.remove();
    }
}

