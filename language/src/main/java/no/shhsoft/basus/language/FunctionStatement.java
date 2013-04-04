package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FunctionStatement
extends AbstractStatement {

    private final String name;
    private final String[] argumentNames;
    private final StatementList statements;

    public FunctionStatement(final String name, final String[] argumentNames,
                             final StatementList statements) {
        this.name = name;
        this.argumentNames = argumentNames;
        this.statements = statements;
    }

    public String getName() {
        return name;
    }

    public String[] getArgumentNames() {
        return argumentNames;
    }

    public StatementList getStatements() {
        return statements;
    }

}
