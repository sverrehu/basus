package no.shhsoft.basus.language;

import java.util.List;

public class AdditiveExpression
extends AbstractExpressionListWithOperator {

    public AdditiveExpression(final List<Expression> expressions,
                              final List<OperatorType> operators) {
        super(expressions, operators);
    }

}
