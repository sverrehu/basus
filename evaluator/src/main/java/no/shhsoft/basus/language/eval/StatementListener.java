package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.language.Statement;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface StatementListener {

    void startExecuting(Statement statement, EvaluationContext context);

    void endExecuting(Statement statement, EvaluationContext context);

}
