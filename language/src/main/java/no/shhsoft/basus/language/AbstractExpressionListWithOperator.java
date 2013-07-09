package no.shhsoft.basus.language;

import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public class AbstractExpressionListWithOperator
extends AbstractExpressionList {

    private OperatorType[] operators;

    private void setOperators(final List<OperatorType> list) {
        operators = new OperatorType[list.size()];
        for (int q = operators.length - 1; q >= 0; q--) {
            operators[q] = list.get(q);
        }
    }

    public final OperatorType getOperator(final int idx) {
        return operators[idx];
    }

    AbstractExpressionListWithOperator(final List<Expression> expressions,
                                       final List<OperatorType> operators) {
        super(expressions);
        setOperators(operators);
        if (operators.size() != getNumExpressions() - 1) {
            throw new RuntimeException("Incorrect number of operators: was " + operators.size()
                                       + ", expected " + (getNumExpressions() - 1));
        }
    }

}
