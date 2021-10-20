package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.IntegerValue;
import no.shhsoft.basus.value.NumericValue;
import no.shhsoft.basus.value.StringValue;
import no.shhsoft.basus.value.Value;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class StringFunctions {

    private static final Function LENGTH = new Length();
    private static final Function SUBSTRING = new Substring();
    private static final Function CHAR_AT = new CharAt();
    private static final Function INDEX_OF = new IndexOf();
    private static final Function TO_UPPER = new ToUpper();
    private static final Function TO_LOWER = new ToLower();
    static final List<Function> ALL_FUNCTIONS = Arrays.asList(
        LENGTH, SUBSTRING, CHAR_AT, INDEX_OF, TO_UPPER, TO_LOWER
    );

    private StringFunctions() {
    }

    private abstract static class AbstractStringOrCharacterFunction
    extends BuiltinFunction {

        protected abstract Value stringCall(EvaluationContext context,
                                            TextLocationHolder locationHolder, Value[] args);

        protected abstract Value characterCall(EvaluationContext context,
                                               TextLocationHolder locationHolder, Value[] args);

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final Value stringOrCharacterArgument = args[getNumArgs() - 1];
            if (stringOrCharacterArgument instanceof StringValue) {
                return stringCall(context, locationHolder, args);
            }
            if (stringOrCharacterArgument instanceof NumericValue) {
                return characterCall(context, locationHolder, args);
            }
            if (getNumArgs() == 1) {
                error("err.funcArgNotStringOrCharacter", locationHolder, getName());
            } else {
                error("err.lastFuncArgNotStringOrCharacter", locationHolder, getName());
            }
            /* just to please the compiler */
            return IntegerValue.ZERO;
        }

        AbstractStringOrCharacterFunction(final String name) {
            super(name, 1, new Class<?>[] { Value.class });
        }

        AbstractStringOrCharacterFunction(final String name, final int numArgs,
                                          final Class<?>[] argTypes) {
            super(name, numArgs, argTypes);
        }

    }

    private static final class Length
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final String s = ((StringValue) args[0]).getValue();
            return IntegerValue.get(s.length());
        }

        public Length() {
            super("length", 1, new Class<?>[] { StringValue.class });
        }

    }

    private static final class Substring
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final String s = ((StringValue) args[0]).getValue();
            int from = ((NumericValue) args[1]).getValueAsInteger() - 1;
            int length = ((NumericValue) args[2]).getValueAsInteger();
            if (from < 0) {
                from = 0;
            } else if (from > s.length()) {
                from = s.length();
            }
            if (length < 0) {
                length = 0;
            }
            int end = from + length;
            if (end > s.length()) {
                end = s.length();
            }
            return new StringValue(s.substring(from, end));
        }

        public Substring() {
            super("substring", 3, new Class<?>[] { StringValue.class,
                                                          NumericValue.class });
        }

    }

    private static final class CharAt
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final String s = ((StringValue) args[0]).getValue();
            final int index = ((NumericValue) args[1]).getValueAsInteger() - 1;
            if (index < 0 || index >= s.length()) {
                return IntegerValue.ZERO;
            }
            return IntegerValue.get(s.charAt(index));
        }

        public CharAt() {
            super("charAt", 2, new Class<?>[] { StringValue.class,
                                                       NumericValue.class });
        }

    }

    private static final class IndexOf
    extends AbstractStringOrCharacterFunction {

        @Override
        protected Value characterCall(final EvaluationContext context, final TextLocationHolder locationHolder,
                                      final Value[] args) {
            final String haystack = ((StringValue) args[0]).getValue();
            final int needleCharacter = ((NumericValue) args[1]).getValueAsInteger();
            return IntegerValue.get(haystack.indexOf(needleCharacter) + 1);
        }

        @Override
        protected Value stringCall(final EvaluationContext context,
                                   final TextLocationHolder locationHolder,
                                   final Value[] args) {
            final String haystack = ((StringValue) args[0]).getValue();
            final String needleString = ((StringValue) args[1]).getValue();
            return IntegerValue.get(haystack.indexOf(needleString) + 1);
        }

        public IndexOf() {
            super("indexOf", 2, new Class<?>[] { StringValue.class, Value.class });
        }

    }

    private static final class ToUpper
    extends AbstractStringOrCharacterFunction {

        @Override
        protected Value characterCall(final EvaluationContext context,
                                      final TextLocationHolder locationHolder,
                                      final Value[] args) {
            final int character = ((NumericValue) args[0]).getValueAsInteger();
            return IntegerValue.get(Character.toUpperCase(character));
        }

        @Override
        protected Value stringCall(final EvaluationContext context,
                                   final TextLocationHolder locationHolder,
                                   final Value[] args) {
            final String string = ((StringValue) args[0]).getValue();
            return new StringValue(string.toUpperCase());
        }

        public ToUpper() {
            super("toUpper");
        }

    }

    private static final class ToLower
    extends AbstractStringOrCharacterFunction {

        @Override
        protected Value characterCall(final EvaluationContext context,
                                      final TextLocationHolder locationHolder,
                                      final Value[] args) {
            final int character = ((NumericValue) args[0]).getValueAsInteger();
            return IntegerValue.get(Character.toLowerCase(character));
        }

        @Override
        protected Value stringCall(final EvaluationContext context,
                                   final TextLocationHolder locationHolder,
                                   final Value[] args) {
            final String string = ((StringValue) args[0]).getValue();
            return new StringValue(string.toLowerCase());
        }

        public ToLower() {
            super("toLower");
        }

    }

    public static void register(final SimpleEvaluationContext context) {
        for (final Function function : ALL_FUNCTIONS) {
            context.registerFunction(function);
        }
    }

}
