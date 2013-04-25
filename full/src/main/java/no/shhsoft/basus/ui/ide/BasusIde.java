package no.shhsoft.basus.ui.ide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.AccessControlException;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;

import no.shhsoft.awt.FullScreenDisplayModeOptimizer;
import no.shhsoft.basus.language.AbstractBasusException;
import no.shhsoft.basus.language.Statement;
import no.shhsoft.basus.language.eval.EvaluationContext;
import no.shhsoft.basus.language.eval.StatementListener;
import no.shhsoft.basus.language.eval.runtime.BasusRunner;
import no.shhsoft.basus.language.eval.runtime.Console;
import no.shhsoft.basus.language.eval.runtime.DrawingArea;
import no.shhsoft.basus.language.eval.runtime.RunStatusListener;
import no.shhsoft.basus.language.eval.runtime.TerminationRequestListener;
import no.shhsoft.basus.language.parser.ParserException;
import no.shhsoft.basus.tools.format.BasusFormatter;
import no.shhsoft.basus.ui.FullScreenOutput;
import no.shhsoft.basus.ui.OutputCanvas;
import no.shhsoft.basus.ui.compat.OldJavaCompat;
import no.shhsoft.basus.ui.ide.debug.DebugController;
import no.shhsoft.basus.ui.ide.debug.StepListener;
import no.shhsoft.basus.ui.ide.debug.variables.VariablesWindow;
import no.shhsoft.basus.ui.ide.help.HelpWindow;
import no.shhsoft.basus.utils.TextLocation;
import no.shhsoft.i18n.I18N;
import no.shhsoft.swing.AppFrame;
import no.shhsoft.swing.SwingUtils;
import no.shhsoft.utils.AppProps;
import no.shhsoft.utils.AppVersionChecker;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.UncheckedIoException;

