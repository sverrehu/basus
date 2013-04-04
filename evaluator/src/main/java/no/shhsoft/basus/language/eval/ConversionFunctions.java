package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.ui.sprite.SpriteManager;
import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.BooleanValue;
import no.shhsoft.basus.value.ImageValue;
import no.shhsoft.basus.value.IntegerValue;
import no.shhsoft.basus.value.NumericValue;
import no.shhsoft.basus.value.RealValue;
import no.shhsoft.basus.value.SpriteValue;
import no.shhsoft.basus.value.StringValue;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class ConversionFunctions {

    private static final Function TO_STRING = new ToString();
    private static final Function CHAR_TO_STRING = new CharToString();
    private static final Function TO_INTEGER = new ToInteger();
    private static final Function TO_REAL = new ToReal();
    private static final Function TO_CHAR = new ToChar();
    private static final Function TO_SPRITE = new ToSprite();
    private static final Function IS_VALID_INTEGER = new IsValidInteger();
    private static final Function IS_VALID_REAL = new IsValidReal();

    private ConversionFunctions() {
    }

    private static final class ToString
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new StringValue(args[0].toString());
        }

        public ToString() {
            super("toString", 1, new Class<?>[] { Value.class });
        }

    }

    private static final class CharToString
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final char[] chars = new char[1];
            chars[0] = (char) ((NumericValue) args[0]).getValueAsInteger();
            return new StringValue(new String(chars));
        }

        public CharToString() {
            super("charToString", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class ToInteger
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final Value value = args[0];
            if (value instanceof NumericValue) {
                return IntegerValue.get(((NumericValue) value).getValueAsInteger());
            }
            if (value instanceof BooleanValue) {
                if (((BooleanValue) value).getValue()) {
                    return IntegerValue.ONE;
                }
                return IntegerValue.ZERO;
            }
            if (value instanceof StringValue) {
                try {
                    return IntegerValue.get(Integer.parseInt(((StringValue) value).getValue()
                    .trim()));
                } catch (final NumberFormatException e) {
                    return IntegerValue.ZERO;
                }
            }
            throw new RuntimeException("Unhandled type " + value.getClass().getName());
        }

        public ToInteger() {
            super("toInteger", 1, new Class<?>[] { Value.class });
        }

    }

    private static final class ToReal
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final Value value = args[0];
            if (value instanceof NumericValue) {
                return new RealValue(((NumericValue) value).getValueAsDouble());
            }
            if (value instanceof BooleanValue) {
                if (((BooleanValue) value).getValue()) {
                    return RealValue.ONE;
                }
                return RealValue.ZERO;
            }
            if (value instanceof StringValue) {
                try {
                    return new RealValue(Double.parseDouble(
                                              ((StringValue) value).getValue().trim()));
                } catch (final NumberFormatException e) {
                    return RealValue.ZERO;
                }
            }
            throw new RuntimeException("Unhandled type " + value.getClass().getName());
        }

        public ToReal() {
            super("toReal", 1, new Class<?>[] { Value.class });
        }

    }

    private static final class ToChar
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final String s = ((StringValue) args[0]).getValue();
            if (s.length() == 0) {
                return IntegerValue.ZERO;
            }
            return IntegerValue.get(s.charAt(0));
        }

        public ToChar() {
            super("toChar", 1, new Class<?>[] { StringValue.class });
        }

    }

    private static final class ToSprite
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final SpriteManager spriteManager = context.getDrawingArea().getSpriteManager();
            final int index = spriteManager.createSprite(((ImageValue) args[0]).getValue());
            return new SpriteValue(spriteManager, index);
        }

        public ToSprite() {
            super("toSprite", 1, new Class<?>[] { ImageValue.class });
        }

    }

    private static final class IsValidInteger
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final Value value = args[0];
            if (value instanceof NumericValue || value instanceof BooleanValue) {
                return BooleanValue.TRUE;
            }
            if (value instanceof StringValue) {
                try {
                    Integer.parseInt(((StringValue) value).getValue().trim());
                    return BooleanValue.TRUE;
                } catch (final NumberFormatException e) {
                    return BooleanValue.FALSE;
                }
            }
            throw new RuntimeException("Unhandled type " + value.getClass().getName());
        }

        public IsValidInteger() {
            super("isValidInteger", 1, new Class<?>[] { Value.class });
        }

    }

    private static final class IsValidReal
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final Value value = args[0];
            if (value instanceof NumericValue || value instanceof BooleanValue) {
                return BooleanValue.TRUE;
            }
            if (value instanceof StringValue) {
                try {
                    Double.parseDouble(((StringValue) value).getValue().trim());
                    return BooleanValue.TRUE;
                } catch (final NumberFormatException e) {
                    return BooleanValue.FALSE;
                }
            }
            throw new RuntimeException("Unhandled type " + value.getClass().getName());
        }

        public IsValidReal() {
            super("isValidReal", 1, new Class<?>[] { Value.class });
        }

    }

    public static void register(final SimpleEvaluationContext context) {
        context.registerFunction(TO_STRING);
        context.registerFunction(CHAR_TO_STRING);
        context.registerFunction(TO_INTEGER);
        context.registerFunction(TO_REAL);
        context.registerFunction(TO_CHAR);
        context.registerFunction(TO_SPRITE);
        context.registerFunction(IS_VALID_INTEGER);
        context.registerFunction(IS_VALID_REAL);
    }

}
