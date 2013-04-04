package no.shhsoft.basus.ui.ide.debug.variables;

import javax.swing.JTable;

import no.shhsoft.basus.language.Statement;
import no.shhsoft.basus.language.eval.EvaluationContext;
import no.shhsoft.basus.ui.ide.debug.StepListener;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class VariablesPane
extends JTable
implements StepListener {

    private static final long serialVersionUID = 1L;

    public VariablesPane(final VariablesModel model) {
        super(model);
        setFillsViewportHeight(true);
    }

    @Override
    public void beginStatement(final Statement statement, final EvaluationContext context) {
        ((VariablesModel) getModel()).update(context);
    }

    @Override
    public void endStatement(final Statement statement, final EvaluationContext context) {
    }

}
