package no.shhsoft.basus.ui.ide.debug.variables;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JScrollPane;

import no.shhsoft.basus.ui.ide.debug.StepListener;
import no.shhsoft.i18n.I18N;
import no.shhsoft.swing.AppFrame;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class VariablesWindow
extends AppFrame {

    private static final long serialVersionUID = 1L;
    private final VariablesPane variablesPane;

    public VariablesWindow() {
        super("variables");
        setDefaultLocation(10, 10);
        setDefaultDimension(350, 300);
        setTitle(I18N.msg("window.title.variables"));
        final Container content = getContentPane();
        content.setLayout(new BorderLayout());

        variablesPane = new VariablesPane(new VariablesModel());
        variablesPane.getColumnModel().getColumn(0).setPreferredWidth(10);
        variablesPane.getColumnModel().getColumn(1).setPreferredWidth(90);
//        content.add(variablesPane, BorderLayout.CENTER);
        content.add(new JScrollPane(variablesPane), BorderLayout.CENTER);

        pack();
    }

    public StepListener getStepListener() {
        return variablesPane;
    }

}
