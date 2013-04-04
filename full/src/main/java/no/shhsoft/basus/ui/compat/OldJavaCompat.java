package no.shhsoft.basus.ui.compat;

import java.lang.reflect.Constructor;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class OldJavaCompat {

    private OldJavaCompat() {
    }

    public static void setFileNameExtensionFilter(final JFileChooser fileChooser,
                                                  final String description,
                                                  final String extension) {
        try {
            final Class<?> clazz = Class.forName("javax.swing.filechooser.FileNameExtensionFilter");
            if (clazz != null) {
                final Constructor<?> cons = clazz.getConstructor(String.class, String[].class);
                final FileFilter fileFilter
                    = (FileFilter) cons.newInstance(description, new String[] { extension });
                fileChooser.setFileFilter(fileFilter);
            }
        } catch (final Throwable t) {
            System.out.println("You're running a pre 1.6 Java.");
        }
    }

}
