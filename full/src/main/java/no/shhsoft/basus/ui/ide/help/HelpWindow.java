package no.shhsoft.basus.ui.ide.help;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JScrollPane;

import no.shhsoft.i18n.I18N;
import no.shhsoft.swing.AppFrame;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class HelpWindow
extends AppFrame {

    private static final long serialVersionUID = 1L;
    private static final String HELP_CONTENTS = "index";
    private final HelpPane helpPane;

    private void open(final String name) {
        if (!isVisible()) {
            helpPane.clearHistory();
            setVisible(true);
        }
        helpPane.open(name);
        toFront();
    }

    public HelpWindow() {
        super("help");
        setDefaultLocation(10, 10);
        setDefaultDimension(500, 600);
        setTitle(I18N.msg("window.title.help"));
        final Container content = getContentPane();
        content.setLayout(new BorderLayout());

        helpPane = new HelpPane();
        content.add(new JScrollPane(helpPane), BorderLayout.CENTER);

        pack();
    }

    public void openContents() {
        open(HELP_CONTENTS);
    }

    public void openTopic(final String topic) {
        if (topic == null) {
            openContents();
        } else {
            open(topic);
        }
    }

    public void setSearchPrefixes(final String[] prefixes) {
        helpPane.setSearchPrefixes(prefixes);
    }

}
