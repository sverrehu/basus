package no.shhsoft.basus.language;

import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class IndexExpression
extends AbstractExpressionList
implements AssignableExpression {

    private final Expression array;

    public IndexExpression(final Expression array, final List<Expression> expressions) {
        super(expressions);
        this.array = array;
    }

    public Expression getArray() {
        return array;
    }

}
