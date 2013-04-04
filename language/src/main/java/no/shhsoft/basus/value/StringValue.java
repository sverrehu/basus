package no.shhsoft.basus.value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StringValue
implements Value {

    private final String string;

    public StringValue(final String string) {
        this.string = string;
    }

    public String getValue() {
        return string;
    }

    @Override
    public String toString() {
        return getValue();
    }

}
