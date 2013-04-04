package no.shhsoft.basus.ui.sprite;

import java.awt.image.BufferedImage;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface SpriteManager {

    void removeSprites();

    Sprite[] getSpritesToDraw();

    int createSprite(BufferedImage image);

    int getSpriteWidth(int index);

    int getSpriteHeight(int index);

    boolean setSpriteDepth(int index, int depth);

    int getSpriteDepth(int index);

    boolean setSpritePosition(int index, int x, int y);

    int getSpriteX(int index);

    int getSpriteY(int index);

    boolean setSpriteVisible(int index, boolean visible);

    boolean isSpriteVisible(int index);

    boolean isSpriteCollision(int index1, int index2);

}
