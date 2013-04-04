package no.shhsoft.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public class InputPanel
extends JPanel {

    private static final long serialVersionUID = 1L;
    private final JPanel panel;
    private GridBagLayout gbl;
    private GridBagConstraints gbc;

    private void addField(final Component field, final int gwidth, final int gheight, final int gx,
                          final int wx, final int wy) {
        if ((gx == 0 || gx == 1) && wy == 0) { /* label or submit */
            if (gx == 0) {
                gbc.anchor = GridBagConstraints.NORTHEAST;
            } else {
                gbc.anchor = GridBagConstraints.WEST;
            }
            gbc.fill = GridBagConstraints.NONE;
        } else {
            gbc.anchor = GridBagConstraints.WEST;
            gbc.fill = GridBagConstraints.HORIZONTAL;
        }
        gbc.gridwidth = gwidth;
        gbc.gridheight = gheight;
        gbc.gridx = gx;
        gbc.weightx = wx;
        gbc.weighty = wy;
        gbl.setConstraints(field, gbc);
        panel.add(field);
    }

    public InputPanel() {
        panel = new JPanel();
        setLayout(new GridLayout(1, 1));
        add(new JScrollPane(panel));
        clear();
    }

    public final void clear() {
        panel.removeAll();

        gbl = new GridBagLayout();
        gbc = new GridBagConstraints();
        panel.setLayout(gbl);
        gbc.insets = new Insets(2, 5, 2, 5);
    }

    public final void addFields(final Component field1, final Component field2,
                                final Component field3) {
        if (field1 != null) {
            addField(field1, 1, 1, 0, 0, 0);
        }
        if (field2 != null) {
            addField(field2, field3 == null ? GridBagConstraints.REMAINDER : 1, 1,
                     GridBagConstraints.RELATIVE, 1, 0);
        }
        if (field3 != null) {
            addField(field3, GridBagConstraints.REMAINDER, 1, GridBagConstraints.RELATIVE, 0, 0);
        }
    }

    public final void addFields(final Component field1, final Component field2) {
        addFields(field1, field2, null);
    }

    public final void addFields(final String label, final Component field2, final Component field3) {
        addFields(new JLabel(label), field2, field3);
    }

    public final void addFields(final String label, final Component field2) {
        addFields(label, field2, null);
    }

    public final void addSubmit(final Component field) {
        addField(field, 1, 1, 1, 0, 0);
    }

}
