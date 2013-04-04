package no.shhsoft.basus.value;

import no.shhsoft.basus.ui.sprite.SpriteManager;


/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class SpriteValue
implements WidthAndHeightValue {

    private final SpriteManager spriteManager;
    private final int spriteIndex;

    public SpriteValue(final SpriteManager spriteManager, final int spriteIndex) {
        this.spriteManager = spriteManager;
        this.spriteIndex = spriteIndex;
    }

    public int getValue() {
        return spriteIndex;
    }

    @Override
    public int getWidth() {
        return spriteManager.getSpriteWidth(spriteIndex);
    }

    @Override
    public int getHeight() {
        return spriteManager.getSpriteHeight(spriteIndex);
    }

}
