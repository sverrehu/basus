package no.shhsoft.basus.language;

import no.shhsoft.basus.utils.TextLocation;
import no.shhsoft.basus.utils.TextLocationHolder;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractTextLocationHolder
implements TextLocationHolder {

    private TextLocation startLocation;
    private TextLocation endLocation;

    protected AbstractTextLocationHolder() {
    }

    protected AbstractTextLocationHolder(final TextLocation startLocation,
                                         final TextLocation endLocation) {
        setStartLocation(startLocation);
        setEndLocation(endLocation);
    }

    @Override
    public final void setStartLocation(final TextLocation startLocation) {
        this.startLocation = startLocation;
    }

    @Override
    public final TextLocation getStartLocation() {
        return startLocation;
    }

    @Override
    public final void setEndLocation(final TextLocation endLocation) {
        this.endLocation = endLocation;
    }

    @Override
    public final TextLocation getEndLocation() {
        return endLocation;
    }

}
