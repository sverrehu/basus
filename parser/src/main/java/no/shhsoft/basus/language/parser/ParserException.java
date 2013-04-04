package no.shhsoft.basus.language.parser;

import no.shhsoft.basus.language.AbstractBasusException;
import no.shhsoft.basus.utils.TextLocation;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ParserException
extends AbstractBasusException {

    private static final long serialVersionUID = 1L;

    public ParserException() {
    }

    public ParserException(final String message) {
        super(message);
    }

    public ParserException(final Throwable cause) {
        super(cause);
    }

    public ParserException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ParserException(final TextLocation textLocation) {
        super(textLocation);
    }

    public ParserException(final String message, final TextLocation textLocation) {
        super(message, textLocation);
    }

    public ParserException(final String message, final Throwable cause,
                           final TextLocation textLocation) {
        super(message, cause, textLocation);
    }

    public ParserException(final Throwable cause, final TextLocation textLocation) {
        super(cause, textLocation);
    }

}
