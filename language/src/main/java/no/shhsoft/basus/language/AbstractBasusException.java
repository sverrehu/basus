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

    public AbstractBasusException() {
    }

    public AbstractBasusException(final String message) {
        super(message);
    }

    public AbstractBasusException(final Throwable cause) {
        super(cause);
    }

    public AbstractBasusException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public AbstractBasusException(final TextLocation textLocation) {
        setTextLocation(textLocation);
    }

    public AbstractBasusException(final String message, final TextLocation textLocation) {
        super(message);
        setTextLocation(textLocation);
    }

    public AbstractBasusException(final Throwable cause, final TextLocation textLocation) {
        super(cause);
        setTextLocation(textLocation);
    }

    public AbstractBasusException(final String message, final Throwable cause,
                                  final TextLocation textLocation) {
        super(message, cause);
        setTextLocation(textLocation);
    }

    public final void setTextLocation(final TextLocation textLocation) {
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
