package no.shhsoft.awt;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.RescaleOp;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedIoException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ImageLoader {

    private GraphicsConfiguration gc = null;
    private final Map<URL, CachedImage> images = new HashMap<URL, CachedImage>();
    private double scaling = 0.0;

    private static class CachedImage {

        private final URL url;
        private final BufferedImage image;
        private int refCount;

        CachedImage(final URL key, final BufferedImage image) {
            this.url = key;
            this.image = image;
            refCount = 0;
        }

        public URL getUrl() {
            return url;
        }

        public BufferedImage getImage() {
            return image;
        }

        public int getRefcount() {
            return refCount;
        }

        public int incRefCount() {
            return ++refCount;
        }

        public int decRefCount() {
            return --refCount;
        }

    }

    private GraphicsConfiguration getGraphicsConfiguration() {
        if (gc != null) {
            return gc;
        }
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
        .getDefaultConfiguration();
    }

    private BufferedImage loadManagedImage(final URL url) {
        BufferedImage img = null;
        BufferedImage src;
        BufferedImageOp op = null;
        InputStream in = null;
        try {
            in = url.openStream();
            if (in == null) {
                throw new UncheckedIoException("No resource found for `" + url.toString() + "'");
            }
            src = ImageIO.read(new BufferedInputStream(in, 65536));
            if (scaling != 0.0) {
                op = new RescaleOp((float) scaling, 0.0f, null);
            }
            img = getGraphicsConfiguration().createCompatibleImage(
                src.getWidth(), src.getHeight(), src.getColorModel().getTransparency());
            final Graphics2D g2d = img.createGraphics();
            g2d.setComposite(AlphaComposite.Src);
            g2d.drawImage(src, op, 0, 0);
            g2d.dispose();
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        } finally {
            IoUtils.closeSilently(in);
        }
        return img;
    }

    public void setGraphicsConfiguration(final GraphicsConfiguration gc) {
        this.gc = gc;
    }

    public void setScaling(final double scaling) {
        this.scaling = scaling;
    }

    public synchronized BufferedImage get(final URL url) {
        CachedImage ci = images.get(url);
        if (ci == null) {
            final BufferedImage image = loadManagedImage(url);
            ci = new CachedImage(url, image);
            images.put(url, ci);
        }
        ci.incRefCount();
        return ci.getImage();
    }

    public synchronized void release(final String key) {
        final CachedImage ci = images.get(key);
        if (ci == null) {
            return;
        }
        if (ci.decRefCount() <= 0) {
            ci.getImage().flush();
            images.remove(key);
        }
    }

    public synchronized void releaseAll() {
        for (final CachedImage ci : images.values()) {
            ci.getImage().flush();
        }
        images.clear();
    }

}
