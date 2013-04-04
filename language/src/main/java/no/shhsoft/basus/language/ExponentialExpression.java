package no.shhsoft.basus.language;

import java.util.List;

public class ExponentialExpression
extends AbstractExpressionListWithOperator {

    public ExponentialExpression(final List<Expression> expressions,
                                 final List<OperatorType> operators) {
        super(expressions, operators);
    }

}
