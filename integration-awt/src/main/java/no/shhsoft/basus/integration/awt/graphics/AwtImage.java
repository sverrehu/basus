package no.shhsoft.basus.integration.awt.graphics;

import java.awt.image.BufferedImage;

import no.shhsoft.basus.integration.api.graphics.Image;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class AwtImage
implements Image {

    private final BufferedImage image;

    public AwtImage(final BufferedImage image) {
        this.image = image;
    }

    @Override
    public int getWidth() {
        return image.getWidth();
    }

    @Override
    public int getHeight() {
        return image.getHeight();
    }

}
