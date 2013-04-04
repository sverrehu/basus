package no.shhsoft.utils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UncheckedInvocationTargetException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UncheckedInvocationTargetException() {
        super();
    }

    public UncheckedInvocationTargetException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UncheckedInvocationTargetException(final String message) {
        super(message);
    }

    public UncheckedInvocationTargetException(final Throwable cause) {
        super(cause);
    }

}
