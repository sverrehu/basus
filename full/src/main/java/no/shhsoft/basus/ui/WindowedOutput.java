package no.shhsoft.basus.ui;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class WindowedOutput
extends Frame
implements WindowListener {

    private static final long serialVersionUID = 1L;
    private final OutputCanvas outputCanvas;

    private void shutdown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        }).start();
    }

    public WindowedOutput() {
        super("Basus");
        outputCanvas = new OutputCanvas();
        setBackground(Color.BLACK);
        setSize(OutputCanvas.AREA_WIDTH, OutputCanvas.AREA_HEIGHT);
        setLocation(10, 10);
        setVisible(true);
        setResizable(false);

        addWindowListener(new WindowAdapter() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void windowClosing(final WindowEvent e) {
                shutdown();
            }
        });
        addWindowListener(this);

        add(outputCanvas);

        /* this second call to setVisible(true) is somehow needed on my home computer. */
        setVisible(true);
        outputCanvas.requestFocusInWindow();
    }

    public OutputCanvas getOutputCanvas() {
        return outputCanvas;
    }

    @Override
    public void windowActivated(final WindowEvent e) {
        outputCanvas.requestFocusInWindow();
    }

    @Override
    public void windowClosed(final WindowEvent e) {
    }

    @Override
    public void windowClosing(final WindowEvent e) {
    }

    @Override
    public void windowDeactivated(final WindowEvent e) {
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
