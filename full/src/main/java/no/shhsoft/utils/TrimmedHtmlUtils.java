package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class TrimmedHtmlUtils {

    private TrimmedHtmlUtils() {
        /* not to be instantiated. */
    }

    public static String encode(final String s) {
        if (s == null) {
            return "(null)";
        }
        if (s.indexOf('"') < 0 && s.indexOf('&') < 0 && s.indexOf('<') < 0 && s.indexOf('>') < 0) {
            return s;
        }
        final int len = s.length();
        final StringBuilder sb = new StringBuilder(len + 10);
        for (int q = 0; q < len; q++) {
            final char c = s.charAt(q);
            switch (c) {
                case '"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

}
