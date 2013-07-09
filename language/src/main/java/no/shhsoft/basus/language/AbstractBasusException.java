package no.shhsoft.basus.language;

import no.shhsoft.basus.utils.TextLocation;
import no.shhsoft.basus.utils.TextLocationHolder;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractBasusException
extends RuntimeException
implements TextLocationHolder {

    private static final long serialVersionUID = 1L;
    private TextLocation textLocation;

    protected AbstractBasusException() {
    }

    protected AbstractBasusException(final String message) {
        super(message);
    }

    protected AbstractBasusException(final Throwable cause) {
        super(cause);
    }

    protected AbstractBasusException(final String message, final Throwable cause) {
        super(message, cause);
    }

    protected AbstractBasusException(final TextLocation textLocation) {
        setTextLocation(textLocation);
    }

    protected AbstractBasusException(final String message, final TextLocation textLocation) {
        super(message);
        setTextLocation(textLocation);
    }

    protected AbstractBasusException(final Throwable cause, final TextLocation textLocation) {
        super(cause);
        setTextLocation(textLocation);
    }

    protected AbstractBasusException(final String message, final Throwable cause,
                                     final TextLocation textLocation) {
        super(message, cause);
        setTextLocation(textLocation);
    }

    final void setTextLocation(final TextLocation textLocation) {
        this.textLocation = textLocation;
    }

    public final TextLocation getTextLocation() {
        return textLocation;
    }

    @Override
    public final TextLocation getStartLocation() {
        return textLocation;
    }

    @Override
    public final TextLocation getEndLocation() {
        return textLocation;
    }

    @Override
    public final void setStartLocation(final TextLocation startLocation) {
        textLocation = startLocation;
    }

    @Override
    public final void setEndLocation(final TextLocation endLocation) {
        textLocation = endLocation;
    }

}
