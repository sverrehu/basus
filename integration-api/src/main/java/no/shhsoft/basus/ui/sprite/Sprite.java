package no.shhsoft.basus.ui.sprite;

import java.awt.image.BufferedImage;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Sprite {

    private final BufferedImage image;
    /* x and y are Basus coordinates: lower left corner. */
    private int x;
    private int y;
    private boolean visible;
    private int depth;

    public Sprite(final BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public boolean setPosition(final int x, final int y) {
        final boolean changed = (this.x != x || this.y != y) && visible;
        this.x = x;
        this.y = y;
        return changed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean setVisible(final boolean visible) {
        final boolean changed = this.visible != visible;
        this.visible = visible;
        return changed;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean setDepth(final int depth) {
        final boolean changed = this.depth != depth && visible;
        this.depth = depth;
        return changed;
    }

    public int getDepth() {
        return depth;
    }

}
