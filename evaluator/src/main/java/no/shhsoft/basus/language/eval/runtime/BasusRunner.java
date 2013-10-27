package no.shhsoft.basus.language.eval.runtime;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import no.shhsoft.basus.language.AbstractBasusException;
import no.shhsoft.basus.language.StatementList;
import no.shhsoft.basus.language.eval.EvaluationContext;
import no.shhsoft.basus.language.eval.Evaluator;
import no.shhsoft.basus.language.eval.SimpleEvaluationContext;
import no.shhsoft.basus.language.eval.StatementListener;
import no.shhsoft.basus.language.parser.BasusParser;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BasusRunner
implements Runnable {

    private Thread thread;
    private final List<RunStatusListener> runStatusListeners = new ArrayList<RunStatusListener>();
    private String program;
    private Throwable lastThrowable;
    private DrawingArea drawingArea;
    private Console console;
    private StatementListener statementListener;
    private Evaluator evaluator;
    private URL sourceUrl;
    private EvaluationContext context;

    private void notifyStarting() {
        for (final RunStatusListener listener : runStatusListeners) {
            listener.notifyStarting();
        }
    }

    private void notifyStopping() {
        for (final RunStatusListener listener : runStatusListeners) {
            listener.notifyStopping();
        }
    }

    @Override
    public void run() {
        lastThrowable = null;
        notifyStarting();
        if (drawingArea != null) {
            drawingArea.reset();
        }
        context = new SimpleEvaluationContext(console, drawingArea, sourceUrl);
        try {
            final StatementList statements = BasusParser.parse(program);
            evaluator = new Evaluator(statementListener);
            evaluator.evaluate(statements, context);
        } catch (final AbstractBasusException e) {
            lastThrowable = e;
        } catch (final StackOverflowError e) {
            System.err.println("Stack overflow");
            lastThrowable = e;
        } catch (final Throwable t) {
            lastThrowable = t;
//            t.printStackTrace(System.err);
        } finally {
            synchronized (this) {
                thread = null;
            }
            notifyStopping();
        }
    }

    public void addRunStatusListener(final RunStatusListener listener) {
        runStatusListeners.add(listener);
    }

    public synchronized void runProgram(@SuppressWarnings("hiding") final String program,
                                        @SuppressWarnings("hiding") final Console console,
                                        @SuppressWarnings("hiding") final DrawingArea drawingArea,
                                        @SuppressWarnings("hiding") final StatementListener statementListener,
                                        final URL sourceUrl) {
        if (thread != null) {
            throw new IllegalStateException("A program is already running");
        }
        thread = new Thread(this);
        this.console = console;
        this.drawingArea = drawingArea;
        this.program = program;
        this.statementListener = statementListener;
        this.sourceUrl = sourceUrl;
        thread.start();
    }

    public synchronized void stopProgram() {
        if (thread == null) {
            return;
        }
        if (evaluator != null) {
            evaluator.terminate();
        }
        thread.interrupt();
    }

    public void waitForProgramToFinish() {
        Thread t;
        synchronized (this) {
            t = thread;
            if (t == null) {
                return;
            }
        }
        try {
            t.join();
        } catch (final InterruptedException e) {
            System.out.println("interrupted");
        }
    }

    public Throwable getLastThrowable() {
        return lastThrowable;
    }

    public EvaluationContext getEvaluationContext() {
        return context;
    }

}
