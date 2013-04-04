package no.shhsoft.basus.ui.ide.debug;

import no.shhsoft.basus.language.Statement;
import no.shhsoft.basus.language.eval.EvaluationContext;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface StepListener {

    void beginStatement(Statement statement, EvaluationContext context);

    void endStatement(Statement statement, EvaluationContext context);

}
