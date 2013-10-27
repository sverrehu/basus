package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.ArrayValue;
import no.shhsoft.basus.value.IntegerValue;
import no.shhsoft.basus.value.NumericValue;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class MiscFunctions {

    private static final Function PRINT = new Print();
    private static final Function PRINTLN = new Println();
    private static final Function WAIT = new Wait();
    private static final Function TIME = new Time();
    private static final Function EXIT = new Exit();
    private static final Function MAX_INDEX = new MaxIndex();

    private MiscFunctions() {
    }

    private abstract static class AbstractPrint
    extends BuiltinFunction {

        private final boolean appendNewline;

        @Override
        protected final Value implCall(final EvaluationContext context,
                                       final TextLocationHolder locationHolder, final Value[] args) {
            final StringBuilder sb = new StringBuilder();
            for (final Value arg : args) {
                sb.append(arg.toString());
            }
            if (appendNewline) {
                context.getConsole().println(sb.toString());
            } else {
                context.getConsole().print(sb.toString());
            }
            return IntegerValue.ZERO;
        }

        public AbstractPrint(final String name, final boolean appendNewline) {
            super(name, Function.NUM_ARGS_ANY, new Class<?>[] { Value.class });
            this.appendNewline = appendNewline;
        }

    }

    private static final class Print
    extends AbstractPrint {

        public Print() {
            super("print", false);
        }

    }

    private static final class Println
    extends AbstractPrint {

        public Println() {
            super("println", true);
        }

    }

    private static final class Time
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return IntegerValue.get((int) (System.currentTimeMillis() - context.getStartTime()));
        }

        public Time() {
            super("time", 0, null);
        }

    }

    private static final class Wait
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final int t = ((NumericValue) args[0]).getValueAsInteger();
            try {
                if (t > 0) {
                    Thread.sleep(t);
                }
            } catch (final InterruptedException e) {
                ignore();
            }
            return IntegerValue.ZERO;
        }

        public Wait() {
            super("wait", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class MaxIndex
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final ArrayValue array = (ArrayValue) args[0];
            return IntegerValue.get(array.getLength() - 1);
        }

        public MaxIndex() {
            super("maxIndex", 1, new Class<?>[] { ArrayValue.class });
        }

    }

    private static final class Exit
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            context.setStopProgram(true);
            return IntegerValue.ZERO;
        }

        public Exit() {
            super("exit", 0, null);
        }

    }

    public static void register(final SimpleEvaluationContext context) {
        context.registerFunction(PRINT);
        context.registerFunction(PRINTLN);
        context.registerFunction(WAIT);
        context.registerFunction(TIME);
        context.registerFunction(EXIT);
        context.registerFunction(MAX_INDEX);
    }

}
