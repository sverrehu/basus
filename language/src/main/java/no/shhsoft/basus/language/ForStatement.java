package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ForStatement
extends AbstractStatement {

    private final AssignableExpression assignable;
    private final Expression from;
    private final Expression to;
    private final Expression step;
    private final StatementList statements;

    public ForStatement(final AssignableExpression assignable, final Expression from,
                        final Expression to, final Expression step,
                        final StatementList statements) {
        this.assignable = assignable;
        this.from = from;
        this.to = to;
        this.step = step;
        this.statements = statements;
    }

    public AssignableExpression getAssignable() {
        return assignable;
    }

    public Expression getFrom() {
        return from;
    }

    public Expression getTo() {
        return to;
    }

    public Expression getStep() {
        return step;
    }

    public StatementList getStatements() {
        return statements;
    }

}
