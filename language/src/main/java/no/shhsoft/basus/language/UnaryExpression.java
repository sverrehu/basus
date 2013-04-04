package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UnaryExpression
extends AbstractExpression {

    private final Expression expression;
    private final boolean negate;
    private final OperatorType operatorType;

    public UnaryExpression(final Expression expression, final boolean negate, final OperatorType operatorType) {
        this.expression = expression;
        this.negate = negate;
        this.operatorType = operatorType;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean isNegate() {
        return negate;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

}
