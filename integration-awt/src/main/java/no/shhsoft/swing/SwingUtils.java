package no.shhsoft.swing;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

import no.shhsoft.utils.UncheckedInterruptedException;
import no.shhsoft.utils.UncheckedInvocationTargetException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class SwingUtils {

    private SwingUtils() {
    }

    private static void invokeAndWait(final Runnable runnable, final boolean ignoreInterrupt) {
        if (EventQueue.isDispatchThread()) {
            runnable.run();
            return;
        }
        try {
            EventQueue.invokeAndWait(runnable);
        } catch (final InterruptedException e) {
            if (!ignoreInterrupt) {
                throw new UncheckedInterruptedException(e);
            }
        } catch (final InvocationTargetException e) {
            throw new UncheckedInvocationTargetException(e);
        }
    }

    public static void invokeAndWait(final Runnable runnable) {
        invokeAndWait(runnable, true);
    }

    public static void invokeLater(final Runnable runnable) {
        EventQueue.invokeLater(runnable);
    }

}
