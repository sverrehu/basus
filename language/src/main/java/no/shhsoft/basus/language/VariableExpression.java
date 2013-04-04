package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class VariableExpression
extends AbstractExpression
implements AssignableExpression {

    private final String variableName;

    public VariableExpression(final String variableName) {
        this.variableName = variableName;
    }

    public String getVariableName() {
        return variableName;
    }

}
