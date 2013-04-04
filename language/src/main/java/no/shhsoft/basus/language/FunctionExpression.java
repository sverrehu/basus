package no.shhsoft.basus.language;

import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FunctionExpression
extends AbstractExpressionList {

    private final String functionName;

    public FunctionExpression(final String functionName, final List<Expression> expressions) {
        super(expressions);
        this.functionName = functionName;
    }

    public String getFunctionName() {
        return functionName;
    }

}
