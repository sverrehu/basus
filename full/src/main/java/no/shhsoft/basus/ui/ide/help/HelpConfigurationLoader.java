package no.shhsoft.basus.ui.ide.help;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import no.shhsoft.utils.IoUtils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HelpConfigurationLoader
extends DefaultHandler {

    private static final String TAG_HELP = "help";
    private static final String TAG_SEARCH_PREFIXES = "search-prefixes";
    private static final String TAG_PREFIX = "prefix";
    private static final String TAG_SYNONYMS = "synonyms";
    private static final String TAG_LABEL = "label";
    private static final String TAG_SYNONYM = "synonym";
    private static final String ATTR_NAME = "name";
    private final StringBuilder chars = new StringBuilder();
    private List<String> searchPrefixes;
    private String currentSynonymLabel;
    private Map<String, String> synonyms;
    private State state;

    private enum State {
        PREFIX,
        SYNONYM,
    }

    private HelpConfigurationLoader() {
    }

    private void assertAttributesSize(final Attributes attributes,
                                      final int expectedSize, final String tagName) {
        if (attributes.getLength() != expectedSize) {
            throw new RuntimeException(tagName + " expects " + expectedSize + " attribute(s)");
        }
    }

    private void startHelp(final Attributes attributes) {
        assertAttributesSize(attributes, 0, TAG_HELP);
    }

    private void endHelp() {
    }

    private void startSearchPrefixes(final Attributes attributes) {
        assertAttributesSize(attributes, 0, TAG_SEARCH_PREFIXES);
        if (searchPrefixes == null) {
            searchPrefixes = new ArrayList<String>();
        }
    }

    private void endSearchPrefixes() {
    }

    private void startPrefix(final Attributes attributes) {
        assertAttributesSize(attributes, 0, TAG_PREFIX);
        state = State.PREFIX;
    }

    private void endPrefix() {
        state = null;
    }

    private void startSynonyms(final Attributes attributes) {
        assertAttributesSize(attributes, 0, TAG_SYNONYMS);
        if (synonyms == null) {
            synonyms = new HashMap<String, String>();
        }
    }

    private void endSynonyms() {
    }

    private void startLabel(final Attributes attributes) {
        assertAttributesSize(attributes, 1, TAG_LABEL);
        final String name = attributes.getValue(ATTR_NAME);
        if (name == null) {
            throw new RuntimeException("missing attribute " + ATTR_NAME);
        }
        currentSynonymLabel = name;
    }

    private void endLabel() {
    }

    private void startSynonym(final Attributes attributes) {
        assertAttributesSize(attributes, 0, TAG_SYNONYM);
        state = State.SYNONYM;
    }

    private void endSynonym() {
        state = null;
    }

    @Override
    public void characters(final char[] ch, final int start, final int length)
    throws SAXException {
        chars.append(new String(ch, start, length));
    }

    public void handlerCharacters() {
        if (chars.length() == 0) {
            return;
        }
        final String text = chars.toString().trim();
        chars.setLength(0);
        if (state != null) {
            switch (state) {
                case PREFIX:
                    searchPrefixes.add(text);
                    break;
                case SYNONYM:
                    synonyms.put(text, currentSynonymLabel);
                    break;
                default:
                    throw new RuntimeException("strange.");
            }
        }
    }

    @Override
    public void startElement(final String uri, final String localName, final String qName,
                             final Attributes attributes)
    throws SAXException {
        if (qName.compareTo(TAG_HELP) == 0) {
            startHelp(attributes);
        } else if (qName.compareTo(TAG_SEARCH_PREFIXES) == 0) {
            startSearchPrefixes(attributes);
        } else if (qName.compareTo(TAG_PREFIX) == 0) {
            startPrefix(attributes);
        } else if (qName.compareTo(TAG_SYNONYMS) == 0) {
            startSynonyms(attributes);
        } else if (qName.compareTo(TAG_LABEL) == 0) {
            startLabel(attributes);
        } else if (qName.compareTo(TAG_SYNONYM) == 0) {
            startSynonym(attributes);
        } else {
            throw new RuntimeException("unrecognized tag `" + qName + "'");
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName)
    throws SAXException {
        handlerCharacters();
        if (qName.compareTo(TAG_HELP) == 0) {
            endHelp();
        } else if (qName.compareTo(TAG_SEARCH_PREFIXES) == 0) {
            endSearchPrefixes();
        } else if (qName.compareTo(TAG_PREFIX) == 0) {
            endPrefix();
        } else if (qName.compareTo(TAG_SYNONYMS) == 0) {
            endSynonyms();
        } else if (qName.compareTo(TAG_LABEL) == 0) {
            endLabel();
        } else if (qName.compareTo(TAG_SYNONYM) == 0) {
            endSynonym();
        } else {
            throw new RuntimeException("unrecognized tag `" + qName + "'");
        }
    }

    public static void load(final String resourceName, final HelpPane helpPane) {
        final byte[] data = IoUtils.readResource(resourceName);
        if (data == null) {
            return;
        }
        final HelpConfigurationLoader loader = new HelpConfigurationLoader();
        Throwable throwable = null;
        final InputStream in = new ByteArrayInputStream(data);
        try {
            final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
            final SAXParser parser = parserFactory.newSAXParser();
            parser.parse(in, loader);
        } catch (final SAXException e) {
            throwable = e;
        } catch (final IOException e) {
            throwable = e;
        } catch (final ParserConfigurationException e) {
            throwable = e;
        }
        if (throwable != null) {
            throw new RuntimeException("error parsing XML: " + throwable.getMessage(),
                                       throwable);
        }
        if (loader.searchPrefixes != null) {
            helpPane.setSearchPrefixes(loader.searchPrefixes.toArray(
                                              new String[loader.searchPrefixes.size()]));
        }
        if (loader.synonyms != null) {
            helpPane.setSynonyms(loader.synonyms);
        }
    }

}
