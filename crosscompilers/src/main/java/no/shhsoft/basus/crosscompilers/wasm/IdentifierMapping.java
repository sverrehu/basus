package no.shhsoft.basus.crosscompilers.wasm;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class IdentifierMapping {

    private final Map<String, Integer> map = new HashMap<>();
    private int nextValue;

    public int get(final String name) {
        final Integer i = map.get(name);
        if (i == null) {
            return -1;
        }
        return i;
    }

    public int put(final String name) {
        if (map.containsKey(name)) {
            throw new RuntimeException("Key \"" + name + "\" already exists")
        }
        final int value = nextValue++;
        map.put(name, value);
        return value;
    }

    public int getOrPut(final String name) {
        int value = get(name);
        if (value < 0) {
            value = put(name);
        }
        return value;
    }

}
