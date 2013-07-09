package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.IntegerValue;
import no.shhsoft.basus.value.NumericValue;
import no.shhsoft.basus.value.RealValue;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class MathFunctions {

    private static final Function SIN = new Sin();
    private static final Function COS = new Cos();
    private static final Function TAN = new Tan();
    private static final Function ASIN = new Asin();
    private static final Function ACOS = new Acos();
    private static final Function ATAN = new Atan();
    private static final Function SINR = new Sinr();
    private static final Function COSR = new Cosr();
    private static final Function TANR = new Tanr();
    private static final Function ASINR = new Asinr();
    private static final Function ACOSR = new Acosr();
    private static final Function ATANR = new Atanr();
    private static final Function SINH = new Sinh();
    private static final Function COSH = new Cosh();
    private static final Function TANH = new Tanh();
    private static final Function ABS = new Abs();
    private static final Function CBRT = new Cbrt();
    private static final Function CEIL = new Ceil();
    private static final Function FLOOR = new Floor();
    private static final Function LOG = new Log();
    private static final Function LOG10 = new Log10();
    private static final Function ROUND = new Round();
    private static final Function SQRT = new Sqrt();
    private static final Function RANDOM = new Random();
    private static final Function MIN = new Min();
    private static final Function MAX = new Max();

    private MathFunctions() {
    }

    /* not private to avoid "synthetic access" */
    private static double degToRad(final double deg) {
        return 2.0 * Math.PI * (deg / 360.0);
    }

    /* not private to avoid "synthetic access" */
    private static double radToDeg(final double rad) {
        return 360.0 * rad / (2.0 * Math.PI);
    }

    private static final class Sin
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.sin(degToRad(((NumericValue) args[0]).getValueAsDouble())));
        }

        public Sin() {
            super("sin", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Cos
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.cos(degToRad(((NumericValue) args[0]).getValueAsDouble())));
        }

        public Cos() {
            super("cos", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Tan
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.tan(degToRad(((NumericValue) args[0]).getValueAsDouble())));
        }

        public Tan() {
            super("tan", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Asin
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(radToDeg(Math.asin(((NumericValue) args[0]).getValueAsDouble())));
        }

        public Asin() {
            super("asin", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Acos
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(radToDeg(Math.acos(((NumericValue) args[0]).getValueAsDouble())));
        }

        public Acos() {
            super("acos", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Atan
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(radToDeg(Math.atan(((NumericValue) args[0]).getValueAsDouble())));
        }

        public Atan() {
            super("atan", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Sinr
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.sin(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Sinr() {
            super("sinr", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Cosr
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.cos(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Cosr() {
            super("cosr", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Tanr
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.tan(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Tanr() {
            super("tanr", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Asinr
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.asin(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Asinr() {
            super("asinr", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Acosr
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.acos(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Acosr() {
            super("acosr", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Atanr
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.atan(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Atanr() {
            super("atanr", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Sinh
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.sinh(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Sinh() {
            super("sinh", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Cosh
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.cosh(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Cosh() {
            super("cosh", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Tanh
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.tanh(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Tanh() {
            super("tanh", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Abs
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.abs(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Abs() {
            super("abs", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Cbrt
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.cbrt(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Cbrt() {
            super("cbrt", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Ceil
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.ceil(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Ceil() {
            super("ceil", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Floor
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.floor(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Floor() {
            super("floor", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Log
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.log(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Log() {
            super("log", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Log10
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.log10(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Log10() {
            super("log10", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Round
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.rint(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Round() {
            super("round", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Sqrt
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new RealValue(Math.sqrt(((NumericValue) args[0]).getValueAsDouble()));
        }

        public Sqrt() {
            super("sqrt", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Random
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            if (args.length == 0) {
                /* >= 0.0, < 1.0 */
                return new RealValue(Math.random());
            }
            if (args.length == 1) {
                /* >= 0, <= max */
                final int max = ((NumericValue) args[0]).getValueAsInteger();
                return IntegerValue.get((int) ((max + 1) * Math.random()));
            }
            if (args.length == 2) {
                /* >= from, <= to */
                if (args[0] instanceof NumericValue && args[1] instanceof NumericValue) {
                    final int from = ((NumericValue) args[0]).getValueAsInteger();
                    final int to = ((NumericValue) args[1]).getValueAsInteger();
                    final int max = Math.abs(to - from);
                    return IntegerValue.get(Math.min(from, to) + (int) ((max + 1) * Math.random()));
                }
            }
            error("err.funcArgNumeric012", locationHolder, getName());
            /* just to please the compiler */
            return null;
        }

        public Random() {
            super("random", Function.NUM_ARGS_ANY,
                  new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Min
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            if (args[0] instanceof IntegerValue && args[1] instanceof IntegerValue) {
                return IntegerValue.get(Math.min(((IntegerValue) args[0]).getValue(),
                                                 ((IntegerValue) args[1]).getValue()));
            }
            return new RealValue(Math.min(((NumericValue) args[0]).getValueAsDouble(),
                                          ((NumericValue) args[1]).getValueAsDouble()));
        }

        public Min() {
            super("min", 2, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class Max
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            if (args[0] instanceof IntegerValue && args[1] instanceof IntegerValue) {
                return IntegerValue.get(Math.max(((IntegerValue) args[0]).getValue(),
                                                 ((IntegerValue) args[1]).getValue()));
            }
            return new RealValue(Math.max(((NumericValue) args[0]).getValueAsDouble(),
                                          ((NumericValue) args[1]).getValueAsDouble()));
        }

        public Max() {
            super("max", 2, new Class<?>[] { NumericValue.class });
        }

    }

    public static void register(final SimpleEvaluationContext context) {
        context.registerFunction(SIN);
        context.registerFunction(COS);
        context.registerFunction(TAN);
        context.registerFunction(ASIN);
        context.registerFunction(ACOS);
        context.registerFunction(ATAN);
        context.registerFunction(SINR);
        context.registerFunction(COSR);
        context.registerFunction(TANR);
        context.registerFunction(ASINR);
        context.registerFunction(ACOSR);
        context.registerFunction(ATANR);
        context.registerFunction(SINH);
        context.registerFunction(COSH);
        context.registerFunction(TANH);
        context.registerFunction(ABS);
        context.registerFunction(CBRT);
        context.registerFunction(CEIL);
        context.registerFunction(FLOOR);
        context.registerFunction(LOG);
        context.registerFunction(LOG10);
        context.registerFunction(ROUND);
        context.registerFunction(SQRT);
        context.registerFunction(RANDOM);
        context.registerFunction(MIN);
        context.registerFunction(MAX);
    }

}
