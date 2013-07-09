package no.shhsoft.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class VersionData {

    private final String version;
    private final String timeStamp;
    private final int serial;
    public static final String VERSION_PROPERTIES_NAME = "version.properties";

    private VersionData(final String version, final String timeStamp, final int serial) {
        this.version = version;
        this.timeStamp = timeStamp;
        this.serial = serial;
    }

    private static VersionData createFromProperties(final Properties properties) {
        final String version = properties.getProperty("version");
        final String timeStamp = properties.getProperty("timestamp");
        int serial;
        try {
            serial = Integer.valueOf(properties.getProperty("serial")).intValue();
        } catch (final NumberFormatException e) {
            serial = -1;
        }
        return new VersionData(version, timeStamp, serial);
    }

    private static Properties readProperties(final InputStream in) {
        final Properties properties = new Properties();
        try {
            properties.load(in);
            return properties;
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        } finally {
            IoUtils.closeSilently(in);
        }
    }

    private static Properties readPropertiesFromResource(final String resourceName) {
        return readProperties(VersionData.class.getResourceAsStream(resourceName));
    }

    private static Properties readPropertiesFromUrl(final URL url) {
        try {
            final URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            conn.addRequestProperty("Cache-Control", "no-cache");
            return readProperties(conn.getInputStream());
        } catch (final IOException e) {
            throw new UncheckedIoException(e);
        }
    }

    private static VersionData getVersionDataFromResource(final String resourceName) {
        return createFromProperties(readPropertiesFromResource(resourceName));
    }

    public static VersionData getApplicationVersionData() {
        return getVersionDataFromResource("/" + VERSION_PROPERTIES_NAME);
    }

    public static VersionData getVersionDataFromUrl(final URL url) {
        return createFromProperties(readPropertiesFromUrl(url));
    }

    public static VersionData getVersionDataFromUrl(final String url) {
        try {
            return createFromProperties(readPropertiesFromUrl(new URL(url)));
        } catch (final MalformedURLException e) {
            throw new UncheckedIoException(e);
        }
    }

    public String getVersion() {
        return version;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public int getSerial() {
        return serial;
    }

}
