/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import java.io.IOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.LeafNode;

public class DataNode
extends LeafNode {
    public DataNode(String data) {
        this.value = data;
    }

    @Override
    public String nodeName() {
        return "#data";
    }

    public String getWholeData() {
        return this.coreValue();
    }

    public DataNode setWholeData(String data) {
        this.coreValue(data);
        return this;
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        String data = this.getWholeData();
        if (out.syntax() == Document.OutputSettings.Syntax.xml && !data.contains("<![CDATA[")) {
            if (this.parentNameIs("script")) {
                accum.append("//<![CDATA[\n").append(data).append("\n//]]>");
            } else if (this.parentNameIs("style")) {
                accum.append("/*<![CDATA[*/\n").append(data).append("\n/*]]>*/");
            } else {
                accum.append("<![CDATA[").append(data).append("]]>");
            }
        } else {
            accum.append(this.getWholeData());
        }
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }

    @Override
    public String toString() {
        return this.outerHtml();
    }

    @Override
    public DataNode clone() {
        return (DataNode)super.clone();
    }
}

