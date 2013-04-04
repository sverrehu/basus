package no.shhsoft.basus.value;


/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ArrayValue
implements Value {

    private Value[] values;

    public ArrayValue() {
        this.values = new Value[0];
    }

    public void setValue(final int index, final Value value) {
        if (index < 0) {
            throw new RuntimeException("Array index " + index + " less than zero");
        }
        if (values.length <= index) {
            final Value[] oldValues = values;
            values = new Value[index + 1];
            System.arraycopy(oldValues, 0, values, 0, oldValues.length);
        }
        values[index] = value;
    }

    public Value getValue(final int index) {
        if (index < 0 || index >= values.length) {
            throw new RuntimeException("Array index " + index + " out of bounds");
        }
        return values[index];
    }

    public boolean hasValue(final int index) {
        if (index < 0 || index >= values.length) {
            return false;
        }
        return values[index] != null;
    }

    public int getLength() {
        return values.length;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (int q = 0; q < values.length; q++) {
            if (q > 0) {
                sb.append(", ");
            }
            final Value value = values[q];
            if (value != null) {
                sb.append(value.toString());
            }
        }
        sb.append(" }");
        return sb.toString();
    }

}
