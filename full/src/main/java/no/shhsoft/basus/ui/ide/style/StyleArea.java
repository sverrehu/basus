package no.shhsoft.basus.ui.ide.style;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StyleArea {

    private final int from;
    private final int to;
    private final Style style;

    public StyleArea(final int from, final int to, final Style style) {
        this.from = from;
        this.to = to;
        this.style = style;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public Style getStyle() {
        return style;
    }

}
