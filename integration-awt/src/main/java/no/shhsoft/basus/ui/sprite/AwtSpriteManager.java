package no.shhsoft.basus.ui.sprite;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class AwtSpriteManager implements SpriteManager {

    private final List<Sprite> sprites = new ArrayList<Sprite>();
    private static final DepthComparator DEPTH_COMPARATOR = new DepthComparator();

    private static class DepthComparator
    implements Comparator<Sprite> {

        @Override
        public int compare(final Sprite sprite1, final Sprite sprite2) {
            return sprite2.getDepth() - sprite1.getDepth();
        }

    }

    private int addSprite(final Sprite sprite) {
        synchronized (sprites) {
            sprites.add(sprite);
            return sprites.size() - 1;
        }
    }

    private Sprite getSprite(final int index) {
        synchronized (sprites) {
            return sprites.get(index);
        }
    }

    private static Rectangle getOverlapRectangle(final Sprite sprite1, final Sprite sprite2) {
        final Rectangle r1 = new Rectangle(sprite1.getX(), sprite1.getY(),
                                           sprite1.getWidth(), sprite1.getHeight());
        final Rectangle r2 = new Rectangle(sprite2.getX(), sprite2.getY(),
                                           sprite2.getWidth(), sprite2.getHeight());
        final Rectangle intersection = r1.intersection(r2);
        if (intersection.isEmpty()) {
            return null;
        }
        return intersection;
    }

    @Override
    public void removeSprites() {
        synchronized (sprites) {
            sprites.clear();
        }
    }

    @Override
    public Sprite[] getSpritesToDraw() {
        final List<Sprite> spritesToDraw;
        synchronized (sprites) {
            spritesToDraw = new ArrayList<Sprite>(sprites.size());
            for (final Sprite sprite : sprites) {
                if (sprite.isVisible()) {
                    spritesToDraw.add(sprite);
                }
            }
        }
        final Sprite[] ret = spritesToDraw.toArray(new Sprite[spritesToDraw.size()]);
        Arrays.sort(ret, DEPTH_COMPARATOR);
        return ret;
    }

    @Override
    public int createSprite(final BufferedImage image) {
        return addSprite(new Sprite(image));
    }

    @Override
    public int getSpriteWidth(final int index) {
        return getSprite(index).getWidth();
    }

    @Override
    public int getSpriteHeight(final int index) {
        return getSprite(index).getHeight();
    }

    @Override
    public synchronized boolean setSpriteDepth(final int index, final int depth) {
        return getSprite(index).setDepth(depth);
    }

    @Override
    public int getSpriteDepth(final int index) {
        return getSprite(index).getDepth();
    }

    @Override
    public synchronized boolean setSpritePosition(final int index, final int x, final int y) {
        return getSprite(index).setPosition(x, y);
    }

    @Override
    public int getSpriteX(final int index) {
        return getSprite(index).getX();
    }

    @Override
    public int getSpriteY(final int index) {
        return getSprite(index).getY();
    }

    @Override
    public synchronized boolean setSpriteVisible(final int index, final boolean visible) {
        return getSprite(index).setVisible(visible);
    }

    @Override
    public boolean isSpriteVisible(final int index) {
        return getSprite(index).isVisible();
    }

    @Override
    public synchronized boolean isSpriteCollision(final int index1, final int index2) {
        final Sprite sprite1 = getSprite(index1);
        final Sprite sprite2 = getSprite(index2);
        if (!sprite1.isVisible() || !sprite2.isVisible()) {
            return false;
        }
        final Rectangle overlap = getOverlapRectangle(sprite1, sprite2);
        if (overlap == null) {
            return false;
        }
        final BufferedImage image1 = sprite1.getImage();
        final BufferedImage image2 = sprite2.getImage();
        final int x1 = overlap.x - sprite1.getX();
        final int y1 = sprite1.getY() + sprite1.getHeight() - overlap.y - overlap.height;
        final int x2 = overlap.x - sprite2.getX();
        final int y2 = sprite2.getY() + sprite2.getHeight() - overlap.y - overlap.height;
        for (int x = overlap.width - 1; x >= 0; x--) {
            for (int y = overlap.height - 1; y >= 0; y--) {
                final int alpha1 = image1.getRGB(x1 + x, y1 + y) >>> 24;
                final int alpha2 = image2.getRGB(x2 + x, y2 + y) >>> 24;
                final boolean isVisible1 = alpha1 > 5;
                final boolean isVisible2 = alpha2 > 5;
                if (isVisible1 && isVisible2) {
                    return true;
                }
            }
        }
        return false;
    }

}
