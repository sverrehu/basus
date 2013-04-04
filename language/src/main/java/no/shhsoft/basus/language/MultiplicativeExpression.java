package no.shhsoft.basus.language;

import java.util.List;

public class MultiplicativeExpression
extends AbstractExpressionListWithOperator {

    public MultiplicativeExpression(final List<Expression> expressions,
                                    final List<OperatorType> operators) {
        super(expressions, operators);
    }

}
