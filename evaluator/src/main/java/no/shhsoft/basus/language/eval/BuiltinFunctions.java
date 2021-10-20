package no.shhsoft.basus.language.eval;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BuiltinFunctions {

    private static final List<Function> FUNCTIONS = new ArrayList<>();
    private static final Set<String> FUNCTION_NAMES = new HashSet<>();

    static {
        FUNCTIONS.addAll(MathFunctions.ALL_FUNCTIONS);
        FUNCTIONS.addAll(ConversionFunctions.ALL_FUNCTIONS);
        FUNCTIONS.addAll(StringFunctions.ALL_FUNCTIONS);
        FUNCTIONS.addAll(MiscFunctions.ALL_FUNCTIONS);
        FUNCTIONS.addAll(UserInputFunctions.ALL_FUNCTIONS);
        FUNCTIONS.addAll(GraphicFunctions.ALL_FUNCTIONS);
        for (final Function function : FUNCTIONS) {
            FUNCTION_NAMES.add(function.getName());
        }
    }

    public static boolean isBuiltin(final String name) {
        return FUNCTION_NAMES.contains(name);
    }

}
