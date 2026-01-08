/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jspecify.annotations.Nullable
 */
package org.jsoup.parser;

import java.util.HashMap;
import org.jsoup.helper.Validate;
import org.jsoup.internal.Normalizer;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Range;
import org.jsoup.parser.CharacterReader;
import org.jsoup.parser.ParseSettings;
import org.jsoup.parser.TreeBuilder;
import org.jspecify.annotations.Nullable;

abstract class Token {
    final TokenType type;
    static final int Unset = -1;
    private int startPos;
    private int endPos = -1;

    private Token(TokenType type) {
        this.type = type;
    }

    String tokenType() {
        return this.getClass().getSimpleName();
    }

    Token reset() {
        this.startPos = -1;
        this.endPos = -1;
        return this;
    }

    int startPos() {
        return this.startPos;
    }

    void startPos(int pos) {
        this.startPos = pos;
    }

    int endPos() {
        return this.endPos;
    }

    void endPos(int pos) {
        this.endPos = pos;
    }

    static void reset(StringBuilder sb) {
        if (sb != null) {
            sb.delete(0, sb.length());
        }
    }

    final boolean isDoctype() {
        return this.type == TokenType.Doctype;
    }

    final Doctype asDoctype() {
        return (Doctype)this;
    }

    final boolean isStartTag() {
        return this.type == TokenType.StartTag;
    }

    final StartTag asStartTag() {
        return (StartTag)this;
    }

    final boolean isEndTag() {
        return this.type == TokenType.EndTag;
    }

    final EndTag asEndTag() {
        return (EndTag)this;
    }

    final boolean isComment() {
        return this.type == TokenType.Comment;
    }

    final Comment asComment() {
        return (Comment)this;
    }

    final boolean isCharacter() {
        return this.type == TokenType.Character;
    }

    final boolean isCData() {
        return this instanceof CData;
    }

    final Character asCharacter() {
        return (Character)this;
    }

    final boolean isEOF() {
        return this.type == TokenType.EOF;
    }

    public static enum TokenType {
        Doctype,
        StartTag,
        EndTag,
        Comment,
        Character,
        EOF;

    }

    static final class Doctype
    extends Token {
        final StringBuilder name = new StringBuilder();
        String pubSysKey = null;
        final StringBuilder publicIdentifier = new StringBuilder();
        final StringBuilder systemIdentifier = new StringBuilder();
        boolean forceQuirks = false;

        Doctype() {
            super(TokenType.Doctype);
        }

        @Override
        Token reset() {
            super.reset();
            Doctype.reset(this.name);
            this.pubSysKey = null;
            Doctype.reset(this.publicIdentifier);
            Doctype.reset(this.systemIdentifier);
            this.forceQuirks = false;
            return this;
        }

        String getName() {
            return this.name.toString();
        }

        String getPubSysKey() {
            return this.pubSysKey;
        }

        String getPublicIdentifier() {
            return this.publicIdentifier.toString();
        }

        public String getSystemIdentifier() {
            return this.systemIdentifier.toString();
        }

        public boolean isForceQuirks() {
            return this.forceQuirks;
        }

        public String toString() {
            return "<!doctype " + this.getName() + ">";
        }
    }

    static final class StartTag
    extends Tag {
        StartTag(TreeBuilder treeBuilder) {
            super(TokenType.StartTag, treeBuilder);
        }

        @Override
        Tag reset() {
            super.reset();
            this.attributes = null;
            return this;
        }

        StartTag nameAttr(String name, Attributes attributes) {
            this.tagName = name;
            this.attributes = attributes;
            this.normalName = ParseSettings.normalName(this.tagName);
            return this;
        }

        @Override
        public String toString() {
            String closer;
            String string = closer = this.isSelfClosing() ? "/>" : ">";
            if (this.hasAttributes() && this.attributes.size() > 0) {
                return "<" + this.toStringName() + " " + this.attributes.toString() + closer;
            }
            return "<" + this.toStringName() + closer;
        }
    }

    static final class EndTag
    extends Tag {
        EndTag(TreeBuilder treeBuilder) {
            super(TokenType.EndTag, treeBuilder);
        }

        @Override
        public String toString() {
            return "</" + this.toStringName() + ">";
        }
    }

