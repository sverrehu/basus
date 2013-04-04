package no.shhsoft.basus.value;

import no.shhsoft.utils.FloatingPointFormatter;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RealValue
extends NumericValue {

    private final double real;
    public static final RealValue ZERO = new RealValue(0.0);
    public static final RealValue ONE = new RealValue(1.0);

    public RealValue(final double real) {
        this.real = real;
    }

    public double getValue() {
        return real;
    }

    @Override
    public double getValueAsDouble() {
        return real;
    }

    @Override
    public int getValueAsInteger() {
        return (int) real;
    }

    @Override
    public String toString() {
        return FloatingPointFormatter.format(real);
    }

}
