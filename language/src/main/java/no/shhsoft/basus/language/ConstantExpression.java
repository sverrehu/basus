package no.shhsoft.basus.language;

import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ConstantExpression
extends AbstractExpression {

    private final Value constant;

    public ConstantExpression(final Value constant) {
        this.constant = constant;
    }

    public Value getConstant() {
        return constant;
    }

}
