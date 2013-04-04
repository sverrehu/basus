package no.shhsoft.basus.ui.ide;

import java.awt.Color;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import no.shhsoft.basus.ui.ide.style.BasusStyleParser;
import no.shhsoft.basus.ui.ide.style.Style;
import no.shhsoft.basus.ui.ide.style.StyleArea;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class BasusSyntaxDocument
extends DefaultStyledDocument {

    private static final long serialVersionUID = 1L;
    private static final boolean HIGHLIGHTING_ENABLED = true;
    private static final SimpleAttributeSet NORMAL_STYLE;
    private static final SimpleAttributeSet RESERVED_WORD_STYLE;
    private static final SimpleAttributeSet COMMENT_STYLE;
    private static final SimpleAttributeSet STRING_STYLE;
    private static final SimpleAttributeSet ERROR_STYLE;
    private static final BasusStyleParser STYLE_PARSER = new BasusStyleParser();
    private boolean highlighting;

    private static SimpleAttributeSet createStyle(final Color foreground, final boolean bold, final boolean italic) {
        final SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setFontFamily(style, "Monospaced");
        StyleConstants.setFontSize(style, 12);
        StyleConstants.setBackground(style, Color.WHITE);
        StyleConstants.setForeground(style, foreground);
        StyleConstants.setBold(style, bold);
        StyleConstants.setItalic(style, italic);
        return style;
    }

    static {
        NORMAL_STYLE = createStyle(Color.BLACK, false, false);
        RESERVED_WORD_STYLE = createStyle(new Color(127, 0, 85), true, false);
        COMMENT_STYLE = createStyle(new Color(63, 127, 95), false, true);
        STRING_STYLE = createStyle(new Color(42, 0, 255), false, false);
        ERROR_STYLE = createStyle(Color.RED, false, false);
    }

    private static SimpleAttributeSet toAttributeSet(final Style style) {
        switch (style) {
            case COMMENT:
                return COMMENT_STYLE;
            case ERROR:
                return ERROR_STYLE;
            case NORMAL:
                return NORMAL_STYLE;
            case RESERVED_WORD:
                return RESERVED_WORD_STYLE;
            case STRING:
                return STRING_STYLE;
            default:
                throw new RuntimeException("Unhandled style " + style.toString());
        }
    }

    private synchronized void highlight() {
        String text = null;
        try {
            text = getText(0, getLength());
        } catch (final BadLocationException e1) {
            e1.printStackTrace();
        }
        final List<StyleArea> styleAreas = STYLE_PARSER.getStyleAreas(text);
        try {
            highlighting = true;
            writeLock();
            setCharacterAttributes(0, getLength(), NORMAL_STYLE, true);
            for (final StyleArea styleArea : styleAreas) {
                final SimpleAttributeSet attributeSet = toAttributeSet(styleArea.getStyle());
                final int from = styleArea.getFrom();
                final int length = styleArea.getTo() - from;
                setCharacterAttributes(from, length, attributeSet, true);
            }
        } finally {
            writeUnlock();
            highlighting = false;
        }
    }

    public synchronized boolean isHighlighting() {
        return highlighting;
    }

    public void updateFontSize(final int fontSize) {
        StyleConstants.setFontSize(NORMAL_STYLE, fontSize);
        StyleConstants.setFontSize(RESERVED_WORD_STYLE, fontSize);
        StyleConstants.setFontSize(COMMENT_STYLE, fontSize);
        StyleConstants.setFontSize(STRING_STYLE, fontSize);
        StyleConstants.setFontSize(ERROR_STYLE, fontSize);
        if (HIGHLIGHTING_ENABLED) {
            highlight();
        }
    }

    @Override
    public void remove(final int offs, final int len)
    throws BadLocationException {
        super.remove(offs, len);
        if (HIGHLIGHTING_ENABLED) {
            highlight();
        }
    }

    @Override
    public void insertString(final int offs, final String str, final AttributeSet a)
    throws BadLocationException {
        super.insertString(offs, str, a);
        if (HIGHLIGHTING_ENABLED) {
            highlight();
        }
    }

}
