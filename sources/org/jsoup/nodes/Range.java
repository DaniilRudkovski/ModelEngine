/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.nodes;

import org.jsoup.nodes.Node;

public class Range {
    private static final Position UntrackedPos = new Position(-1, -1, -1);
    private final Position start;
    private final Position end;
    static final Range Untracked = new Range(UntrackedPos, UntrackedPos);

    public Range(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    public Position start() {
        return this.start;
    }

    public int startPos() {
        return this.start.pos;
    }

    public Position end() {
        return this.end;
    }

    public int endPos() {
        return this.end.pos;
    }

    public boolean isTracked() {
        return this != Untracked;
    }

    public boolean isImplicit() {
        if (!this.isTracked()) {
            return false;
        }
        return this.start.equals(this.end);
    }

    static Range of(Node node, boolean start) {
        String key;
        String string = key = start ? "jsoup.start" : "jsoup.end";
        if (!node.hasAttributes()) {
            return Untracked;
        }
        Object range = node.attributes().userData(key);
        return range != null ? (Range)range : Untracked;
    }

    @Deprecated
    public void track(Node node, boolean start) {
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Range range = (Range)o;
        if (!this.start.equals(range.start)) {
            return false;
        }
        return this.end.equals(range.end);
    }

    public int hashCode() {
        int result = this.start.hashCode();
        result = 31 * result + this.end.hashCode();
        return result;
    }

    public String toString() {
        return this.start + "-" + this.end;
    }

    public static class Position {
        private final int pos;
        private final int lineNumber;
        private final int columnNumber;

        public Position(int pos, int lineNumber, int columnNumber) {
            this.pos = pos;
            this.lineNumber = lineNumber;
            this.columnNumber = columnNumber;
        }

        public int pos() {
            return this.pos;
        }

        public int lineNumber() {
            return this.lineNumber;
        }

        public int columnNumber() {
            return this.columnNumber;
        }

        public boolean isTracked() {
            return this != UntrackedPos;
        }

        public String toString() {
            return this.lineNumber + "," + this.columnNumber + ":" + this.pos;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Position position = (Position)o;
            if (this.pos != position.pos) {
                return false;
            }
            if (this.lineNumber != position.lineNumber) {
                return false;
            }
            return this.columnNumber == position.columnNumber;
        }

        public int hashCode() {
            int result = this.pos;
            result = 31 * result + this.lineNumber;
            result = 31 * result + this.columnNumber;
            return result;
        }
    }

    public static class AttributeRange {
        static final AttributeRange UntrackedAttr = new AttributeRange(Untracked, Untracked);
        private final Range nameRange;
        private final Range valueRange;

        public AttributeRange(Range nameRange, Range valueRange) {
            this.nameRange = nameRange;
            this.valueRange = valueRange;
        }

        public Range nameRange() {
            return this.nameRange;
        }

        public Range valueRange() {
            return this.valueRange;
        }

        public String toString() {
            return this.nameRange().toString() + "=" + this.valueRange().toString();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            AttributeRange that = (AttributeRange)o;
            if (!this.nameRange.equals(that.nameRange)) {
                return false;
            }
            return this.valueRange.equals(that.valueRange);
        }

        public int hashCode() {
            int result = this.nameRange.hashCode();
            result = 31 * result + this.valueRange.hashCode();
            return result;
        }
    }
}

