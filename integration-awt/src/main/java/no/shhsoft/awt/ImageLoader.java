package no.shhsoft.awt;

import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedIoException;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ImageLoader {

    private GraphicsConfiguration gc = null;
    private final Map<URL, CachedImage> images = new HashMap<URL, CachedImage>();

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

        public int incRefCount() {
            return ++refCount;
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
//            final double scaling = 0.0;
//            if (scaling != 0.0) {
//                op = new RescaleOp((float) scaling, 0.0f, null);
//            }
            img = createCompatibleImage(
                src.getWidth(), src.getHeight(), src.getColorModel().getTransparency() != Transparency.OPAQUE);
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

    public BufferedImage createCompatibleImage(final int width, final int height, final boolean hasAlphaChannel) {
        return getGraphicsConfiguration().createCompatibleImage(width, height,
                                                                hasAlphaChannel ? Transparency.TRANSLUCENT : Transparency.OPAQUE);
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

    public synchronized void releaseAll() {
        for (final CachedImage ci : images.values()) {
            ci.getImage().flush();
        }
        images.clear();
    }

}
