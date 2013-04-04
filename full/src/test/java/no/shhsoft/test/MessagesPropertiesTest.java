package no.shhsoft.test;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import no.shhsoft.i18n.I18N;
import no.shhsoft.utils.IoUtils;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class MessagesPropertiesTest {

    private static final String PROP_PREFIX = "messages";
    private static final String PROP_SUFFIX = ".properties";

    private static Properties master;
    private static String masterResource;

    private static Properties loadProperties(final String resourceName) {
        final URL resourceUrl
            = MessagesPropertiesTest.class.getClassLoader().getResource(resourceName);
        if (resourceUrl == null) {
            throw new RuntimeException("Unable to load properties from `" + resourceName
                                       + "'.  No such resource.");
        }
        InputStream is = null;
        try {
            is = resourceUrl.openStream();
            final Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (final IOException e) {
            throw new RuntimeException("Unable to load properties from `" + resourceName + "'", e);
        } finally {
            IoUtils.closeSilently(is);
        }
    }

    private String[] findMissingKeys(final Properties parent, final Properties child) {
        final Set<String> missing = new HashSet<String>();
        for (final Object key : parent.keySet()) {
            if (child.get(key) == null) {
                missing.add((String) key);
            }
        }
        final String[] ret = missing.toArray(new String[missing.size()]);
        Arrays.sort(ret);
        return ret;
    }

    @BeforeClass
    public static void beforeClass() {
        masterResource = PROP_PREFIX + PROP_SUFFIX;
        master = loadProperties(masterResource);
    }

    @Test
    public void testAllLanguageFiles() {
        boolean ok = true;
        for (final String language : I18N.getSupportedLanguages()) {
            if (language.equals(I18N.getDefaultLocale().getLanguage())) {
                continue;
            }
            final String resourceName = PROP_PREFIX + "_" + language + PROP_SUFFIX;
            final Properties props = loadProperties(resourceName);
            String[] missing = findMissingKeys(master, props);
            if (missing.length > 0) {
                ok = false;
                System.err.println("Keys missing in `" + resourceName + "'");
                for (final String key : missing) {
                    System.err.println("    " + key);
                }
            }
            missing = findMissingKeys(props, master);
            if (missing.length > 0) {
                ok = false;
                System.err.println("Keys present in `" + resourceName
                                   + "' that are not in the master properties");
                for (final String key : missing) {
                    System.err.println("    " + key);
                }
            }
        }
        if (!ok) {
            fail("Language property files are not in sync.");
        }
    }

}
