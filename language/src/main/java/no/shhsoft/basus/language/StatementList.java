package no.shhsoft.basus.language;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StatementList {

    private final List<Statement> statements = new ArrayList<>();

    public void addStatement(final Statement statement) {
        statements.add(statement);
    }

    public int getNumStatements() {
        return statements.size();
    }

    public Statement getStatement(final int idx) {
        return statements.get(idx);
    }

}
