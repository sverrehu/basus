package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class AssignmentStatement
extends AbstractStatement {

    private final boolean local;
    private final AssignableExpression leftHandSide;
    private final Expression rightHandSide;

    public AssignmentStatement(final boolean local,
                               final AssignableExpression lhs, final Expression rhs) {
        this.local = local;
        this.leftHandSide = lhs;
        this.rightHandSide = rhs;
    }

    public boolean isLocal() {
        return local;
    }

    public AssignableExpression getLeftHandSide() {
        return leftHandSide;
    }

    public Expression getRightHandSide() {
        return rightHandSide;
    }

}
