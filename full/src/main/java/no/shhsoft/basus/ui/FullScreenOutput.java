package no.shhsoft.basus.ui;

import java.awt.Color;
import java.awt.GridLayout;

import no.shhsoft.awt.FullScreenFrame;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FullScreenOutput
extends FullScreenFrame {

    private static final long serialVersionUID = 1L;
    private final OutputCanvas outputCanvas;

    public FullScreenOutput() {
        outputCanvas = new OutputCanvas();
        outputCanvas.requestFocusInWindow();
        setBackground(Color.BLACK);
    }

    public OutputCanvas getOutputCanvas() {
        return outputCanvas;
    }

    @Override
    public void afterEnteringFullScreen() {
        removeAll();
        setLayout(new GridLayout());
        add(outputCanvas);
        outputCanvas.componentShown(null);
        outputCanvas.requestFocusInWindow();
    }

}