    static final class Comment
    extends Token {
        private final StringBuilder data = new StringBuilder();
        private String dataS;
        boolean bogus = false;

        @Override
        Token reset() {
            super.reset();
            Comment.reset(this.data);
            this.dataS = null;
            this.bogus = false;
            return this;
        }

        Comment() {
            super(TokenType.Comment);
        }

        String getData() {
            return this.dataS != null ? this.dataS : this.data.toString();
        }

        Comment append(String append) {
            this.ensureData();
            if (this.data.length() == 0) {
                this.dataS = append;
            } else {
                this.data.append(append);
            }
            return this;
        }

        Comment append(char append) {
            this.ensureData();
            this.data.append(append);
            return this;
        }

        private void ensureData() {
            if (this.dataS != null) {
                this.data.append(this.dataS);
                this.dataS = null;
            }
        }

        public String toString() {
            return "<!--" + this.getData() + "-->";
        }
    }

    static final class CData
    extends Character {
        CData(String data) {
            this.data(data);
        }

        @Override
        public String toString() {
            return "<![CDATA[" + this.getData() + "]]>";
        }
    }

    static class Character
    extends Token
    implements Cloneable {
        private String data;

        Character() {
            super(TokenType.Character);
        }

        @Override
        Token reset() {
            super.reset();
            this.data = null;
            return this;
        }

        Character data(String data) {
            this.data = data;
            return this;
        }

        String getData() {
            return this.data;
        }

        public String toString() {
            return this.getData();
        }

        protected Character clone() {
            try {
                return (Character)super.clone();
            }
            catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    static final class EOF
    extends Token {
        EOF() {
            super(TokenType.EOF);
        }

        @Override
        Token reset() {
            super.reset();
            return this;
        }

        public String toString() {
            return "";
        }
    }

    static abstract class Tag
    extends Token {
        protected @Nullable String tagName;
        protected @Nullable String normalName;
        boolean selfClosing = false;
        @Nullable Attributes attributes;
        private @Nullable String attrName;
        private final StringBuilder attrNameSb = new StringBuilder();
        private boolean hasAttrName = false;
        private @Nullable String attrValue;
        private final StringBuilder attrValueSb = new StringBuilder();
        private boolean hasAttrValue = false;
        private boolean hasEmptyAttrValue = false;
        final TreeBuilder treeBuilder;
        final boolean trackSource;
        int attrNameStart;
        int attrNameEnd;
        int attrValStart;
        int attrValEnd;
        private static final int MaxAttributes = 512;

        Tag(TokenType type, TreeBuilder treeBuilder) {
            super(type);
            this.treeBuilder = treeBuilder;
            this.trackSource = treeBuilder.trackSourceRange;
        }

        @Override
        Tag reset() {
            super.reset();
            this.tagName = null;
            this.normalName = null;
            this.selfClosing = false;
            this.attributes = null;
            this.resetPendingAttr();
            return this;
        }

        private void resetPendingAttr() {
            Tag.reset(this.attrNameSb);
            this.attrName = null;
            this.hasAttrName = false;
            Tag.reset(this.attrValueSb);
            this.attrValue = null;
            this.hasEmptyAttrValue = false;
            this.hasAttrValue = false;
            if (this.trackSource) {
                this.attrValEnd = -1;
                this.attrValStart = -1;
                this.attrNameEnd = -1;
                this.attrNameStart = -1;
            }
        }

        final void newAttribute() {
            if (this.attributes == null) {
                this.attributes = new Attributes();
            }
            if (this.hasAttrName && this.attributes.size() < 512) {
                String name = this.attrNameSb.length() > 0 ? this.attrNameSb.toString() : this.attrName;
                if ((name = name.trim()).length() > 0) {
                    String value = this.hasAttrValue ? (this.attrValueSb.length() > 0 ? this.attrValueSb.toString() : this.attrValue) : (this.hasEmptyAttrValue ? "" : null);
                    this.attributes.add(name, value);
                    this.trackAttributeRange(name);
                }
            }
            this.resetPendingAttr();
        }

        private void trackAttributeRange(String name) {
            if (this.trackSource && this.isStartTag()) {
                StartTag start = this.asStartTag();
                CharacterReader r = start.treeBuilder.reader;
                boolean preserve = start.treeBuilder.settings.preserveAttributeCase();
                assert (this.attributes != null);
                HashMap<String, Range.AttributeRange> attrRanges = (HashMap<String, Range.AttributeRange>)this.attributes.userData("jsoup.attrs");
                if (attrRanges == null) {
                    attrRanges = new HashMap<String, Range.AttributeRange>();
                    this.attributes.userData("jsoup.attrs", attrRanges);
                }
                if (!preserve) {
                    name = Normalizer.lowerCase(name);
                }
                if (attrRanges.containsKey(name)) {
                    return;
                }
                if (!this.hasAttrValue) {
                    this.attrValStart = this.attrValEnd = this.attrNameEnd;
                }
                Range.AttributeRange range = new Range.AttributeRange(new Range(new Range.Position(this.attrNameStart, r.lineNumber(this.attrNameStart), r.columnNumber(this.attrNameStart)), new Range.Position(this.attrNameEnd, r.lineNumber(this.attrNameEnd), r.columnNumber(this.attrNameEnd))), new Range(new Range.Position(this.attrValStart, r.lineNumber(this.attrValStart), r.columnNumber(this.attrValStart)), new Range.Position(this.attrValEnd, r.lineNumber(this.attrValEnd), r.columnNumber(this.attrValEnd))));
                attrRanges.put(name, range);
            }
        }

        final boolean hasAttributes() {
            return this.attributes != null;
        }

        final boolean hasAttribute(String key) {
            return this.attributes != null && this.attributes.hasKey(key);
        }

        final boolean hasAttributeIgnoreCase(String key) {
            return this.attributes != null && this.attributes.hasKeyIgnoreCase(key);
        }

        final void finaliseTag() {
            if (this.hasAttrName) {
                this.newAttribute();
            }
        }

        final String name() {
            Validate.isFalse(this.tagName == null || this.tagName.length() == 0);
            return this.tagName;
        }

        final String normalName() {
            return this.normalName;
        }

        final String toStringName() {
            return this.tagName != null ? this.tagName : "[unset]";
        }

        final Tag name(String name) {
            this.tagName = name;
            this.normalName = ParseSettings.normalName(this.tagName);
            return this;
        }

        final boolean isSelfClosing() {
            return this.selfClosing;
        }

        final void appendTagName(String append) {
            append = append.replace('\u0000', '\ufffd');
            this.tagName = this.tagName == null ? append : this.tagName.concat(append);
            this.normalName = ParseSettings.normalName(this.tagName);
        }

        final void appendTagName(char append) {
            this.appendTagName(String.valueOf(append));
        }

        final void appendAttributeName(String append, int startPos, int endPos) {
            append = append.replace('\u0000', '\ufffd');
            this.ensureAttrName(startPos, endPos);
            if (this.attrNameSb.length() == 0) {
                this.attrName = append;
            } else {
                this.attrNameSb.append(append);
            }
        }

        final void appendAttributeName(char append, int startPos, int endPos) {
            this.ensureAttrName(startPos, endPos);
            this.attrNameSb.append(append);
        }

        final void appendAttributeValue(String append, int startPos, int endPos) {
            this.ensureAttrValue(startPos, endPos);
            if (this.attrValueSb.length() == 0) {
                this.attrValue = append;
            } else {
                this.attrValueSb.append(append);
            }
        }

        final void appendAttributeValue(char append, int startPos, int endPos) {
            this.ensureAttrValue(startPos, endPos);
            this.attrValueSb.append(append);
        }

        final void appendAttributeValue(int[] appendCodepoints, int startPos, int endPos) {
            this.ensureAttrValue(startPos, endPos);
            for (int codepoint : appendCodepoints) {
                this.attrValueSb.appendCodePoint(codepoint);
            }
        }

        final void setEmptyAttributeValue() {
            this.hasEmptyAttrValue = true;
        }

        private void ensureAttrName(int startPos, int endPos) {
            this.hasAttrName = true;
            if (this.attrName != null) {
                this.attrNameSb.append(this.attrName);
                this.attrName = null;
            }
            if (this.trackSource) {
                this.attrNameStart = this.attrNameStart > -1 ? this.attrNameStart : startPos;
                this.attrNameEnd = endPos;
            }
        }

        private void ensureAttrValue(int startPos, int endPos) {
            this.hasAttrValue = true;
            if (this.attrValue != null) {
                this.attrValueSb.append(this.attrValue);
                this.attrValue = null;
            }
            if (this.trackSource) {
                this.attrValStart = this.attrValStart > -1 ? this.attrValStart : startPos;
                this.attrValEnd = endPos;
            }
        }

        public abstract String toString();
    }
}

