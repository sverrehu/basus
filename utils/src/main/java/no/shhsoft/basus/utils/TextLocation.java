package no.shhsoft.basus.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class TextLocation {

    private int line;
    private int column;
    private int offset;

    public TextLocation() {
    }

    public TextLocation(final int line, final int column, final int offset) {
        this.line = line;
        this.column = column;
        this.offset = offset;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public int getOffset() {
        return offset;
    }

}
