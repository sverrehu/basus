package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CallStatement
extends AbstractStatement {

    private final FunctionExpression functionExpression;

    public CallStatement(final FunctionExpression functionExpression) {
        this.functionExpression = functionExpression;
    }

    public FunctionExpression getFunctionExpression() {
        return functionExpression;
    }

}
