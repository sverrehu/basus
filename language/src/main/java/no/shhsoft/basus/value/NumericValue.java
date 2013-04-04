package no.shhsoft.basus.value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class NumericValue
implements Value {

    public abstract double getValueAsDouble();

    public abstract int getValueAsInteger();

}
