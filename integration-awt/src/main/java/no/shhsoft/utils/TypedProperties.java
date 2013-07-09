package no.shhsoft.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class TypedProperties {

    private final Properties defaultProp = new Properties();
    private final Properties prop = new Properties(defaultProp);
    private boolean propsSaved = true;

    public void load(final String filename)
    throws IOException {
        final FileInputStream fin = new FileInputStream(filename);
        prop.loadFromXML(fin);
        fin.close();
        propsSaved = true;
    }

    public void save(final String filename)
    throws IOException {
        if (propsSaved) {
            return;
        }
        final FileOutputStream fout = new FileOutputStream(filename);
        prop.storeToXML(fout, "Generated file.  Do not edit.");
        fout.close();
        propsSaved = true;
    }

    public void uncheckedSave(final String filename) {
        try {
            save(filename);
        } catch (final IOException e) {
            System.err.println("problem saving `" + filename + "': "
                               + e.getMessage());
        }
    }

    public Properties getProps() {
        return prop;
    }

    public void setDefault(final String key, final String value) {
        defaultProp.put(key, value);
    }

    public void setDefault(final String key, final int value) {
        defaultProp.put(key, String.valueOf(value));
    }

    public void setDefault(final String key, final long value) {
        defaultProp.put(key, String.valueOf(value));
    }

    public void setDefault(final String key, final boolean value) {
        defaultProp.put(key, value ? "1" : "0");
    }

    public String get(final String key) {
        return prop.getProperty(key);
    }

    public void set(final String key, final String value) {
        prop.put(key, value);
        propsSaved = false;
    }

    public int getInt(final String key) {
        try {
            return Integer.parseInt(prop.getProperty(key));
        } catch (final NumberFormatException e) {
            return 0;
        }
    }

    public void set(final String key, final int value) {
        prop.put(key, String.valueOf(value));
        propsSaved = false;
    }

    public long getLong(final String key) {
        try {
            return Long.parseLong(prop.getProperty(key));
        } catch (final NumberFormatException e) {
            return 0L;
        }
    }

    public void set(final String key, final long value) {
        prop.put(key, String.valueOf(value));
        propsSaved = false;
    }

    public boolean getBoolean(final String key) {
        return (getInt(key) != 0);
    }

    public void set(final String key, final boolean value) {
        prop.put(key, value ? "1" : "0");
        propsSaved = false;
    }

    public void remove(final String key) {
        prop.remove(key);
        propsSaved = false;
    }

    @Override
    public String toString() {
        return prop.toString();
    }

}
