package no.shhsoft.awt;

import java.awt.BufferCapabilities;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.ImageCapabilities;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class FullScreenFrame
extends Frame
implements WindowListener {

    private static final long serialVersionUID = 1L;
    private boolean hasCalledEnterFullScreen = false;
    private GraphicsDevice device;
    private DisplayMode oldDisplayMode = null;

    private void showGraphicsConfiguration(final GraphicsConfiguration gc) {
        System.out.println("  pixel size: " + gc.getColorModel().getPixelSize());
        System.out.println("  bounds: " + gc.getBounds().toString());
        final GraphicsDevice gd = gc.getDevice();
        System.out.println("  isFullScreenSupported: " + gd.isFullScreenSupported());
        System.out.println("  isDisplayChangeSupported: " + gd.isDisplayChangeSupported());
        System.out.println("  memory: " + gd.getAvailableAcceleratedMemory()
                           + (gd.getAvailableAcceleratedMemory() < 0 ? " (unlimited)" : ""));
        final DisplayMode dm = gd.getDisplayMode();
        System.out.println("  disp size: " + dm.getWidth() + "x" + dm.getHeight());
        System.out.println("  disp depth: " + dm.getBitDepth());
        System.out.println("  disp refresh: " + dm.getRefreshRate());
        final BufferCapabilities bc = gc.getBufferCapabilities();
        System.out.println("  full screen required: " + bc.isFullScreenRequired());
        System.out.println("  multi buffer available: " + bc.isMultiBufferAvailable());
        System.out.println("  page flipping: " + bc.isPageFlipping());
        final ImageCapabilities ic = gc.getImageCapabilities();
        System.out.println("  is accel: " + ic.isAccelerated());
    }

    protected FullScreenFrame() {
        addWindowListener(this);
    }

    protected void afterEnteringFullScreen() {
    }

    void afterLeavingFullScreen() {
    }

    public final FullScreenDisplayModeOptimizer.FullScreenTransform enterFullScreen(
                                                   final int wantedWidth, final int wantedHeight) {
        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = env.getDefaultScreenDevice();
        oldDisplayMode = device.getDisplayMode();
        final FullScreenDisplayModeOptimizer.DisplayModeAndTransform modeAndTransform
            = FullScreenDisplayModeOptimizer.getBestDisplayMode(wantedWidth, wantedHeight);
        if (modeAndTransform == null || modeAndTransform.getMode() == null) {
            System.err.println("no matching display mode found");
            return null;
        }
        final DisplayMode dispMode = modeAndTransform.getMode();
        System.out.println("using mode " + dispMode.getWidth() + "x" + dispMode.getHeight()
                           + " @ " + dispMode.getBitDepth());

        if (device.isFullScreenSupported()) {
            try {
                setUndecorated(true);
            } catch (final IllegalComponentStateException e) {
                System.err.println("Unable to setUndecorated(true)");
            }
            setIgnoreRepaint(true);
        }
        setResizable(false);
        try {
            if (device.isFullScreenSupported()) {
                device.setFullScreenWindow(this);
            }
            device.setDisplayMode(dispMode);
            System.out.println("current configuration:");
            showGraphicsConfiguration(getGraphicsConfiguration());
        } catch (final Throwable t) {
            System.err.println("error: " + t.getMessage());
            t.printStackTrace();
            return null;
        }

        hasCalledEnterFullScreen = true;
        setVisible(true);
        afterEnteringFullScreen();
        return modeAndTransform.getTransform();
    }

    public final boolean leaveFullScreen() {
        if (!hasCalledEnterFullScreen) {
            return false;
        }
        boolean ret = false;
        if (oldDisplayMode != null && !oldDisplayMode.equals(device.getDisplayMode())) {
            if (device.isDisplayChangeSupported()) {
                try {
                    device.setDisplayMode(oldDisplayMode);
                } catch (final Exception e) {
                    System.err.println("There was an error changing display mode: "
                                       + e.getMessage());
                }
            }
            oldDisplayMode = null;
        }
        if (device.isFullScreenSupported()) {
            device.setFullScreenWindow(null);
            ret = true;
        }
        setVisible(false);
        afterLeavingFullScreen();
        hasCalledEnterFullScreen = false;
        return ret;
    }

    public final boolean isFullScreenSupported() {
        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return env.getDefaultScreenDevice().isFullScreenSupported();
    }

    @Override
    public final void windowActivated(final WindowEvent e) {
        if (hasCalledEnterFullScreen) {
            device.setFullScreenWindow(this);
            validate();
        }
    }

    @Override
    public void windowClosed(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
    }

    @Override
    public final void windowDeactivated(final WindowEvent e) {
        if (hasCalledEnterFullScreen) {
            device.setFullScreenWindow(null);
        }
    }

    @Override
    public void windowDeiconified(final WindowEvent e) {
    }

    @Override
    public void windowIconified(final WindowEvent e) {
    }

    @Override
    public void windowOpened(final WindowEvent e) {
    }

}
