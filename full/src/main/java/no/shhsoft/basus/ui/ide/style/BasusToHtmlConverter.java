package no.shhsoft.basus.ui.ide.style;

import java.util.List;

import no.shhsoft.utils.StringUtils;
import no.shhsoft.utils.TrimmedHtmlUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BasusToHtmlConverter {

    private static final String CSS_STYLE_PREFIX = "basus";
    private static final String MAIN_CSS_CLASS = CSS_STYLE_PREFIX + "program";

    private BasusToHtmlConverter() {
    }

    private static String escape(final String s) {
        String ret = TrimmedHtmlUtils.encode(s);
        ret = StringUtils.replace(ret, " ", "&nbsp;");
        ret = StringUtils.replace(ret, "\t", "&nbsp;");
        ret = StringUtils.replace(ret, "\n", "<br/>\n");
        ret = StringUtils.replace(ret, "\r", "");
        return ret;
    }

    private static String getStyleClass(final Style style) {
        return CSS_STYLE_PREFIX + style.toString().toLowerCase().replaceAll("_", "");
    }

    private static void append(final StringBuilder sb, final String basusProgram, final int from, final int to, final Style style) {
        final String sub = basusProgram.substring(from, to);
        if (style != null) {
            sb.append("<span class=\"");
            sb.append(TrimmedHtmlUtils.encode(getStyleClass(style)));
            sb.append("\">");
        }
        sb.append(escape(sub));
        if (style != null) {
            sb.append("</span>");
        }
    }

    private static void append(final StringBuilder sb, final String basusProgram, final int from, final int to) {
        append(sb, basusProgram, from, to, null);
    }

    private static void appendStyleClassInStyleSheet(final StringBuilder sb, final Style style) {
        sb.append('.');
        if (style == null) {
            sb.append(MAIN_CSS_CLASS);
        } else {
            sb.append(getStyleClass(style));
        }
    }

    private static void appendStyleSheet(final StringBuilder sb) {
        sb.append("<style type=\"text/css\">\n");
        appendStyleClassInStyleSheet(sb, null);
        sb.append(" { font-family: courier new, courier, monospace; color: #000000;"
                  + " font-weight: normal; font-style: normal; font-size: 10pt; }\n");
        appendStyleClassInStyleSheet(sb, Style.COMMENT);
        sb.append(" { font-style: italic; color: #3f7f5f; }\n");
        appendStyleClassInStyleSheet(sb, Style.RESERVED_WORD);
        sb.append(" { font-weight: bold; color: #7f0055; }\n");
        appendStyleClassInStyleSheet(sb, Style.STRING);
        sb.append(" { color: #2a00ff; }\n");
        appendStyleClassInStyleSheet(sb, Style.ERROR);
        sb.append(" { color: #ff0000; }\n");
        sb.append("</style>\n");
    }

    public static String toHtml(final String basusProgram) {
        final StringBuilder sb = new StringBuilder();
        appendStyleSheet(sb);
        sb.append("<div class=\"");
        sb.append(MAIN_CSS_CLASS);
        sb.append("\">\n");
        final List<StyleArea> styleAreas = new BasusStyleParser().getStyleAreas(basusProgram);
        int from = 0;
        for (final StyleArea styleArea : styleAreas) {
            final int styleFrom = styleArea.getFrom();
            final int styleTo = styleArea.getTo();
            if (styleFrom > from) {
                append(sb, basusProgram, from, styleFrom);
            }
            append(sb, basusProgram, styleFrom, styleTo, styleArea.getStyle());
            from = styleTo;
        }
        if (from < basusProgram.length()) {
            append(sb, basusProgram, from, basusProgram.length());
        }
        sb.append("</div>\n");
        return sb.toString();
    }

}
