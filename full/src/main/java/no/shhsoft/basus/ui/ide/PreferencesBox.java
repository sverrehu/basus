package no.shhsoft.basus.ui.ide;


import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import no.shhsoft.i18n.I18N;
import no.shhsoft.swing.InputPanel;
import no.shhsoft.utils.AppProps;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class PreferencesBox {

    private final BasusIde parent;
    private final JOptionPane pane;
    private final JDialog dialog;
    private JComboBox languages;
    private JComboBox fontSize;
    private JComboBox theme;
    private JCheckBox versionCheck;

    private static class LanguageItem
    extends DefaultComboBoxModel {

        private static final long serialVersionUID = 1L;
        private final String language;

        public LanguageItem(final String language) {
            this.language = language;
        }

        public String getLanguage() {
            return language;
        }

        @Override
        public String toString() {
            return I18N.msg("preferences.language." + language);
        }

        @Override
        public boolean equals(final Object obj) {
            return obj != null && obj instanceof LanguageItem
                && language.equals(((LanguageItem) obj).language);
        }

        @Override
        public int hashCode() {
            return language.hashCode();
        }

    }

    private static class ThemeItem
            extends DefaultComboBoxModel {

        private static final long serialVersionUID = 1L;
        private final UIManager.LookAndFeelInfo info;

        public ThemeItem(final UIManager.LookAndFeelInfo info) {
            this.info = info;
        }

        public String getClassName() {
            return info.getClassName();
        }

        @Override
        public String toString() {
            return info.getName();
        }

        @Override
        public boolean equals(final Object obj) {
            return obj != null && obj instanceof ThemeItem
                    && info.getName().equals(((ThemeItem) obj).info.getName());
        }

        @Override
        public int hashCode() {
            return info.getName().hashCode();
        }

    }

    private void fillPreferences() {
        languages.setSelectedItem(new LanguageItem(I18N.getLocale().getLanguage()));
        fontSize.setSelectedItem(new Integer(AppProps.getInt("editor.font.size")));
        versionCheck.setSelected(parent.getVersionChecker().isEnabled());
    }

    private void storePreferences() {
        final LanguageItem selectedLanguage = (LanguageItem) languages.getSelectedItem();
        if (selectedLanguage != null) {
            final String language = selectedLanguage.getLanguage();
            AppProps.set("locale.language", language);
        }
        final int size = ((Integer) fontSize.getSelectedItem()).intValue();
        parent.setEditorFontSize(size);
        AppProps.set("editor.font.size", size);
        parent.getVersionChecker().setEnabled(versionCheck.isSelected());
    }

    @SuppressWarnings("boxing")
    private JPanel createPreferencesPanel() {
        final InputPanel panel = new InputPanel();
        final String[] supportedLanguages = I18N.getSupportedLanguages();
        final LanguageItem[] selectableLanguages = new LanguageItem[supportedLanguages.length];
        int idx = 0;
        for (final String language : supportedLanguages) {
            selectableLanguages[idx++] = new LanguageItem(language);
        }
        languages = new JComboBox<LanguageItem>(selectableLanguages);
        panel.addFields(new JLabel(I18N.msg("preferences.label.language")), languages);
        final Integer[] selectableFontSizes = new Integer[] {
            8, 9, 10, 11, 12, 14, 16, 18, 20
        };
        fontSize = new JComboBox<Integer>(selectableFontSizes);
        panel.addFields(new JLabel(I18N.msg("preferences.label.editor.font.size")), fontSize);
        List<String> selectableThemes = new ArrayList<String>();
        for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            selectableThemes.add(info.getName());
        }
        UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
        ThemeItem[] themeItems = new ThemeItem[lookAndFeels.length];
        idx = 0;
        for (final UIManager.LookAndFeelInfo info : lookAndFeels) {
            themeItems[idx++] = new ThemeItem(info);
        }
        theme = new JComboBox<ThemeItem>(themeItems);
        theme.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    applyTheme((ThemeItem) e.getItem());
                }
            }
        });
        panel.addFields(new JLabel(I18N.msg("preferences.label.theme")), theme);
        versionCheck = new JCheckBox();
        panel.addFields(new JLabel(I18N.msg("preferences.label.versionChecker")), versionCheck);
        return panel;
    }

    public PreferencesBox(final BasusIde parent) {
        this.parent = parent;
        pane = new JOptionPane(createPreferencesPanel(),
                               JOptionPane.PLAIN_MESSAGE,
                               JOptionPane.OK_CANCEL_OPTION);
        dialog = pane.createDialog(parent, I18N.msg("window.title.preferences"));
        dialog.pack();
    }

    @SuppressWarnings("boxing")
    public void open() {
        fillPreferences();
        dialog.setVisible(true);
        if (pane.getValue().equals(JOptionPane.OK_OPTION)) {
            storePreferences();
        }
    }

    public void applyTheme(ThemeItem theme) {
        try {
            UIManager.setLookAndFeel(theme.getClassName());
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        } catch (Exception ex) {
            // Worst thing that can happen is that we don't change the look and feel
            ex.printStackTrace();
        }
    }
}
