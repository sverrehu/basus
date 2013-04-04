package no.shhsoft.utils;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FloatingPointFormatter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat();
    private static final DecimalFormat EXPONENTIAL_FORMAT = new DecimalFormat("0.0E0");

    static {
        final RoundingMode roundingMode = RoundingMode.HALF_UP;
        final DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(Locale.US);
        DECIMAL_FORMAT.setMinimumFractionDigits(1);
        DECIMAL_FORMAT.setMaximumFractionDigits(15);
        DECIMAL_FORMAT.setGroupingUsed(false);
        DECIMAL_FORMAT.setRoundingMode(roundingMode);
        /* needed to ensure decimal mark is a dot. */
        DECIMAL_FORMAT.setDecimalFormatSymbols(decimalFormatSymbols);

        EXPONENTIAL_FORMAT.setGroupingUsed(false);
        EXPONENTIAL_FORMAT.setRoundingMode(roundingMode);
        EXPONENTIAL_FORMAT.setDecimalFormatSymbols(decimalFormatSymbols);
    }

    private FloatingPointFormatter() {
    }

    public static String format(final double d) {
        final double ad = Math.abs(d);
        DecimalFormat format = DECIMAL_FORMAT;
        if (ad < 0.0000001 || ad > 1000000) {
            format = EXPONENTIAL_FORMAT;
        }
        synchronized (format) {
            return format.format(d);
        }
    }

}
