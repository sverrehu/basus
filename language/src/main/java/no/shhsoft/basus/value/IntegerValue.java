package no.shhsoft.basus.value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class IntegerValue
extends NumericValue {

    private final int integer;
    private final boolean fromCharacterConstant;
    private static final int NUM_CACHED = 2048;
    private static final int CACHE_OFFSET = NUM_CACHED / 2;
    private static final int CACHE_FROM = -(NUM_CACHED - CACHE_OFFSET - 1);
    private static final int CACHE_TO = CACHE_FROM + NUM_CACHED - 1;
    private static final IntegerValue[] CACHE;
    public static final IntegerValue ZERO;
    public static final IntegerValue ONE;

    static {
        CACHE = new IntegerValue[NUM_CACHED];
        for (int q = 0; q < NUM_CACHED; q++) {
            CACHE[q] = new IntegerValue(CACHE_FROM + q);
        }
        ZERO = get(0);
        ONE = get(1);
    }

    private IntegerValue(final int integer) {
        this.integer = integer;
        this.fromCharacterConstant = false;
    }

    private IntegerValue(final int integer, final boolean fromCharacterConstant) {
        this.integer = integer;
        this.fromCharacterConstant = fromCharacterConstant;
    }

    public static IntegerValue get(final int i) {
        if (i >= CACHE_FROM && i <= CACHE_TO) {
            return CACHE[i - CACHE_FROM];
        }
        return new IntegerValue(i);
    }

    public static IntegerValue getFromCharacterConstant(final int i) {
        return new IntegerValue(i, true);
    }

    public int getValue() {
        return integer;
    }

    public boolean isFromCharacterConstant() {
        return fromCharacterConstant;
    }

    @Override
    public double getValueAsDouble() {
        return integer;
    }

    @Override
    public int getValueAsInteger() {
        return integer;
    }

    @Override
    public String toString() {
        return String.valueOf(integer);
    }

}
