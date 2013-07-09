package no.shhsoft.basus.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import no.shhsoft.awt.FullScreenDisplayModeOptimizer.FullScreenTransform;
import no.shhsoft.awt.ImageLoader;
import no.shhsoft.basus.language.eval.runtime.Console;
import no.shhsoft.basus.language.eval.runtime.DrawingArea;
import no.shhsoft.basus.language.eval.runtime.TerminationRequestListener;
import no.shhsoft.basus.ui.sprite.AwtSpriteManager;
import no.shhsoft.basus.ui.sprite.Sprite;
import no.shhsoft.basus.ui.sprite.SpriteManager;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class OutputCanvas
extends JComponent
implements Console, DrawingArea, ComponentListener, KeyListener,
    MouseMotionListener, MouseListener {

    private static final long serialVersionUID = 1L;
    public static final int AREA_WIDTH = 640;
    public static final int AREA_HEIGHT = 480;
    private int charX = 0;
    private int charY = 0;
    private int charWidth;
    private int charHeight;
    private int charBaseline;
    private int maxCharXPos;
    private BufferedImage screenImage;
    private BufferedImage bgImage;
    private Graphics2D screenGraphics;
    private final KeyboardBuffer keyboardBuffer = new KeyboardBuffer();
    private boolean cursorVisible;
    private Color backgroundColor;
    private Color foregroundColor;
    private final Set<Integer> keysPressed = new HashSet<Integer>();
    private boolean hasLastKeyCodePressed;
    private int lastKeyCodePressed;
    private boolean autoFlush = true;
    private boolean scalingNeeded = false;
    private double xScale;
    private double yScale;
    private BufferedImageOp screenImageOp = null;
    private int screenOffsetX = 0;
    private int screenOffsetY = 0;
    private int screenAreaWidth;
    private int screenAreaHeight;
    private boolean scaleAndOffsetLocked = false;
    private final List<TerminationRequestListener> terminationRequestListeners
        = new ArrayList<TerminationRequestListener>();
    private int realMouseX;
    private int realMouseY;
    private int mouseButtonFlags;
    private static final int MOUSE_BUTTON_1 = 1;
    private static final int MOUSE_BUTTON_2 = 2;
    private static final int MOUSE_BUTTON_3 = 4;
    private ImageLoader imageLoader;
    private final SpriteManager spriteManager = new AwtSpriteManager();
    private boolean initiated = false;

    private static int mapY(final int y) {
        return AREA_HEIGHT - y - 1;
    }

    private static int[] mapY(final int[] ys, final int numPoints) {
        final int[] mys = new int[numPoints];
        for (int q = numPoints - 1; q >= 0; q--) {
            mys[q] = mapY(ys[q]);
        }
        return mys;
    }

    private void terminationRequested() {
        synchronized (terminationRequestListeners) {
            for (final TerminationRequestListener listener : terminationRequestListeners) {
            listener.terminationRequested();
         }
        }
    }

    private synchronized void init() {
        if (!isDisplayable() || getWidth() <= 0 || getHeight() <= 0) {
            return;
        }
        screenImage = new BufferedImage(AREA_WIDTH, AREA_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
        screenGraphics = screenImage.createGraphics();
//        screenGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
//                                    RenderingHints.VALUE_ANTIALIAS_ON);
        if (getWidth() != AREA_WIDTH || getHeight() != AREA_HEIGHT) {
            setScaleAndOffset((double) getWidth() / AREA_WIDTH, (double) getHeight() / AREA_HEIGHT,
                              0, 0);

            screenImageOp = new AffineTransformOp(AffineTransform.getScaleInstance(xScale, yScale),
                                                  AffineTransformOp.TYPE_BILINEAR);
        } else {
            setScaleAndOffset(1.0, 1.0, 0, 0);
        }
        final Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
        screenGraphics.setFont(font);
        final FontMetrics fm = screenGraphics.getFontMetrics();
        charWidth = fm.stringWidth("a");
        charHeight = fm.getHeight();
        charBaseline = fm.getLeading() + fm.getAscent();
        maxCharXPos = (AREA_WIDTH / charWidth) * charWidth - charWidth;
        imageLoader = new ImageLoader();
        reset();
        setFocusTraversalKeysEnabled(false);
        initiated = true;
        notifyAll();
        flush();
    }

    private void updateMousePosition(final MouseEvent event) {
        realMouseX = event.getX();
        realMouseY = event.getY();
    }

    private synchronized void scroll() {
        final AffineTransformOp op = new AffineTransformOp(
                                            AffineTransform.getTranslateInstance(0.0, -charHeight),
                                            AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        final BufferedImage tmp = new BufferedImage(AREA_WIDTH, AREA_HEIGHT + charHeight,
                                                    screenImage.getType());
        tmp.createGraphics().drawImage(screenImage, null, 0, 0);
        op.filter(tmp, screenImage);
        final int clearHeight = charHeight + AREA_HEIGHT % charHeight;
        screenGraphics.clearRect(0, AREA_HEIGHT - clearHeight, AREA_WIDTH, clearHeight);
    }

    private synchronized void setScaleAndOffset(final double xScale, final double yScale,
                                                final int screenOffsetX, final int screenOffsetY) {
        if (scaleAndOffsetLocked) {
            return;
        }
        this.xScale = xScale;
        this.yScale = yScale;
        this.screenOffsetX = screenOffsetX;
        this.screenOffsetY = screenOffsetY;
        screenAreaWidth = (int) (xScale * AREA_WIDTH);
        screenAreaHeight = (int) (yScale * AREA_HEIGHT);
        if (xScale != 1.0 || yScale != 1.0) {
            screenImageOp = new AffineTransformOp(AffineTransform.getScaleInstance(xScale, yScale),
                                                  AffineTransformOp.TYPE_BILINEAR);
            scalingNeeded = true;
        } else {
            screenImageOp = null;
            scalingNeeded = false;
        }
    }

    private int toScreenX(final int x) {
        if (!scalingNeeded) {
            return screenOffsetX + x;
        }
        return screenOffsetX + (int) (x * xScale);
    }

    private int toScreenY(final int y) {
        if (!scalingNeeded) {
            return screenOffsetY + y;
        }
        return screenOffsetY + (int) (y * yScale);
    }

    private void advanceLine() {
        charX = 0;
        charY += charHeight;
        if (charY + charHeight > AREA_HEIGHT) {
            scroll();
            charY -= charHeight;
        }
    }

    private void advanceChar() {
        charX += charWidth;
        if (charX > maxCharXPos) {
            charX = 0;
            advanceLine();
        }
    }

    private void backLine() {
        charY -= charHeight;
        if (charY < 0) {
            charY = 0;
        }
    }

    private void backChar() {
        charX -= charWidth;
        if (charX < 0) {
            charX = maxCharXPos;
            backLine();
        }
    }

    private int getRgb(final int x, final int y) {
        if (x < 0 || x >= screenImage.getWidth() || y < 0 || y >= screenImage.getHeight()) {
            return 0;
        }
        return screenImage.getRGB(x, mapY(y));
    }

    private void conditionallyRepaint() {
        if (autoFlush) {
            repaint();
        }
    }

    private void putChar(final int x, final int y, final char c) {
        screenGraphics.clearRect(x, y, charWidth, charHeight);
        screenGraphics.drawChars(new char[] { c }, 0, 1, x, y + charBaseline);
    }

    public OutputCanvas() {
        setIgnoreRepaint(true);
        addComponentListener(this);
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);
        setSize(AREA_WIDTH, AREA_HEIGHT);
        setBackground(Color.BLACK);
        setDoubleBuffered(true);
        setOpaque(true);
        setFocusable(true);
        init();
    }

    private synchronized void flush(final Graphics g1) {
        final Graphics2D g = (Graphics2D) g1;
        g.setClip(screenOffsetX, screenOffsetY, screenAreaWidth, screenAreaHeight);
        g.setBackground(backgroundColor);
        g.setColor(foregroundColor);
        g.drawImage(screenImage, screenImageOp, screenOffsetX, screenOffsetY);
        if (isCursorVisible()) {
            g.drawLine(toScreenX(charX), toScreenY(charY + charBaseline),
                       toScreenX(charX + charWidth), toScreenY(charY + charBaseline));
        }
        final Sprite[] sprites = spriteManager.getSpritesToDraw();
        for (final Sprite sprite : sprites) {
            final int my = mapY(sprite.getY() + sprite.getHeight() - 1);
            g.drawImage(sprite.getImage(), screenImageOp,
                        toScreenX(sprite.getX()), toScreenY(my));
        }
    }

    @Override
    public void flush() {
        paintImmediately(0, 0, getWidth(), getHeight());
    }

    @Override
    public void update(final Graphics g) {
        /* Never called for Swing components, they say. */
        paint(g);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        /* This is the magic method for Swing. */
        flush(g);
    }

    @Override
    public void paint(final Graphics g) {
        flush(g);
    }

    @Override
    public void componentHidden(final ComponentEvent e) {
    }

    @Override
    public void componentMoved(final ComponentEvent e) {
    }

    @Override
    public void componentResized(final ComponentEvent e) {
        init();
    }

    @Override
    public void componentShown(final ComponentEvent e) {
        init();
    }

    @Override
    public void print(final String s) {
        boolean handleEscape = false;
        for (int q = 0; q < s.length(); q++) {
            final char c = s.charAt(q);
            if (handleEscape) {
                handleEscape = false;
                switch (c) {
                    case 'r':
                        charX = 0;
                        break;
                    case 'n':
                        advanceLine();
                        break;
                    case 'b':
                        backChar();
                        break;
                    case 't':
                        do {
                            advanceChar();
                        } while ((charX % 8) != 0);
                        break;
                    default:
                        putChar(charX, charY, c);
                        advanceChar();
                }
            } else {
                switch (c) {
                    case '\r':
                        charX = 0;
                        break;
                    case '\n':
                        advanceLine();
                        break;
                    case '\b':
                        backChar();
                        break;
                    case '\t':
                        do {
                            advanceChar();
                        } while ((charX % 8) != 0);
                        break;
                    case '\\':
                        handleEscape = true;
                        break;
                    default:
                        putChar(charX, charY, c);
                        advanceChar();
                }
            }
        }
        conditionallyRepaint();
    }

    @Override
    public void println(final String s) {
        print(s);
        advanceLine();
        conditionallyRepaint();
    }

    @Override
    public String readln() {
        setCursorVisible(true);
        try {
            final StringBuilder sb = new StringBuilder();
            for (;;) {
                final int c = readKey();
                if (c == Character.MIN_VALUE) {
                    /* terminated */
                    return sb.toString();
                }
                if (c == '\r' || c == '\n') {
                    println("");
                    return sb.toString();
                }
                if (c == '\b') {
                    if (sb.length() > 0) {
                        sb.setLength(sb.length() - 1);
                        print("\b \b");
                    }
                } else {
                    sb.append((char) c);
                    print(String.valueOf((char) c));
                }
            }
        } finally {
            setCursorVisible(false);
        }
    }

    @Override
    public int readKey() {
        return keyboardBuffer.nextCharacter();
    }

    @Override
    public void copyBackgroundImageFrom(final DrawingArea other) {
        other.getBackgroundImage().copyData(screenImage.getRaster());
    }

    @Override
    public BufferedImage getBackgroundImage() {
        return screenImage;
    }

    @Override
    public void reset() {
        bgImage = null;
        setBackgroundColor(0, 0, 0);
        setForegroundColor(255, 255, 255, 255);
        clear();
        keyboardBuffer.clear();
        setCursorVisible(false);
        hasLastKeyCodePressed = false;
        keysPressed.clear();
        autoFlush = true;
        mouseButtonFlags = 0;
        realMouseX = -1;
        realMouseY = -1;
        imageLoader.releaseAll();
        spriteManager.removeSprites();
    }

    @Override
    public int getAreaWidth() {
        return AREA_WIDTH;
    }

    @Override
    public int getAreaHeight() {
        return AREA_HEIGHT;
    }

    @Override
    public void clear() {
        if (bgImage != null) {
            screenGraphics.drawImage(bgImage, null, 0, 0);
        } else {
            screenGraphics.clearRect(0, 0, AREA_WIDTH, AREA_HEIGHT);
        }
        charX = 0;
        charY = 0;
        conditionallyRepaint();
    }

    @Override
    public void line(final int x0, final int y0, final int x1, final int y1) {
        screenGraphics.drawLine(x0, mapY(y0), x1, mapY(y1));
        conditionallyRepaint();
    }

    @Override
    public void plot(final int x, final int y) {
        final int my = mapY(y);
        screenGraphics.drawLine(x, my, x, my);
        conditionallyRepaint();
    }

    @Override
    public void setBackgroundColor(final int r, final int g, final int b) {
        backgroundColor = new Color(r, g, b);
        screenGraphics.setBackground(backgroundColor);
    }

    @Override
    public void setForegroundColor(final int r, final int g, final int b, final int a) {
        foregroundColor = new Color(r, g, b, a);
        screenGraphics.setColor(foregroundColor);
    }

    @Override
    public void rect(final int x, final int y, final int width, final int height) {
        screenGraphics.drawRect(x, mapY(y + height - 1), width - 1, height - 1);
        conditionallyRepaint();
    }

    @Override
    public void fillRect(final int x, final int y, final int width, final int height) {
        screenGraphics.fillRect(x, mapY(y + height - 1), width, height);
        conditionallyRepaint();
    }

    @Override
    public void circle(final int x, final int y, final int radius) {
        screenGraphics.drawOval(x - radius, mapY(y) - radius, 2 * radius, 2 * radius);
        conditionallyRepaint();
    }

    @Override
    public void fillCircle(final int x, final int y, final int radius) {
        screenGraphics.fillOval(x - radius, mapY(y) - radius, 2 * radius, 2 * radius);
        conditionallyRepaint();
    }

    @Override
    public void shape(final int[] xPoints, final int[] yPoints, final int numPoints) {
        screenGraphics.drawPolygon(xPoints, mapY(yPoints, numPoints), numPoints);
        conditionallyRepaint();
    }

    @Override
    public void fillShape(final int[] xPoints, final int[] yPoints, final int numPoints) {
        screenGraphics.fillPolygon(xPoints, mapY(yPoints, numPoints), numPoints);
        conditionallyRepaint();
    }

    @Override
    public int getRed(final int x, final int y) {
        return (getRgb(x, y) >> 16) & 0xff;
    }

    @Override
    public int getGreen(final int x, final int y) {
        return (getRgb(x, y) >> 8) & 0xff;
    }

    @Override
    public int getBlue(final int x, final int y) {
        return getRgb(x, y) & 0xff;
    }

    @Override
    public boolean isBackground(final int x, final int y) {
        return getRed(x, y) == backgroundColor.getRed()
            && getGreen(x, y) == backgroundColor.getGreen()
            && getBlue(x, y) == backgroundColor.getBlue();
    }

    @Override
    public BufferedImage loadImage(final URL url) {
        return imageLoader.get(url);
    }

    @Override
    public BufferedImage createCompatibleImage(final int width, final int height, final boolean hasAlphaChannel) {
        return imageLoader.createCompatibleImage(width, height, hasAlphaChannel);
    }

    @Override
    public void drawImage(final BufferedImage image, final int x, final int y) {
        final int my = mapY(y + image.getHeight() - 1);
        screenGraphics.drawImage(image, null, x, my);
        conditionallyRepaint();
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        final int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_F12) {
            terminationRequested();
            return;
        }
        final Integer keyCodeInteger = Integer.valueOf(keyCode);
        synchronized (keysPressed) {
            if (keysPressed.contains(keyCodeInteger)) {
                return;
            }
            keysPressed.add(keyCodeInteger);
            lastKeyCodePressed = keyCode;
            hasLastKeyCodePressed = true;
            keysPressed.notifyAll();
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        final int keyCode = e.getKeyCode();
        synchronized (keysPressed) {
            keysPressed.remove(Integer.valueOf(keyCode));
        }
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        keyboardBuffer.addCharacter(e.getKeyChar());
    }

    void setCursorVisible(final boolean cursorVisible) {
        this.cursorVisible = cursorVisible;
        conditionallyRepaint();
    }

    boolean isCursorVisible() {
        return cursorVisible;
    }

    @Override
    public boolean isKeyPressed(final int keyCode) {
        return keysPressed.contains(Integer.valueOf(keyCode));
    }

    @Override
    public void clearKeyboardBuffer() {
        synchronized (keysPressed) {
            keysPressed.clear();
        }
        keyboardBuffer.clear();
    }

    @Override
    public int waitForKeyPressed() {
        synchronized (keysPressed) {
            while (!hasLastKeyCodePressed) {
                try {
                    keysPressed.wait();
                } catch (final InterruptedException e) {
                    return 0;
                }
            }
            hasLastKeyCodePressed = false;
            return lastKeyCodePressed;
        }
    }

    @Override
    public void setAutoFlush(final boolean autoFlush) {
        this.autoFlush = autoFlush;
    }

    @Override
    public void captureBackgroundImage() {
        bgImage = new BufferedImage(screenImage.getWidth(), screenImage.getHeight(),
                                    screenImage.getType());
        bgImage.createGraphics().drawImage(screenImage, null, 0, 0);
    }

    @Override
    public void clearBackgroundImage() {
        bgImage = null;
    }

    public void addTerminationRequestListener(final TerminationRequestListener listener) {
        synchronized (terminationRequestListeners) {
            terminationRequestListeners.add(listener);
        }
    }

    public void removeTerminationRequestListener(final TerminationRequestListener listener) {
        synchronized (terminationRequestListeners) {
            terminationRequestListeners.remove(listener);
        }
    }

    @Override
    public int getMouseX() {
        if (realMouseX < 0) {
            return -1;
        }
        if (!scalingNeeded) {
            return realMouseX - screenOffsetX;
        }
        return (int) ((realMouseX - screenOffsetX) / xScale);
    }

    @Override
    public int getMouseY() {
        if (realMouseY < 0) {
            return -1;
        }
        if (!scalingNeeded) {
            return mapY(realMouseY - screenOffsetY);
        }
        return mapY((int) ((realMouseY - screenOffsetY) / yScale));
    }

    @Override
    public boolean isMouseButtonPressed(final int num) {
        switch (num) {
            case 1:
                return (mouseButtonFlags & MOUSE_BUTTON_1) != 0;
            case 2:
                return (mouseButtonFlags & MOUSE_BUTTON_2) != 0;
            case 3:
                return (mouseButtonFlags & MOUSE_BUTTON_3) != 0;
            default:
                return false;
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        updateMousePosition(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        updateMousePosition(e);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        mouseButtonFlags = 0;
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        mouseButtonFlags = 0;
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        requestFocus();
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                mouseButtonFlags |= MOUSE_BUTTON_1;
                break;
            case MouseEvent.BUTTON2:
                mouseButtonFlags |= MOUSE_BUTTON_2;
                break;
            case MouseEvent.BUTTON3:
                mouseButtonFlags |= MOUSE_BUTTON_3;
                break;
            default:
                /* nothing */
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                mouseButtonFlags &= ~MOUSE_BUTTON_1;
                break;
            case MouseEvent.BUTTON2:
                mouseButtonFlags &= ~MOUSE_BUTTON_2;
                break;
            case MouseEvent.BUTTON3:
                mouseButtonFlags &= ~MOUSE_BUTTON_3;
                break;
            default:
                /* nothing */
        }
    }

    @Override
    public SpriteManager getSpriteManager() {
        return spriteManager;
    }

    @Override
    public int createSprite(final BufferedImage image) {
        return spriteManager.createSprite(image);
    }

    @Override
    public int getSpriteWidth(final int index) {
        return spriteManager.getSpriteWidth(index);
    }

    @Override
    public int getSpriteHeight(final int index) {
        return spriteManager.getSpriteHeight(index);
    }

    @Override
    public void setSpriteDepth(final int index, final int depth) {
        if (spriteManager.setSpriteDepth(index, depth)) {
            conditionallyRepaint();
        }
    }

    @Override
    public int getSpriteDepth(final int index) {
        return spriteManager.getSpriteDepth(index);
    }

    @Override
    public void setSpritePosition(final int index, final int x, final int y) {
        if (spriteManager.setSpritePosition(index, x, y)) {
            conditionallyRepaint();
        }
    }

    @Override
    public int getSpriteX(final int index) {
        return spriteManager.getSpriteX(index);
    }

    @Override
    public int getSpriteY(final int index) {
        return spriteManager.getSpriteY(index);
    }

    @Override
    public void setSpriteVisible(final int index, final boolean visible) {
        if (spriteManager.setSpriteVisible(index, visible)) {
            conditionallyRepaint();
        }
    }

    @Override
    public boolean isSpriteVisible(final int index) {
        return spriteManager.isSpriteVisible(index);
    }

    @Override
    public boolean isSpriteCollision(final int index1, final int index2) {
        return spriteManager.isSpriteCollision(index1, index2);
    }

    public synchronized void setFullScreenTransform(final FullScreenTransform fullScreenTransform) {
        scaleAndOffsetLocked = false;
        setScaleAndOffset(fullScreenTransform.getXScale(), fullScreenTransform.getYScale(),
                          fullScreenTransform.getXOffset(), fullScreenTransform.getYOffset());
        scaleAndOffsetLocked = true;
    }

    public synchronized void waitUntilInitiated() {
        while (!initiated) {
            try {
                wait();
            } catch (final InterruptedException e) {
                System.out.println("Interrupted");
            }
        }
    }

}
