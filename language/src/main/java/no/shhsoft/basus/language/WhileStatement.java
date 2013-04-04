package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class WhileStatement
extends AbstractStatement {

    private final Expression condition;
    private final StatementList statements;

    public WhileStatement(final Expression condition, final StatementList statements) {
        this.condition = condition;
        this.statements = statements;
    }

    public Expression getCondition() {
        return condition;
    }

    public StatementList getStatements() {
        return statements;
    }

}
