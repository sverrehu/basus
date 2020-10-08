package no.shhsoft.basus.ui.ide.debug;

import java.util.ArrayList;
import java.util.List;

import no.shhsoft.basus.language.Statement;
import no.shhsoft.basus.language.eval.EvaluationContext;
import no.shhsoft.basus.language.eval.StatementListener;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class DebugController
implements StatementListener {

    private final List<StepListener> stepListeners = new ArrayList<>();

    private void notifyBeginStatement(final Statement statement, final EvaluationContext context) {
        synchronized (stepListeners) {
            for (final StepListener stepListener : stepListeners) {
                stepListener.beginStatement(statement, context);
            }
        }
    }

    private void notifyEndStatement(final Statement statement, final EvaluationContext context) {
        synchronized (stepListeners) {
            for (final StepListener stepListener : stepListeners) {
                stepListener.endStatement(statement, context);
            }
        }
    }

    public void addStepListener(final StepListener stepListener) {
        synchronized (stepListeners) {
            stepListeners.add(stepListener);
        }
    }

    public void removeStepListener(final StepListener stepListener) {
        synchronized (stepListeners) {
            stepListeners.remove(stepListener);
        }
    }

    @Override
    public void startExecuting(final Statement statement, final EvaluationContext context) {
        notifyBeginStatement(statement, context);
    }

    @Override
    public void endExecuting(final Statement statement, final EvaluationContext context) {
        notifyEndStatement(statement, context);
    }

}
