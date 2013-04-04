package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.language.AbstractBasusException;
import no.shhsoft.basus.utils.TextLocation;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class EvaluationException
extends AbstractBasusException {

    private static final long serialVersionUID = 1L;

    public EvaluationException(final TextLocation textLocation) {
        super(textLocation);
    }

    public EvaluationException(final String message, final TextLocation textLocation) {
        super(message, textLocation);
    }

    public EvaluationException(final String message, final Throwable cause,
                               final TextLocation textLocation) {
        super(message, cause, textLocation);
    }

    public EvaluationException(final Throwable cause, final TextLocation textLocation) {
        super(cause, textLocation);
    }

    public EvaluationException() {
    }

    public EvaluationException(final String message) {
        super(message);
    }

    public EvaluationException(final Throwable cause) {
        super(cause);
    }

    public EvaluationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
