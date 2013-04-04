package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.ArrayValue;
import no.shhsoft.basus.value.BooleanValue;
import no.shhsoft.basus.value.ImageValue;
import no.shhsoft.basus.value.NumericValue;
import no.shhsoft.basus.value.SpriteValue;
import no.shhsoft.basus.value.StringValue;
import no.shhsoft.basus.value.Value;
import no.shhsoft.basus.value.WidthAndHeightValue;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractFunction
implements Function {

    private final String name;
    private final Class<?>[] argTypes;
    private final int numArgs;

    private static String getTypeMismatchMessageKey(final Class<?> type) {
        if (type == BooleanValue.class) {
            return "err.funcArgNotBoolean";
        }
        if (type == NumericValue.class) {
            return "err.funcArgNotNumeric";
        }
        if (type == StringValue.class) {
            return "err.funcArgNotString";
        }
        if (type == ArrayValue.class) {
            return "err.funcArgNotArray";
        }
        if (type == ImageValue.class) {
            return "err.funcArgNotImage";
        }
        if (type == SpriteValue.class) {
            return "err.funcArgNotSprite";
        }
        if (type == WidthAndHeightValue.class) {
            return "err.funcArgNotWidthAndHeight";
        }
        throw new RuntimeException("Unhandled expected type " + type.getName());
    }

    protected static void ignore() {
    }

    protected abstract Value implCall(EvaluationContext context, TextLocationHolder locationHolder,
                                      Value[] args);

    protected static void error(final String key, final TextLocationHolder holder,
                                final Object... args) {
        Evaluator.error(key, holder, args);
    }

    public AbstractFunction(final String name, final int numArgs,
                            final Class<?>[] argTypes) {
        this.name = name;
        this.numArgs = numArgs;
        this.argTypes = argTypes;
    }

    @Override
    @SuppressWarnings("boxing")
    public final Value call(final EvaluationContext context,
                            final TextLocationHolder locationHolder, final Value[] args) {
        final int expectedNumArgs = getNumArgs();
        if (expectedNumArgs >= 0 && args.length != expectedNumArgs) {
            if (expectedNumArgs == 0) {
                error("err.funcArgCount0", locationHolder, getName());
            } else if (expectedNumArgs == 1) {
                error("err.funcArgCount1", locationHolder, getName());
            } else {
                error("err.funcArgCountN", locationHolder, getName(), expectedNumArgs);
            }
        } else if (expectedNumArgs == Function.NUM_ARGS_ANY_EVEN && (args.length & 1) != 0) {
            error("err.funcArgCountEven", locationHolder, getName());
        } else if (expectedNumArgs == Function.NUM_ARGS_ZERO_OR_ONE && args.length > 1) {
            error("err.funcArgCount01", locationHolder, getName());
        } else if (expectedNumArgs == Function.NUM_ARGS_ONE_OR_TWO
                   && args.length != 1 && args.length != 2) {
            error("err.funcArgCount12", locationHolder, getName());
        } else if (expectedNumArgs == Function.NUM_ARGS_ONE_OR_THREE
                   && args.length != 1 && args.length != 3) {
            error("err.funcArgCount13", locationHolder, getName());
        } else if (expectedNumArgs == Function.NUM_ARGS_THREE_OR_FOUR
                   && args.length != 3 && args.length != 4) {
            error("err.funcArgCount34", locationHolder, getName());
        }
        final Class<?>[] expectedTypes = getArgTypes();
        if (expectedTypes != null && expectedTypes.length > 0) {
            int expectedIndex = 0;
            Class<?> expectedType = expectedTypes[expectedIndex];
            for (int q = 0; q < args.length; q++) {
                if (!expectedType.isAssignableFrom(args[q].getClass())) {
                    error(getTypeMismatchMessageKey(expectedType),
                          locationHolder, q + 1, getName());
                }
                if (expectedIndex < expectedTypes.length - 1) {
                    ++expectedIndex;
                    expectedType = expectedTypes[expectedIndex];
                }
            }
        }
        return implCall(context, locationHolder, args);
    }

    @Override
    public final Class<?>[] getArgTypes() {
        return argTypes;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final int getNumArgs() {
        return numArgs;
    }

}
