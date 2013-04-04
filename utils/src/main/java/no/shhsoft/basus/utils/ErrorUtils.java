package no.shhsoft.basus.utils;

import no.shhsoft.i18n.I18N;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ErrorUtils {

    private ErrorUtils() {
    }

    public static String getMessage(final String key, final TextLocation loc,
                                    final Object... args) {
        final StringBuilder sb = new StringBuilder();
        sb.append(I18N.msg(key, args));
        if (loc != null) {
            sb.append(' ');
            sb.append(I18N.msg("err.atLine"));
            sb.append(' ');
            sb.append(loc.getLine());
        }
        return sb.toString();
    }

}
