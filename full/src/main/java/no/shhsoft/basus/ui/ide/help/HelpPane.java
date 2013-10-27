package no.shhsoft.basus.ui.ide.help;

import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import no.shhsoft.i18n.I18N;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HelpPane
extends JTextPane
implements HyperlinkListener {

    private static final long serialVersionUID = 1L;
    private static final String HELP_DIRECTORY = "/help/";
    private final List<HelpLocation> documentStack = new ArrayList<HelpLocation>();
    private String[] searchPrefixes;
    private Map<String, String> synonyms;

    private static final class HelpLocation {

        private String docName;
        /* TODO: the caretPosition saving has no effect at all.  need to control the
         * parent scroller somehow. */
        private int caretPosition;

        public HelpLocation(final String docName, final int caretPosition) {
            this.docName = docName;
            this.caretPosition = caretPosition;
        }

        public String getDocName() {
            return docName;
        }

        public void setDocName(final String docName) {
            this.docName = docName;
        }

        public int getCaretPosition() {
            return caretPosition;
        }

        public void setCaretPosition(final int caretPosition) {
            this.caretPosition = caretPosition;
        }

    }

    private String loadDocument(final String docName) {
        final byte[] data = I18N.readResource(HELP_DIRECTORY, docName + ".html");
        if (data == null) {
            return null;
        }
        try {
            return new String(data, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private String findDocumentInMultipleDirectories(final String docName) {
        String doc = loadDocument(docName);
        if (doc == null && searchPrefixes != null) {
            for (String prefix : searchPrefixes) {
                if (!prefix.endsWith("/")) {
                    prefix = prefix + "/";
                }
                doc = loadDocument(prefix + docName);
                if (doc != null) {
                    break;
                }
            }
        }
        return doc;
    }

    private String findDocument(final String docName) {
        String doc = findDocumentInMultipleDirectories(docName);
        if (doc == null && synonyms != null) {
            final String synonym = synonyms.get(docName);
            if (synonym != null) {
                doc = findDocumentInMultipleDirectories(synonym);
            }
        }
        return doc;
    }

    private boolean openNoHistory(final String docName) {
        final String doc = findDocument(docName);
        if (doc == null) {
            setContent(I18N.msg("help.err.notFound", docName));
            return false;
        }
        setContent(doc);
        return true;
    }

    private synchronized void setContent(final String s) {
        final StringBuilder sb = new StringBuilder();
        sb.append("<html><head></head><body>");
        if (documentStack.size() > 1) {
            sb.append("<a href=\"_back\">[");
            sb.append(I18N.msg("help.butt.back"));
            sb.append("]</a>");
        }
        sb.append("<a href=\"index\">[");
        sb.append(I18N.msg("help.butt.index"));
        sb.append("]</a>");
        sb.append("<hr/>");
        sb.append(s);
        sb.append("</body></html>");
        setText(sb.toString());
        setCaretPosition(0);
    }

    private synchronized void back() {
        if (documentStack.size() <= 1) {
            return;
        }
        documentStack.remove(documentStack.size() - 1);
        final HelpLocation helpLocation = documentStack.get(documentStack.size() - 1);
        openNoHistory(helpLocation.getDocName());
        setCaretPosition(helpLocation.getCaretPosition());
    }

    public HelpPane() {
        setEditable(false);
        final HTMLEditorKit ek = new HTMLEditorKit();
        final StyleSheet ss = new StyleSheet();
        ss.addRule("body { font-family: verdana, helvetica, sans-serif; font-size: 12pt; "
                   + "background: #ffffff; margin: 20; }");
        ss.addRule("ul { margin-left: 20; list-style-type: square; }");
        ss.addRule("h1, h2, h3 { font-weight: bold; padding-top: 10; padding-bottom: 5; }");
        ss.addRule("p, pre, ul { padding-top: 3; padding-bottom: 3; }");
        ss.addRule("h1 { font-size: 16pt; }");
        ss.addRule("h2 { font-size: 14pt; }");
        ss.addRule("h3 { font-size: 13pt; }");
        ss.addRule("a { color: #0000ff; text-decoration: none; }");
        ss.addRule("td { padding-left: 10px; padding-top: 10px; }");
        ss.addRule("pre, tt, .code { font-family: courier, monospace; color: #00aaaa; }");
        ss.addRule(".pseudo { font-style: italic; color: #669988; }");
        /* Java's border stuff sucks; it only supports a single width for all sides' borders. */
        ss.addRule("table.nice-table        { border-width: 1px; border-style: solid; "
                   + "border-color: #000000; width: 100%; }");
        ss.addRule("table.nice-table th     { border-width: 1px; border-style: solid; "
                   + "border-color: #000000; padding: 3px 10px; background: #cccccc; }");
        ss.addRule("table.nice-table td     { border-width: 1px; border-style: solid; "
                   + "border-color: #000000; padding: 3px 10px; background: #ffffff; }");
        ek.setStyleSheet(ss);
        setEditorKit(ek);
        setContentType("text/html");
        addHyperlinkListener(this);
        HelpConfigurationLoader.load(HELP_DIRECTORY + "help.xml", this);
        /* leave it to the scroll pane to scroll. */
        final int[] keysToDisable = {
            KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT
        };
        for (final int element : keysToDisable) {
            /* InputMap.remove doesn't seem to do the trick */
            getInputMap().put(KeyStroke.getKeyStroke(element, 0), "none");
        }
    }

    @Override
    public void hyperlinkUpdate(final HyperlinkEvent e) {
        if (!(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)) {
            return;
        }
        String target;
        if (e.getURL() != null) {
            target = e.getURL().toString();
        } else {
            target = e.getDescription();
        }
        if (target == null || target.length() == 0) {
            return;
        }
        if ("_back".equals(target)) {
            back();
        } else {
            open(target);
        }
    }

    public synchronized void open(final String docName) {
        if (documentStack.isEmpty()
            || !documentStack.get(documentStack.size() - 1).getDocName().equals(docName)) {
            documentStack.add(new HelpLocation(docName, getCaretPosition()));
        }
        openNoHistory(docName);
    }

    public synchronized void clearHistory() {
        documentStack.clear();
    }

    public void setSearchPrefixes(final String[] searchPrefixes) {
        this.searchPrefixes = searchPrefixes;
    }

    public void setSynonyms(final Map<String, String> synonyms) {
        this.synonyms = synonyms;
    }

}
