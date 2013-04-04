package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RelationalExpression
extends AbstractExpression {

    private final Expression leftHandSide;
    private final OperatorType operator;
    private final Expression rightHandSide;

    public RelationalExpression(final Expression leftHandSide, final OperatorType operator, final Expression rightHandSide) {
        this.leftHandSide = leftHandSide;
        this.operator = operator;
        this.rightHandSide = rightHandSide;
    }

    public Expression getLeftHandSide() {
        return leftHandSide;
    }

    public OperatorType getOperator() {
        return operator;
    }

    public Expression getRightHandSide() {
        return rightHandSide;
    }

}
