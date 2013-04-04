package no.shhsoft.basus.value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BooleanValue
implements Value {

    private final boolean bool;
    public static final BooleanValue TRUE = new BooleanValue(true);
    public static final BooleanValue FALSE = new BooleanValue(false);

    private BooleanValue(final boolean bool) {
        this.bool = bool;
    }

    public boolean getValue() {
        return bool;
    }

    @Override
    public String toString() {
        if (bool) {
            return "TRUE";
        }
        return "FALSE";
    }

    public static BooleanValue valueOf(final boolean b) {
        if (b) {
            return TRUE;
        }
        return FALSE;
    }

}
