package no.shhsoft.utils;

import java.io.IOException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class AppProps {

    private static String applicationName = null;
    private static TypedProperties props = new TypedProperties();

    private AppProps() {
        /* not to be instantiated */
    }

    /* includes trailing separator */
    private static String getUserHome() {
        final String sep = System.getProperty("file.separator", "/");
        String home = System.getProperty("user.home", "");
        if (home.length() > 0 && !home.endsWith(sep)) {
            home += sep;
        }
        return home;
    }

    private static boolean isUnix() {
        return System.getProperty("file.separator").equals("/");
    }

    private static String getPropertiesFilename() {
        if (isUnix()) {
            /* assume some kind of Unix */
            return getUserHome() + "." + applicationName + ".cfg";
        }
        /* assume Windows. sorry, Mac folks, I don't know how to
         * identify a Mac. */
        return getUserHome() + applicationName + ".cfg";
    }

    public static void setApplicationName(final String applicationName) {
        AppProps.applicationName = applicationName;
    }

    public static void load()
    throws IOException {
        props.load(getPropertiesFilename());
    }

    public static void save()
    throws IOException {
        props.save(getPropertiesFilename());
    }

    public static void uncheckedSave() {
        props.uncheckedSave(getPropertiesFilename());
    }

    public static void setDefault(final String key, final String value) {
        props.setDefault(key, value);
    }

    public static void setDefault(final String key, final int value) {
        props.setDefault(key, String.valueOf(value));
    }

    public static void setDefault(final String key, final long value) {
        props.setDefault(key, String.valueOf(value));
    }

    public static void setDefault(final String key, final boolean value) {
        props.setDefault(key, value ? "1" : "0");
    }

    public static String get(final String key) {
        return props.get(key);
    }

    public static void set(final String key, final String value) {
        props.set(key, value);
    }

    public static int getInt(final String key) {
        return props.getInt(key);
    }

    public static void set(final String key, final int value) {
        props.set(key, value);
    }

    public static long getLong(final String key) {
        return props.getLong(key);
    }

    public static void set(final String key, final long value) {
        props.set(key, value);
    }

    public static boolean getBoolean(final String key) {
        return props.getBoolean(key);
    }

    public static void set(final String key, final boolean value) {
        props.set(key, value);
    }

    public static void remove(final String key) {
        props.remove(key);
    }

}
