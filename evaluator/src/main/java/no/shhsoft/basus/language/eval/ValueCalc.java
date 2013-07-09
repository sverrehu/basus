package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.BooleanValue;
import no.shhsoft.basus.value.IntegerValue;
import no.shhsoft.basus.value.NumericValue;
import no.shhsoft.basus.value.RealValue;
import no.shhsoft.basus.value.StringValue;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class ValueCalc {

    private ValueCalc() {
    }

    public static Value negate(final Value operand, final TextLocationHolder holder) {
        if (operand instanceof IntegerValue) {
            return IntegerValue.get(-((IntegerValue) operand).getValue());
        } else if (operand instanceof RealValue) {
            return new RealValue(-((RealValue) operand).getValue());
        } else if (operand instanceof BooleanValue) {
            return BooleanValue.valueOf(!((BooleanValue) operand).getValue());
        }
        Evaluator.error("err.typeMismatch", holder);
        /* just to please the compiler */
        return null;
    }

    public static Value add(final Value operand0, final Value operand1,
                            final TextLocationHolder holder) {
        if (operand0 instanceof NumericValue && operand1 instanceof NumericValue) {
            if (operand0 instanceof IntegerValue && operand1 instanceof IntegerValue) {
                return IntegerValue.get(((IntegerValue) operand0).getValue()
                                        + ((IntegerValue) operand1).getValue());
            }
            return new RealValue(((NumericValue) operand0).getValueAsDouble()
                                 + ((NumericValue) operand1).getValueAsDouble());
        }
        if (operand0 instanceof StringValue && operand1 instanceof StringValue) {
            return new StringValue(((StringValue) operand0).getValue()
                                 + ((StringValue) operand1).getValue());
        }
        Evaluator.error("err.typeMismatch", holder);
        /* just to please the compiler */
        return null;
    }

    public static Value subtract(final Value operand0, final Value operand1,
                                 final TextLocationHolder holder) {
        if (operand0 instanceof NumericValue && operand1 instanceof NumericValue) {
            if (operand0 instanceof IntegerValue && operand1 instanceof IntegerValue) {
                return IntegerValue.get(((IntegerValue) operand0).getValue()
                                        - ((IntegerValue) operand1).getValue());
            }
            return new RealValue(((NumericValue) operand0).getValueAsDouble()
                                 - ((NumericValue) operand1).getValueAsDouble());
        }
        Evaluator.error("err.typeMismatch", holder);
        /* just to please the compiler */
        return null;
    }

    public static Value multiply(final Value operand0, final Value operand1,
                                 final TextLocationHolder holder) {
        if (operand0 instanceof NumericValue && operand1 instanceof NumericValue) {
            if (operand0 instanceof IntegerValue && operand1 instanceof IntegerValue) {
                return IntegerValue.get(((IntegerValue) operand0).getValue()
                                        * ((IntegerValue) operand1).getValue());
            }
            return new RealValue(((NumericValue) operand0).getValueAsDouble()
                                 * ((NumericValue) operand1).getValueAsDouble());
        }
        Evaluator.error("err.typeMismatch", holder);
        /* just to please the compiler */
        return null;
    }

    public static Value pow(final Value operand0, final Value operand1,
                            final TextLocationHolder holder) {
        if (operand0 instanceof NumericValue && operand1 instanceof NumericValue) {
            final boolean convertToInteger = (operand0 instanceof IntegerValue
                                        && operand1 instanceof IntegerValue);
            final double val = Math.pow(((NumericValue) operand0).getValueAsDouble(),
                                  ((NumericValue) operand1).getValueAsDouble());
            if (convertToInteger) {
                return IntegerValue.get((int) val);
            }
            return new RealValue(val);
        }
        Evaluator.error("err.typeMismatch", holder);
        /* just to please the compiler */
        return null;
    }

    public static Value divide(final Value operand0, final Value operand1,
                               final TextLocationHolder holder) {
        if (operand0 instanceof NumericValue && operand1 instanceof NumericValue) {
            if (operand0 instanceof IntegerValue && operand1 instanceof IntegerValue) {
                return IntegerValue.get(((IntegerValue) operand0).getValue()
                                        / ((IntegerValue) operand1).getValue());
            }
            return new RealValue(((NumericValue) operand0).getValueAsDouble()
                                 / ((NumericValue) operand1).getValueAsDouble());
        }
        Evaluator.error("err.typeMismatch", holder);
        /* just to please the compiler */
        return null;
    }

    public static Value modulus(final Value operand0, final Value operand1,
                                final TextLocationHolder holder) {
        if (operand0 instanceof NumericValue && operand1 instanceof NumericValue) {
            if (operand0 instanceof IntegerValue && operand1 instanceof IntegerValue) {
                return IntegerValue.get(((IntegerValue) operand0).getValue()
                                        % ((IntegerValue) operand1).getValue());
            }
            return new RealValue(((NumericValue) operand0).getValueAsDouble()
                                 % ((NumericValue) operand1).getValueAsDouble());
        }
        Evaluator.error("err.typeMismatch", holder);
        /* just to please the compiler */
        return null;
    }

    private static int compare(final Value operand0, final Value operand1,
                               final TextLocationHolder holder) {
        if (operand0 instanceof NumericValue && operand1 instanceof NumericValue) {
            return Double.compare(((NumericValue) operand0).getValueAsDouble(),
                                  ((NumericValue) operand1).getValueAsDouble());
        }
        if (operand0 instanceof StringValue && operand1 instanceof StringValue) {
            return ((StringValue) operand0).getValue()
                   .compareTo(((StringValue) operand1).getValue());
        }
        Evaluator.error("err.typeMismatch", holder);
        /* just to please the compiler */
        return 0;
    }

    public static boolean equal(final Value operand0, final Value operand1,
                                final TextLocationHolder holder) {
        return compare(operand0, operand1, holder) == 0;
    }

    public static boolean notEqual(final Value operand0, final Value operand1,
                                   final TextLocationHolder holder) {
        return compare(operand0, operand1, holder) != 0;
    }

    public static boolean less(final Value operand0, final Value operand1,
                               final TextLocationHolder holder) {
        return compare(operand0, operand1, holder) < 0;
    }

    public static boolean lessOrEqual(final Value operand0, final Value operand1,
                                      final TextLocationHolder holder) {
        return compare(operand0, operand1, holder) <= 0;
    }

    public static boolean greater(final Value operand0, final Value operand1,
                                  final TextLocationHolder holder) {
        return compare(operand0, operand1, holder) > 0;
    }

    public static boolean greaterOrEqual(final Value operand0, final Value operand1,
                                         final TextLocationHolder holder) {
        return compare(operand0, operand1, holder) >= 0;
    }

}
