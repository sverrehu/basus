package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ReturnStatement
extends AbstractStatement {

    private final Expression expression;

    public ReturnStatement(final Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

}
