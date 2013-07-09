package no.shhsoft.awt;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FullScreenDisplayModeOptimizer {

    public static final class DisplayModeAndTransform {

        private final DisplayMode mode;
        private final FullScreenTransform transform;
        private final int score;

        public DisplayModeAndTransform(final DisplayMode mode,
                                        final FullScreenTransform transform, final int score) {
            this.mode = mode;
            this.transform = transform;
            this.score = score;
        }

        public DisplayMode getMode() {
            return mode;
        }

        public FullScreenTransform getTransform() {
            return transform;
        }

        public int getScore() {
            return score;
        }

    }

    private static class ModeAndScoreComparator
    implements Comparator<DisplayModeAndTransform> {

        @Override
        public int compare(final DisplayModeAndTransform o1, final DisplayModeAndTransform o2) {
            int diff = o2.getScore() - o1.getScore();
            if (diff != 0) {
                return diff;
            }
            diff = o2.getMode().getBitDepth() - o1.getMode().getBitDepth();
            if (diff != 0) {
                return diff;
            }
            return o2.getMode().getRefreshRate() - o1.getMode().getRefreshRate();
        }

    }

    public static final class FullScreenTransform {

        private final double xScale;
        private final double yScale;
        private final int xOffset;
        private final int yOffset;

        public FullScreenTransform(final double xScale, final double yScale, final int xOffset, final int yOffset) {
            this.xScale = xScale;
            this.yScale = yScale;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }

        public double getXScale() {
            return xScale;
        }

        public double getYScale() {
            return yScale;
        }

        public int getXOffset() {
            return xOffset;
        }

        public int getYOffset() {
            return yOffset;
        }

        @Override
        public String toString() {
            return "xScale: " + xScale + ", yScale: " + yScale + ", xOffset: " + xOffset + ", yOffset: " + yOffset;
        }

    }

    private FullScreenDisplayModeOptimizer() {
    }

    private static boolean almostEqual(final double d0, final double d1) {
        final long l0 = (long) (d0 * 32768.0);
        final long l1 = (long) (d1 * 32768.0);
        return l0 == l1;
    }

    private static FullScreenTransform calculateTransform(final int squarePixelWidth,
                                                          final int squarePixelHeight,
                                                          final int modeWidth,
                                                          final int modeHeight,
                                                          final int wantedWidth,
                                                          final int wantedHeight) {
        /* assumption: square pixes are wanted. */
        final double squarePixelRatio = (double) squarePixelWidth / squarePixelHeight;
        final double modeRatio = (double) modeWidth / modeHeight;
        double xScale = 1.0;
        double yScale = 1.0;
        if (!almostEqual(squarePixelRatio, modeRatio)) {
            /* adjust to have square pixels. */
            xScale *= modeRatio / squarePixelRatio;
        }
        final int usableWidth = (int) (modeWidth * xScale);
        final int usableHeight = (int) (modeHeight * yScale);
        /* try to maximize height first. */
        double scale = (double) usableHeight / wantedHeight;
        if (scale * wantedWidth > usableWidth) {
            /* didn't work.  maximize width instead. */
            scale = (double) usableWidth / wantedWidth;
        }
        xScale *= scale;
        yScale *= scale;
        final double[] normals = { 0.25, 0.50, 0.75, 1.00, 1.25, 1.50, 1.75, 2.00, 2.25, 2.50, 2.75,
                                   3.00, 3.25, 3.50, 3.75, 4.00 };
        for (final double normal : normals) {
            if (almostEqual(xScale, normal)) {
                xScale = normal;
            }
            if (almostEqual(yScale, normal)) {
                yScale = normal;
            }
        }
        final int actualWidth = (int) (xScale * wantedWidth);
        final int actualHeight = (int) (yScale * wantedHeight);
        final int xOffset = (modeWidth - actualWidth) / 2;
        final int yOffset = (modeHeight - actualHeight) / 2;
        return new FullScreenTransform(xScale, yScale, xOffset, yOffset);
    }

    private static int score(final FullScreenTransform transform,
                             final int modeWidth, final int modeHeight) {
        int score = 0;
        if (transform.getXScale() == 1.0) {
            score += 100;
        }
        if (transform.getYScale() == 1.0) {
            score += 100;
        }
        if (transform.getXScale() == 0.5 || transform.getXScale() == 2.0) {
            score += 50;
        }
        if (transform.getYScale() == 0.5 || transform.getYScale() == 2.0) {
            score += 50;
        }
        if (almostEqual(transform.getXScale(), transform.getYScale())) {
            score += 20;
        }
        if (transform.getXScale() >= 1.0 && transform.getYScale() >= 1.0) {
            score += 10;
        }
        final double fractionalOffsetX = transform.getXOffset() / (transform.getXScale() * modeWidth);
        final double fractionalOffsetY = transform.getYOffset() / (transform.getYScale() * modeHeight);
        score -= 10 * fractionalOffsetX + 10 * fractionalOffsetY;
        return score;
    }

    private static DisplayModeAndTransform[] getScoredDisplayModes(final int wantedWidth,
                                                                   final int wantedHeight) {
        final GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        final GraphicsDevice device = env.getDefaultScreenDevice();
        final DisplayMode currentDisplayMode = device.getDisplayMode();
        final int nativeWidth = currentDisplayMode.getWidth();
        final int nativeHeight = currentDisplayMode.getHeight();
        final DisplayModeAndTransform[] modeAndScores
            = new DisplayModeAndTransform[device.getDisplayModes().length];
        int index = 0;
        for (final DisplayMode mode : device.getDisplayModes()) {
            final FullScreenTransform transform = calculateTransform(
                nativeWidth, nativeHeight, mode.getWidth(), mode.getHeight(),
                wantedWidth, wantedHeight);
            modeAndScores[index++] = new DisplayModeAndTransform(mode, transform,
                                                                  score(transform,
                                                                        mode.getWidth(),
                                                                        mode.getHeight()));
        }
        Arrays.sort(modeAndScores, new ModeAndScoreComparator());
        return modeAndScores;
    }

    public static DisplayModeAndTransform getBestDisplayMode(final int wantedWidth,
                                                             final int wantedHeight) {
        final DisplayModeAndTransform[] modes = getScoredDisplayModes(wantedWidth, wantedHeight);
        if (modes == null || modes.length == 0) {
            return null;
        }
        return modes[0];
    }

    public static void main(final String[] args) {
        for (final DisplayModeAndTransform modeAndTransform : getScoredDisplayModes(640, 480)) {
            final int w = modeAndTransform.getMode().getWidth();
            final int h = modeAndTransform.getMode().getHeight();
            System.out.println(modeAndTransform.getScore() + ": " + w + "x" + h + "@"
                               + modeAndTransform.getMode().getBitDepth()
                               + ", ratio: " + ((double) w / h)
                               + " (" + modeAndTransform.getTransform() + ")");
        }
    }

}
