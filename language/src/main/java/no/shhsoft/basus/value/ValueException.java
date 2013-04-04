package no.shhsoft.basus.value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ValueException
extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValueException() {
    }

    public ValueException(final String message) {
        super(message);
    }

    public ValueException(final Throwable cause) {
        super(cause);
    }

    public ValueException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
