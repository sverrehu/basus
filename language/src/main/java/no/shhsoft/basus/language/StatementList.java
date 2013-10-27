package no.shhsoft.basus.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class StatementList {

    private final List<Statement> statements = new ArrayList<Statement>();

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
