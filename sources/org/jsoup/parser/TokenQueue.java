/*
 * Decompiled with CFR 0.152.
 */
package org.jsoup.parser;

import org.jsoup.helper.Validate;
import org.jsoup.internal.StringUtil;

public class TokenQueue {
    private String queue;
    private int pos = 0;
    private static final char ESC = '\\';
    private static final String[] ElementSelectorChars = new String[]{"*|", "|", "_", "-"};
    private static final String[] CssIdentifierChars = new String[]{"-", "_"};

    public TokenQueue(String data) {
        Validate.notNull(data);
        this.queue = data;
    }

    public boolean isEmpty() {
        return this.remainingLength() == 0;
    }

    private int remainingLength() {
        return this.queue.length() - this.pos;
    }

    public void addFirst(String seq) {
        this.queue = seq + this.queue.substring(this.pos);
        this.pos = 0;
    }

    public boolean matches(String seq) {
        return this.queue.regionMatches(true, this.pos, seq, 0, seq.length());
    }

    public boolean matchesAny(String ... seq) {
        for (String s : seq) {
            if (!this.matches(s)) continue;
            return true;
        }
        return false;
    }

    public boolean matchesAny(char ... seq) {
        if (this.isEmpty()) {
            return false;
        }
        for (char c2 : seq) {
            if (this.queue.charAt(this.pos) != c2) continue;
            return true;
        }
        return false;
    }

    public boolean matchChomp(String seq) {
        if (this.matches(seq)) {
            this.pos += seq.length();
            return true;
        }
        return false;
    }

    public boolean matchesWhitespace() {
        return !this.isEmpty() && StringUtil.isWhitespace(this.queue.charAt(this.pos));
    }

    public boolean matchesWord() {
        return !this.isEmpty() && Character.isLetterOrDigit(this.queue.charAt(this.pos));
    }

    public void advance() {
        if (!this.isEmpty()) {
            ++this.pos;
        }
    }

    public char consume() {
        return this.queue.charAt(this.pos++);
    }

    public void consume(String seq) {
        if (!this.matches(seq)) {
            throw new IllegalStateException("Queue did not match expected sequence");
        }
        int len = seq.length();
        if (len > this.remainingLength()) {
            throw new IllegalStateException("Queue not long enough to consume sequence");
        }
        this.pos += len;
    }

    public String consumeTo(String seq) {
        int offset = this.queue.indexOf(seq, this.pos);
        if (offset != -1) {
            String consumed = this.queue.substring(this.pos, offset);
            this.pos += consumed.length();
            return consumed;
        }
        return this.remainder();
    }

    public String consumeToIgnoreCase(String seq) {
        int start = this.pos;
        String first = seq.substring(0, 1);
        boolean canScan = first.toLowerCase().equals(first.toUpperCase());
        while (!this.isEmpty() && !this.matches(seq)) {
            if (canScan) {
                int skip = this.queue.indexOf(first, this.pos) - this.pos;
                if (skip == 0) {
                    ++this.pos;
                    continue;
                }
                if (skip < 0) {
                    this.pos = this.queue.length();
                    continue;
                }
                this.pos += skip;
                continue;
            }
            ++this.pos;
        }
        return this.queue.substring(start, this.pos);
    }

    public String consumeToAny(String ... seq) {
        int start = this.pos;
        while (!this.isEmpty() && !this.matchesAny(seq)) {
            ++this.pos;
        }
        return this.queue.substring(start, this.pos);
    }

    public String chompTo(String seq) {
        String data = this.consumeTo(seq);
        this.matchChomp(seq);
        return data;
    }

    public String chompToIgnoreCase(String seq) {
        String data = this.consumeToIgnoreCase(seq);
        this.matchChomp(seq);
        return data;
    }

    public String chompBalanced(char open, char close) {
        String out;
        int start = -1;
        int end = -1;
        int depth = 0;
        int last = 0;
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        boolean inRegexQE = false;
        while (!this.isEmpty()) {
            block15: {
                char c2;
                block16: {
                    block13: {
                        block14: {
                            c2 = this.consume();
                            if (last == 92) break block13;
                            if (c2 == '\'' && c2 != open && !inDoubleQuote) {
                                inSingleQuote = !inSingleQuote;
                            } else if (c2 == '\"' && c2 != open && !inSingleQuote) {
                                boolean bl = inDoubleQuote = !inDoubleQuote;
                            }
                            if (!inSingleQuote && !inDoubleQuote && !inRegexQE) break block14;
                            last = c2;
                            break block15;
                        }
                        if (c2 == open) {
                            ++depth;
                            if (start == -1) {
                                start = this.pos;
                            }
                        } else if (c2 == close) {
                            --depth;
                        }
                        break block16;
                    }
                    if (c2 == 'Q') {
                        inRegexQE = true;
                    } else if (c2 == 'E') {
                        inRegexQE = false;
                    }
                }
                if (depth > 0 && last != 0) {
                    end = this.pos;
                }
                last = c2;
            }
            if (depth > 0) continue;
        }
        String string = out = end >= 0 ? this.queue.substring(start, end) : "";
        if (depth > 0) {
            Validate.fail("Did not find balanced marker at '" + out + "'");
        }
        return out;
    }

    public static String unescape(String in) {
        StringBuilder out = StringUtil.borrowBuilder();
        char last = '\u0000';
        for (char c2 : in.toCharArray()) {
            if (c2 == '\\') {
                if (last == '\\') {
                    out.append(c2);
                    c2 = '\u0000';
                }
            } else {
                out.append(c2);
            }
            last = c2;
        }
        return StringUtil.releaseBuilder(out);
    }

    public static String escapeCssIdentifier(String in) {
        StringBuilder out = StringUtil.borrowBuilder();
        TokenQueue q = new TokenQueue(in);
        while (!q.isEmpty()) {
            if (q.matchesCssIdentifier(ElementSelectorChars)) {
                out.append(q.consume());
                continue;
            }
            out.append('\\').append(q.consume());
        }
        return StringUtil.releaseBuilder(out);
    }

    public boolean consumeWhitespace() {
        boolean seen = false;
        while (this.matchesWhitespace()) {
            ++this.pos;
            seen = true;
        }
        return seen;
    }

    public String consumeWord() {
        int start = this.pos;
        while (this.matchesWord()) {
            ++this.pos;
        }
        return this.queue.substring(start, this.pos);
    }

    public String consumeElementSelector() {
        return this.consumeEscapedCssIdentifier(ElementSelectorChars);
    }

    public String consumeCssIdentifier() {
        return this.consumeEscapedCssIdentifier(CssIdentifierChars);
    }

    private String consumeEscapedCssIdentifier(String ... matches) {
        int start = this.pos;
        boolean escaped = false;
        while (!this.isEmpty()) {
            if (this.queue.charAt(this.pos) == '\\' && this.remainingLength() > 1) {
                escaped = true;
                this.pos += 2;
                continue;
            }
            if (!this.matchesCssIdentifier(matches)) break;
            ++this.pos;
        }
        String consumed = this.queue.substring(start, this.pos);
        return escaped ? TokenQueue.unescape(consumed) : consumed;
    }

    private boolean matchesCssIdentifier(String ... matches) {
        return this.matchesWord() || this.matchesAny(matches);
    }

    public String remainder() {
        String remainder = this.queue.substring(this.pos);
        this.pos = this.queue.length();
        return remainder;
    }

    public String toString() {
        return this.queue.substring(this.pos);
    }
}

