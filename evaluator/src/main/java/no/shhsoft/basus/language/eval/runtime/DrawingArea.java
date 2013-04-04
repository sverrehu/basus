package no.shhsoft.basus.language.eval.runtime;

import java.awt.image.BufferedImage;
import java.net.URL;

import no.shhsoft.basus.ui.sprite.SpriteManager;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface DrawingArea {

    void copyBackgroundImageFrom(DrawingArea other);

    BufferedImage getBackgroundImage();

    void reset();

    int getAreaWidth();

    int getAreaHeight();

    void setAutoFlush(boolean status);

    void flush();

    void clear();

    boolean isKeyPressed(int keyCode);

    void clearKeyboardBuffer();

    int waitForKeyPressed();

    void setBackgroundColor(int r, int g, int b);

    void setForegroundColor(int r, int g, int b, int a);

    void plot(int x, int y);

    void line(int x0, int y0, int x1, int y1);

    void rect(int x, int y, int width, int height);

    void fillRect(int x, int y, int width, int height);

    void circle(int x, int y, int radius);

    void fillCircle(int x, int y, int radius);

    void shape(int[] xPoints, int[] yPoints, int numPoints);

    void fillShape(int[] xPoints, int[] yPoints, int numPoints);

    int getRed(int x, int y);

    int getGreen(int x, int y);

    int getBlue(int x, int y);

    boolean isBackground(int x, int y);

    void captureBackgroundImage();

    void clearBackgroundImage();

    int getMouseX();

    int getMouseY();

    boolean isMouseButtonPressed(int num);

    BufferedImage loadImage(URL url);

    void drawImage(BufferedImage image, int x, int y);

    SpriteManager getSpriteManager();

    int createSprite(BufferedImage image);

    int getSpriteWidth(int index);

    int getSpriteHeight(int index);

    void setSpriteDepth(int index, int depth);

    int getSpriteDepth(int index);

    void setSpritePosition(int index, int x, int y);

    int getSpriteX(int index);

    int getSpriteY(int index);

    void setSpriteVisible(int index, boolean visible);

    boolean isSpriteVisible(int index);

    boolean isSpriteCollision(int spriteIndex1, int spriteIndex2);

}
