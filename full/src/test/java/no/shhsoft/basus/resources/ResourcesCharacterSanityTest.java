package no.shhsoft.basus.resources;

import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;

import no.shhsoft.resourcetrav.Resource;
import no.shhsoft.resourcetrav.ResourceHandler;
import no.shhsoft.resourcetrav.ResourceTraverser;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.StringUtils;
import no.shhsoft.validation.ValidationUtils;

import org.junit.Test;

/**
 * It has happened that example programs have become unparsable due
 * to mystical changes in the character set.  This class checks
 * that all textual resources contain sane characters only.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class ResourcesCharacterSanityTest {

    private static final String NORWEGIAN_CHARS = "\u00e6\u00f8\u00e5\u00c6\u00d8\u00c5\u00e9";
    private static final String SPECIAL_CHARS = " !\"#%&/()=?`;[]{}|\\<>.:,;'*@$_-+^\n\r";
    private static final String WHITE_LIST = ValidationUtils.ALNUM + NORWEGIAN_CHARS + SPECIAL_CHARS;
    private boolean wasError;

    private String getNonWhiteListedChars(final String s) {
        final StringBuilder sb = new StringBuilder();
        for (int q = s.length() - 1; q >= 0; q--) {
            final char c = s.charAt(q);
            if (WHITE_LIST.indexOf(c) < 0) {
                sb.append(String.format("\\u%04x ", Integer.valueOf(c)));
            }
        }
        return sb.toString();
    }

    private boolean isSane(final String s) {
        return StringUtils.containsOnly(s, WHITE_LIST);
    }

    private void assertResourceSane(final String name) {
        final byte[] data = IoUtils.readResource(name);
        try {
            final String content = new String(data, "UTF-8");
            if (!isSane(content)) {
                System.out.println("Bad chars in `" + name + "': " + getNonWhiteListedChars(content));
                wasError = true;
            }
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void assertAllResourcesAreSane() {
        new ResourceTraverser().traverse(new ResourceHandler() {
            @SuppressWarnings("synthetic-access")
            @Override
            public boolean handle(final Resource resource) {
                final String name = resource.getName();
                if (name.endsWith(".html") || name.endsWith(".bus")) {
                    assertResourceSane(name);
                }
                return true;
            }

        }, "examples", "help");
        if (wasError) {
            fail("Found unwanted characters in one or more resources");
        }
    }

}
