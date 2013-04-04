package no.shhsoft.basus.ui.ide;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import no.shhsoft.i18n.I18N;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.VersionData;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class AboutBox {

    private final JDialog dialog;

    public AboutBox(final Frame parent) {
        final VersionData version = VersionData.getApplicationVersionData();
        final String versionSpec = version.getVersion() + ", " + version.getTimeStamp();
        final String text = I18N.msg("window.text.about", versionSpec);
        final Icon icon = new ImageIcon(IoUtils.readResource("/img/Forskerfabrikken-logo.png"));
        final JTextArea textArea = new JTextArea(text);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        final JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panel.add(iconLabel);
        panel.add(textArea);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        final JOptionPane pane = new JOptionPane(panel);
        dialog = pane.createDialog(parent, I18N.msg("window.title.about"));
        dialog.pack();
    }

    public void open() {
        dialog.setVisible(true);
    }

}