/**
 * I hate this file, and everything like it.  Big, stinking of UI stuff.
 *
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
@SuppressWarnings("synthetic-access")
public final class BasusIde
extends AppFrame
implements RunStatusListener, TerminationRequestListener, EditorButtonsProvider, HelpProvider,
    StepListener {

    private static final long serialVersionUID = 1L;
    private static final String HOME_URL = "http://basus.no";
    private static final String DOWNLOAD_BASE_URL = HOME_URL + "/download/";
    private static final long STEP_WAIT_MS = 100L;
    private OutputCanvas outputCanvas;
    private Editor editor;
    private JTextArea message;
    private transient BasusRunner runner;
    private JButton runButton;
    private JButton stopButton;
    private HelpWindow helpWindow;
    private VariablesWindow variablesWindow;
    private AboutBox aboutBox;
    private PreferencesBox preferencesBox;
    private JFileChooser fileChooser;
    private File currentFile;
    private URL currentSourceUrl;
    private JMenuItem menuEditUndo;
    private JMenuItem menuEditRedo;
    private JMenuItem menuEditCut;
    private JMenuItem menuEditCopy;
    private JMenuItem menuEditPaste;
    private JMenuItem menuEditFormat;
    private JMenuItem menuRunRun;
    private JMenuItem menuRunStep;
    private JMenuItem menuRunStop;
    private boolean scalingOutput;
    private boolean fileOperationsAllowed = true;
    private boolean wantsFullScreen = false;
    private FullScreenOutput fullScreen;
    private AppVersionChecker versionChecker;
    private final BasusFormatter formatter = new BasusFormatter();

    private static enum Confirmation {
        YES, NO, CANCEL,
    }

    private static void ignore() {
    }

    private void setFileOperationsAllowed(final boolean fileOperationsAllowed) {
        this.fileOperationsAllowed = fileOperationsAllowed;
    }

    private boolean isFileOperationsAllowed() {
        return fileOperationsAllowed;
    }

    private void errorDialog(final String key, final Object... args) {
        final String msg = I18N.msg(key, args);
        final String title = I18N.msg("window.title.error");
        JOptionPane.showMessageDialog(this, msg, title, JOptionPane.ERROR_MESSAGE);
    }

    private Confirmation confirmDialog(final String key, final Object... args) {
        final String msg = I18N.msg(key, args);
        final String title = I18N.msg("window.title.confirm");
        final int answer = JOptionPane.showConfirmDialog(this, msg, title,
                                                         JOptionPane.YES_NO_CANCEL_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            return Confirmation.YES;
        }
        if (answer == JOptionPane.NO_OPTION) {
            return Confirmation.NO;
        }
        return Confirmation.CANCEL;
    }

    private static void setAccelKeyWithCtrl(final JMenuItem menuItem, final char c) {
        if (c < 'A' || c > 'Z') {
            throw new RuntimeException("character must be from 'A' to 'Z', as these map "
                                       + "directly to KeyEvent.VK_A - Z");
        }
        menuItem.setAccelerator(
            KeyStroke.getKeyStroke(c, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask(), false));
    }

    private static void setAccelKey(final JMenuItem menuItem, final int keyCode) {
        menuItem.setAccelerator(KeyStroke.getKeyStroke(keyCode, 0, false));
    }

    private void shutdown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (isFileOperationsAllowed()) {
                    final File directory = fileChooser.getCurrentDirectory();
                    AppProps.set("fullScreen", wantsFullScreen);
                    if (directory != null) {
                        AppProps.set("directory", directory.toString());
                    }
                    try {
                        AppProps.save();
                    } catch (final IOException e) {
                        System.err.println("Error saving properties: " + e.getMessage());
                    }
                }
               if (continueWithoutSaving()) {
                    System.exit(0);
                }
            }
        }).start();
    }

    private void startRunning(final RunMode runMode) {
        boolean startedRunning = false;
        StatementListener statementListener = null;
        if (runMode == RunMode.AUTO_STEP) {
            final DebugController debugController = new DebugController();
            debugController.addStepListener(this);
            debugController.addStepListener(variablesWindow.getStepListener());
            statementListener = debugController;
            variablesWindow.setVisible(true);
        } else {
            variablesWindow.setVisible(false);
        }
        if (wantsFullScreen) {
            fullScreen = new FullScreenOutput();
            if (fullScreen.isFullScreenSupported()) {
                fullScreen.getOutputCanvas().addTerminationRequestListener(this);
                final FullScreenDisplayModeOptimizer.FullScreenTransform fullScreenTransform
                    = fullScreen.enterFullScreen(OutputCanvas.AREA_WIDTH, OutputCanvas.AREA_HEIGHT);
                if (fullScreenTransform != null) {
                    fullScreen.getOutputCanvas().setFullScreenTransform(fullScreenTransform);
                    runner.runProgram(editor.getText(), fullScreen.getOutputCanvas(),
                                      fullScreen.getOutputCanvas(), statementListener,
                                      currentSourceUrl);
                    startedRunning = true;
                }
            }
        }
        if (!startedRunning) {
            if (wantsFullScreen) {
                message.setText(I18N.msg("err.fullScreen.notAvailable"));
            }
            runner.runProgram(editor.getText(), outputCanvas, outputCanvas, statementListener,
                              currentSourceUrl);
        }
    }

    private void unconditionalOpen(final File file) {
        byte[] data = null;
        try {
            data = IoUtils.readFile(file.getPath());
        } catch (final UncheckedIoException e) {
            errorDialog("err.unableToLoad", file.getPath());
            return;
        }
        String text = null;
        try {
            text = new String(data, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        setEditorText(text);
        setCurrentFile(file);
    }

    private boolean unconditionalSave(final File file) {
        File actualFile = file;
        if (file.getName().indexOf('.') < 0) {
            actualFile = new File(file.getPath() + ".bus");
        }
        byte[] data = null;
        try {
            data = editor.getText().getBytes("UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        try {
            IoUtils.writeFile(actualFile.getPath(), data);
            editor.setChanged(false);
            setCurrentFile(actualFile);
            return true;
        } catch (final UncheckedIoException e) {
            errorDialog("err.unableToSave", actualFile.getPath());
            return false;
        }
    }

    private void newFile() {
        if (!continueWithoutSaving()) {
            return;
        }
        setEditorText("");
        setCurrentFile(null);
    }

    private void open() {
        if (!continueWithoutSaving()) {
            return;
        }
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        final File file = fileChooser.getSelectedFile();
        unconditionalOpen(file);
    }

    private boolean save() {
        if (currentFile == null) {
            return saveAs();
        }
        return unconditionalSave(currentFile);
    }

    private boolean saveAs() {
        for (;;) {
            if (fileChooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
                return false;
            }
            final File file = fileChooser.getSelectedFile();
            if (file.exists()) {
                final Confirmation answer = confirmDialog("msg.confirmOverwrite", file.getPath());
                if (answer == Confirmation.YES) {
                    return unconditionalSave(file);
                }
                if (answer == Confirmation.CANCEL) {
                    return false;
                }
            } else {
                return unconditionalSave(file);
            }
        }
    }

    private void setEditorText(final String text) {
        editor.setText(text);
        editor.setCaretPosition(0);
        editor.setChanged(false);
    }

    private boolean continueWithoutSaving() {
        if (!isFileOperationsAllowed()) {
            return true;
        }
        if (!editor.isChanged()) {
            return true;
        }
        final Confirmation answer = confirmDialog("msg.notSaved");
        if (answer == Confirmation.YES) {
            return save();
        }
        return answer == Confirmation.NO;
    }

    private JButton createButton(final String text) {
        final JButton button = new JButton(text);
        button.setFocusable(false);
        return button;
    }

    private Component createOutputComponent() {
        outputCanvas = new OutputCanvas();
        outputCanvas.addTerminationRequestListener(this);
        final JPanel outputPanel = new JPanel();
        outputPanel.add(outputCanvas);
        Dimension outputCanvasSize = new Dimension(OutputCanvas.AREA_WIDTH, OutputCanvas.AREA_HEIGHT);
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (screenSize.width < 1100 || screenSize.height < 700) {
            scalingOutput = true;
            outputCanvasSize = new Dimension(500, 375);
        }
        outputCanvas.setMinimumSize(outputCanvasSize);
        outputCanvas.setPreferredSize(outputCanvasSize);
        outputCanvas.setMinimumSize(outputCanvasSize);
        outputCanvas.setMaximumSize(outputCanvasSize);
        return outputPanel;
    }

    public void setEditorFontSize(final int size) {
        editor.setFont(new Font(Font.MONOSPACED, Font.PLAIN, size));
        if (editor.isVisible()) {
            editor.repaint();
        }
    }

    private Component createEditorComponent() {
        editor = new Editor();
        editor.setButtonsProvider(this);
        editor.setHelpProvider(this);
        AppProps.setDefault("editor.font.size", 12);
        setEditorFontSize(AppProps.getInt("editor.font.size"));
        final JScrollPane editorPane = new JScrollPane(editor);
        editorPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(4, 4, 4, 0),
            BorderFactory.createLineBorder(Color.BLACK)));
        final Dimension editorSize = new Dimension(400, 500);
        editorPane.setSize(editorSize);
        editorPane.setMinimumSize(editorSize);
        editorPane.setMaximumSize(null);
        return editorPane;
    }

    private Component createButtonBarComponent() {
        final JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        runButton = createButton(I18N.msg("butt.run"));
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startRunning(RunMode.NORMAL);
            }
        });
        panel.add(runButton);
        stopButton = createButton(I18N.msg("butt.stop"));
        stopButton.setEnabled(false);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                runner.stopProgram();
            }
        });
        panel.add(stopButton);
        final JCheckBox fullScreenCheckBox = new JCheckBox(I18N.msg("butt.fullScreen"));
        fullScreenCheckBox.setSelected(wantsFullScreen);
        fullScreenCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                wantsFullScreen = fullScreenCheckBox.isSelected();
            }
        });
        panel.add(fullScreenCheckBox);
        return panel;
    }

    private Component createMessageComponent() {
        message = new JTextArea();
        message.setEditable(false);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setForeground(Color.RED);
        message.setPreferredSize(new Dimension(100, 500));
        message.setText("\n  " + I18N.msg("tip.stop"));
        final JScrollPane messagePane = new JScrollPane(message);
        messagePane.setBorder(BorderFactory.createCompoundBorder(
                                  BorderFactory.createEmptyBorder(4, 4, 4, 4),
                                  BorderFactory.createLineBorder(Color.BLACK)));
        return messagePane;
    }

    private Component createRightSideComponent() {
        final JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createButtonBarComponent());
        panel.add(createOutputComponent());
        panel.add(createMessageComponent());
        if (scalingOutput) {
            message.setText(I18N.msg("msg.scalingOutput"));
        }
        return panel;
    }

    private static String getTextWithoutMnemonic(final String textWithMnemonic) {
        return textWithMnemonic.replaceAll("_", "");
    }

    private static int getMnemonicChar(final String textWithMnemonic) {
        final int idx = textWithMnemonic.indexOf('_');
        if (idx < 0 || idx == textWithMnemonic.length() - 1) {
            return -1;
        }
        final char c = Character.toUpperCase(textWithMnemonic.charAt(idx + 1));
        if (c < 'A' || c > 'Z') {
            return -1;
        }
        return c;
    }

    private static void setTextAndMnemonic(final AbstractButton button, final String label) {
        final String textWithMnemonic = I18N.msg(label);
        final String textWithoutMnemonic = getTextWithoutMnemonic(textWithMnemonic);
        final int mnemonic = getMnemonicChar(textWithMnemonic);
        button.setText(textWithoutMnemonic);
        if (mnemonic >= 0) {
            button.setMnemonic(mnemonic);
        }
    }

    private static JMenu createJMenu(final String label) {
        final JMenu menu = new JMenu();
        setTextAndMnemonic(menu, label);
        return menu;
    }

    private static JMenuItem createJMenuItem(final String label) {
        final JMenuItem menuItem = new JMenuItem();
        setTextAndMnemonic(menuItem, label);
        return menuItem;
    }

    private JMenuItem createExampleMenuItem(final String name) {
        final JMenuItem item = createJMenuItem("example." + name);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent event) {
                String program;
                try {
                    program = new String(I18N.readResource("/examples/", name), "UTF-8");
                } catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
                if (continueWithoutSaving()) {
                    setEditorText(program);
                    setCurrentFile(null);
                }
            }
        });
        return item;
    }

    private JMenuItem createExampleSubMenu(final String label, final String[] names) {
        final JMenu menu = createJMenu(label);
        for (final String name : names) {
            menu.add(createExampleMenuItem(name));
        }
        return menu;
    }

    private JMenu createRunMenu() {
        final JMenu menuRun = createJMenu("menu.run");
        menuRunRun = createJMenuItem("menu.run.run");
        setAccelKey(menuRunRun, KeyEvent.VK_F11);
        menuRun.add(menuRunRun);
        menuRunRun.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startRunning(RunMode.NORMAL);
            }
        });
        menuRunStep = createJMenuItem("menu.run.step");
        menuRun.add(menuRunStep);
        menuRunStep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                startRunning(RunMode.AUTO_STEP);
            }
        });
        menuRunStop = createJMenuItem("menu.run.stop");
        setAccelKey(menuRunStop, KeyEvent.VK_F12);
        menuRun.add(menuRunStop);
        menuRunStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                runner.stopProgram();
            }
        });
        return menuRun;
    }

    private JMenuBar createMenuBar(final boolean allowFileOperations) {
        final JMenuBar menuBar = new JMenuBar();

        final JMenu menuFile = createJMenu("menu.file");
        final JMenuItem menuFileNew = createJMenuItem("menu.file.new");
        final JMenuItem menuFileOpen = createJMenuItem("menu.file.open");
        final JMenuItem menuFileSave = createJMenuItem("menu.file.save");
        final JMenuItem menuFileSaveAs = createJMenuItem("menu.file.saveAs");
        final JMenuItem menuFilePreferences = createJMenuItem("menu.file.preferences");
        final JMenuItem menuFileExit = createJMenuItem("menu.file.exit");
        menuBar.add(menuFile);
        menuFile.add(menuFileNew);
        menuFileNew.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                newFile();
            }
        });
        if (allowFileOperations) {
            setAccelKeyWithCtrl(menuFileOpen, 'O');
            menuFile.add(menuFileOpen);
            menuFileOpen.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    open();
                }
            });
            setAccelKeyWithCtrl(menuFileSave, 'S');
            menuFile.add(menuFileSave);
            menuFileSave.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    save();
                }
            });
            menuFile.add(menuFileSaveAs);
            menuFileSaveAs.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    saveAs();
                }
            });
        }
        menuFile.add(new JSeparator());
        menuFile.add(menuFilePreferences);
        menuFilePreferences.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                preferencesBox.open();
            }
        });
        menuFile.add(new JSeparator());
        menuFile.add(menuFileExit);
        menuFileExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                shutdown();
            }
        });

        final JMenu menuEdit = createJMenu("menu.edit");
        menuEditUndo = createJMenuItem("menu.edit.undo");
        menuEditRedo = createJMenuItem("menu.edit.redo");
        menuEditCut = createJMenuItem("menu.edit.cut");
        menuEditCopy = createJMenuItem("menu.edit.copy");
        menuEditPaste = createJMenuItem("menu.edit.paste");
        menuEditFormat = createJMenuItem("menu.edit.format");
        menuBar.add(menuEdit);
        setAccelKeyWithCtrl(menuEditUndo, 'Z');
        menuEdit.add(menuEditUndo);
        menuEditUndo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                editor.undo();
            }
        });
        setAccelKeyWithCtrl(menuEditRedo, 'Y');
        menuEdit.add(menuEditRedo);
        menuEditRedo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                editor.redo();
            }
        });
        menuEdit.add(new JSeparator());
        setAccelKeyWithCtrl(menuEditCut, 'X');
        menuEdit.add(menuEditCut);
        menuEditCut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                editor.cut();
            }
        });
        setAccelKeyWithCtrl(menuEditCopy, 'C');
        menuEdit.add(menuEditCopy);
        menuEditCopy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                editor.copy();
            }
        });
        setAccelKeyWithCtrl(menuEditPaste, 'V');
        menuEdit.add(menuEditPaste);
        menuEditPaste.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                editor.paste();
            }
        });
        menuEdit.add(new JSeparator());
        menuEdit.add(menuEditFormat);
        menuEditFormat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                format();
            }
        });

        menuBar.add(createRunMenu());

        final JMenu menuExamples = createJMenu("menu.examples");
        menuBar.add(menuExamples);
        menuExamples.add(createExampleSubMenu("menu.examples.simple", new String[] {
            "helloWorld.bus", "helloYou.bus", "multiplication.bus", "factorial.bus",
            "rabbitCalculator.bus", "sort.bus", "guessNumber.bus"
        }));
        menuExamples.add(createExampleSubMenu("menu.examples.graphics", new String[] {
            "randomCircles.bus", "spaceShuttleSprite.bus", "mouseDraw.bus",
            "spiral.bus", "bounce.bus", "bounceMany.bus", "fadeToBlack.bus",
            "plotTrigFunctions.bus", "navigateByKeys.bus", "gravity.bus", "mandelbrot.bus",
        }));
        menuExamples.add(createExampleSubMenu("menu.examples.games", new String[] {
            "lightCycle.bus", "spaceWar.bus", "hitTheGreenBall.bus", "breakout.bus"
        }));
        menuExamples.add(createExampleSubMenu("menu.examples.tools", new String[] {
            "keyCodePrinter.bus", "framesPerSecond.bus"
        }));

        final JMenu menuHelp = createJMenu("menu.help");
        menuBar.add(menuHelp);
        final JMenuItem menuHelpContents = createJMenuItem("menu.help.contents");
        menuHelp.add(menuHelpContents);
        menuHelpContents.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                helpWindow.openContents();
            }
        });
        menuHelp.add(new JSeparator());
        final JMenuItem menuHelpAbout = createJMenuItem("menu.help.about");
        menuHelp.add(menuHelpAbout);
        menuHelpAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                aboutBox.open();
            }
        });

        return menuBar;
    }

    private void createAndShow() {
        runner = new BasusRunner();
        runner.addRunStatusListener(this);
        final Container content = getContentPane();
        content.setLayout(new BorderLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                shutdown();
            }
        });

        try {
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            try {
                OldJavaCompat.setFileNameExtensionFilter(fileChooser, "*.bus", "bus");
            } catch (final Exception e) {
                System.out.println("You're running a pre 1.6 Java.");
            }
        } catch (final AccessControlException e) {
            /* probably running with Java WebStart. */
            setFileOperationsAllowed(false);
        } catch (final ExceptionInInitializerError e) {
            /* probably running with Java WebStart. */
            setFileOperationsAllowed(false);
        }
        if (isFileOperationsAllowed()) {
            try {
                AppProps.load();
                final String directory = AppProps.get("directory");
                if (directory != null) {
                    fileChooser.setCurrentDirectory(new File(directory));
                }
                AppProps.setDefault("fullScreen", false);
                wantsFullScreen = AppProps.getBoolean("fullScreen");
            } catch (final FileNotFoundException e1) {
                ignore();
            } catch (final IOException e1) {
                System.err.println("error loading properties: " + e1.getMessage());
            }
            versionChecker = new AppVersionChecker(DOWNLOAD_BASE_URL);
        }
        final String language = AppProps.get("locale.language");
        if (language != null) {
            I18N.setLanguage(language);
        }
        final String theme = AppProps.get("theme.name");
        applyTheme(theme);

        setJMenuBar(createMenuBar(isFileOperationsAllowed()));

        content.add(createEditorComponent(), BorderLayout.CENTER);
        content.add(createRightSideComponent(), BorderLayout.EAST);

        helpWindow = new HelpWindow();
        variablesWindow = new VariablesWindow();
        aboutBox = new AboutBox(this);
        preferencesBox = new PreferencesBox(this);
        pack();
        editor.requestFocusInWindow();
        setVisible(true);
        newFile();
        conditionallyReportNewVersion();
    }

    private void conditionallyReportNewVersion() {
        if (versionChecker != null && versionChecker.isNewVersionAvailable()) {
            final String msg = I18N.msg("window.text.newVersion", HOME_URL);
            final String title = I18N.msg("window.title.newVersion");
            JOptionPane.showMessageDialog(this, msg, title, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public BasusIde() {
        super("main");
        AppProps.setApplicationName("basus");
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setDefaultDimension(screenSize.width - 50, screenSize.height - 70);
        setDefaultMaximized(true);
        setTitle(I18N.msg("window.title.main"));
        SwingUtils.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                createAndShow();
            }
        });
    }

    public DrawingArea getDrawingArea() {
        return outputCanvas;
    }

    public Console getConsole() {
        return outputCanvas;
    }

    @Override
    public void notifyStarting() {
        runButton.setEnabled(false);
        stopButton.setEnabled(true);
        menuRunRun.setEnabled(false);
        menuRunStep.setEnabled(false);
        menuRunStop.setEnabled(true);
        message.setText("");
        SwingUtils.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                outputCanvas.requestFocusInWindow();
            }
        });
    }

    private void displayThrowable(final Throwable throwable) {
        message.setText(throwable.getMessage());
        if (throwable instanceof AbstractBasusException) {
            final TextLocation location = ((AbstractBasusException) throwable).getTextLocation();
            if (location != null) {
                final int line = location.getLine() - 1;
                try {
                    editor.setCaretPosition(editor.getLineStartOffset(line)
                                            + location.getColumn() - 1);
                } catch (final BadLocationException e1) {
                    e1.printStackTrace(System.err);
                }
                editor.setErrorHighlightedText(location, null);
            }
        } else if (throwable instanceof StackOverflowError) {
            message.setText(I18N.msg("err.stackOverflow"));
        }
    }

    @Override
    public void notifyStopping() {
        if (fullScreen != null) {
            if (fullScreen.leaveFullScreen()) {
                outputCanvas.copyBackgroundImageFrom(fullScreen.getOutputCanvas());
                outputCanvas.flush();
            }
            fullScreen = null;
        }
        runButton.setEnabled(true);
        stopButton.setEnabled(false);
        menuRunRun.setEnabled(true);
        menuRunStep.setEnabled(true);
        menuRunStop.setEnabled(false);
        editor.setStepHighlightedText(null, null);
        final Throwable throwable = runner.getLastThrowable();
        if (throwable != null) {
            displayThrowable(throwable);
        }
        SwingUtils.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                editor.requestFocusInWindow();
                conditionallyReportNewVersion();
            }
        });
    }

    @Override
    public void updateButtons() {
        menuEditUndo.setEnabled(editor.canUndo());
        menuEditRedo.setEnabled(editor.canRedo());
        menuEditCut.setEnabled(editor.canCut());
        menuEditCopy.setEnabled(editor.canCopy());
        menuEditPaste.setEnabled(editor.canPaste());
    }

    @Override
    public void openContexHelp(final String word) {
        helpWindow.openTopic(word);
    }

    @Override
    public void openHelpContents() {
        helpWindow.openContents();
    }

    public void setCurrentSourceUrl(final URL url) {
        if (url == null && fileOperationsAllowed) {
            currentSourceUrl = IoUtils.fileToUrl(System.getProperty("user.dir"));
        } else {
            currentSourceUrl = url;
        }
    }

    public void setCurrentFile(final File currentFile) {
        this.currentFile = currentFile;
        final StringBuilder sb = new StringBuilder();
        sb.append(I18N.msg("window.title.main"));
        if (currentFile != null) {
            sb.append(" - ");
            sb.append(currentFile.getName());
            setCurrentSourceUrl(IoUtils.fileToUrl(currentFile));
        }
        SwingUtils.invokeLater(new Runnable() {
            @Override
            public void run() {
                setTitle(sb.toString());
            }
        });
    }

    public void loadFile(final String filename) {
        if (filename == null) {
            return;
        }
        SwingUtils.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                unconditionalOpen(new File(filename));
            }
        });
    }

    public void runCurrentProgram() {
        SwingUtils.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                startRunning(RunMode.NORMAL);
            }
        });
    }

    public void format() {
        SwingUtils.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    final String formattedProgram = formatter.format(editor.getText());
                    editor.setText(formattedProgram);
                } catch (final ParserException e) {
                    displayThrowable(e);
                }
            }
        });
    }

    @Override
    public void terminationRequested() {
        runner.stopProgram();
    }

    public AppVersionChecker getVersionChecker() {
        return versionChecker;
    }

    @Override
    public void beginStatement(final Statement statement, final EvaluationContext context) {
        long t = System.currentTimeMillis();
        SwingUtils.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                editor.setStepHighlightedText(statement.getStartLocation(), statement.getStartLocation());
            }
        });
        t = STEP_WAIT_MS - (System.currentTimeMillis() - t);
        try {
            if (t > 0L) {
                Thread.sleep(t);
            }
        } catch (final InterruptedException e) {
            /* ingore */
            return;
        }
    }

    @Override
    public void endStatement(final Statement statement, final EvaluationContext context) {
    }

    public void applyTheme(final String theme) {
        try {
            for (final UIManager.LookAndFeelInfo lookAndFeel : UIManager.getInstalledLookAndFeels()) {
                if (lookAndFeel.getName().equalsIgnoreCase(theme)) {
                    UIManager.setLookAndFeel(lookAndFeel.getClassName());
                    for (final Window window : Window.getWindows()) {
                        SwingUtilities.updateComponentTreeUI(window);
                    }
                }
            }
        } catch (final Exception ex) {
            /* ignore */
            return;
        }
    }

}
