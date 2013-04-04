package no.shhsoft.swing;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;

import no.shhsoft.utils.AppProps;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public class AppFrame
extends JFrame
implements ComponentListener, WindowStateListener {

    private static final long serialVersionUID = 1L;
    private final String propertyPrefix;
    private static final String LOCATION_Y = ".location.y";
    private static final String LOCATION_X = ".location.x";
    private static final String DIMENSION_HEIGHT = ".dimension.height";
    private static final String DIMENSION_WIDTH = ".dimension.width";
    private static final String MAXIMIZED = ".maximized";

    private void setCurrentLocation(final int x, final int y) {
        AppProps.set(propertyPrefix + LOCATION_X, x);
        AppProps.set(propertyPrefix + LOCATION_Y, y);
    }

    private void setCurrentDimension(final int width, final int height) {
        AppProps.set(propertyPrefix + DIMENSION_WIDTH, width);
        AppProps.set(propertyPrefix + DIMENSION_HEIGHT, height);
    }

    private void setCurrentMaximized(final boolean state) {
        AppProps.set(propertyPrefix + MAXIMIZED, state);
    }

    public final void setDefaultLocation(final int x, final int y) {
        AppProps.setDefault(propertyPrefix + LOCATION_X, x);
        AppProps.setDefault(propertyPrefix + LOCATION_Y, y);
    }

    public final Point getWantedLocation() {
        return new Point(AppProps.getInt(propertyPrefix + LOCATION_X),
                         AppProps.getInt(propertyPrefix + LOCATION_Y));
    }

    public final void setDefaultDimension(final int width, final int height) {
        AppProps.setDefault(propertyPrefix + DIMENSION_WIDTH, width);
        AppProps.setDefault(propertyPrefix + DIMENSION_HEIGHT, height);
    }

    public final Dimension getWantedDimension() {
        return new Dimension(AppProps.getInt(propertyPrefix + DIMENSION_WIDTH),
                             AppProps.getInt(propertyPrefix + DIMENSION_HEIGHT));
    }

    public final void setDefaultMaximized(final boolean state) {
        AppProps.setDefault(propertyPrefix + MAXIMIZED, state);
    }

    public final boolean getWantedMaximized() {
        return AppProps.getBoolean(propertyPrefix + MAXIMIZED);
    }

    public AppFrame(final String propertyPrefix) {
        this.propertyPrefix = propertyPrefix;
        addComponentListener(this);
        addWindowStateListener(this);
    }

    @Override
    public final void componentResized(final ComponentEvent e) {
        final Dimension dimension = getSize();
        setCurrentDimension(dimension.width, dimension.height);
    }

    @Override
    public final void componentMoved(final ComponentEvent e) {
        /* Top level frames need not always be visible. To avoid
         * java.awt.IllegalComponentStateException, we perform the
         * following test. */
        if (isVisible()) {
            final Point point = getLocationOnScreen();
            setCurrentLocation(point.x, point.y);
        }
    }

    @Override
    public final void componentShown(final ComponentEvent e) {
        setLocation(getWantedLocation());
        setSize(getWantedDimension());
        if (getWantedMaximized()) {
            setExtendedState(MAXIMIZED_BOTH);
        }
    }

    @Override
    public final void componentHidden(final ComponentEvent e) {
    }

    @Override
    public final void windowStateChanged(final WindowEvent e) {
        setCurrentMaximized((e.getNewState() & MAXIMIZED_BOTH) != 0);
    }

    @Override
    public final void pack() {
        final Dimension dimension = getWantedDimension();
        final Point location = getWantedLocation();
        super.pack();
        setSize(dimension);
        setLocation(location);
    }

}
