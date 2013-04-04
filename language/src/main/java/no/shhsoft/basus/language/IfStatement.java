package no.shhsoft.basus.language;

import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class IfStatement
extends AbstractStatement {

    private final List<Expression> conditions;
    private final List<StatementList> conditionStatements;
    private final StatementList elseStatements;

    public IfStatement(final List<Expression> conditions,
                       final List<StatementList> conditionStatements,
                       final StatementList elseStatements) {
        this.conditions = conditions;
        this.conditionStatements = conditionStatements;
        this.elseStatements = elseStatements;
    }

    public List<Expression> getConditions() {
        return conditions;
    }

    public List<StatementList> getConditionStatements() {
        return conditionStatements;
    }

    public StatementList getElseStatements() {
        return elseStatements;
    }

}
