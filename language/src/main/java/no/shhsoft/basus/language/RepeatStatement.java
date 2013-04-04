package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class RepeatStatement
extends AbstractStatement {

    private final Expression times;
    private final StatementList statements;

    public RepeatStatement(final Expression times, final StatementList statements) {
        this.times = times;
        this.statements = statements;
    }

    public Expression getTimes() {
        return times;
    }

    public StatementList getStatements() {
        return statements;
    }

}
