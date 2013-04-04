package no.shhsoft.basus.language;

import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractExpressionList
extends AbstractExpression {

    private Expression[] expressions;

    private void setExpressions(final List<Expression> list) {
        expressions = new Expression[list.size()];
        expressions = list.toArray(expressions);
    }

    public final int getNumExpressions() {
        return expressions.length;
    }

    public final Expression getExpression(final int idx) {
        return expressions[idx];
    }

    public AbstractExpressionList(final List<Expression> expressions) {
        setExpressions(expressions);
    }

}
