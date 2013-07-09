package no.shhsoft.basus.value;

import java.awt.image.BufferedImage;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractImageValue
implements WidthAndHeightValue {

    private final BufferedImage image;

    AbstractImageValue(final BufferedImage image) {
        this.image = image;
    }

    public final BufferedImage getValue() {
        return image;
    }

    @Override
    public final int getHeight() {
        return image.getHeight();
    }

    @Override
    public final int getWidth() {
        return image.getWidth();
    }

}
