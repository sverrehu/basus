package no.shhsoft.basus.ui.ide;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ActionMap;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import javax.swing.text.TextAction;
import javax.swing.undo.UndoManager;

import no.shhsoft.basus.utils.TextLocation;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
@SuppressWarnings("synthetic-access")
public final class Editor
extends JTextPane
implements DocumentListener, KeyListener {

    private static final long serialVersionUID = 1L;
    private static final DefaultHighlightPainter ERROR_HIGHLIGHT_PAINTER
        = new DefaultHighlighter.DefaultHighlightPainter(new Color(255, 0, 0, 80));
    private static final DefaultHighlightPainter STEP_HIGHLIGHT_PAINTER
        = new DefaultHighlighter.DefaultHighlightPainter(new Color(0, 255, 0, 80));
    private UndoManager undoManager;
    private EditorButtonsProvider buttonsProvider;
    private Object currentHighlight;
    private boolean changed;
    private HelpProvider helpProvider;
    private BasusSyntaxDocument basusSyntaxDocument;

    private static class AutoIndentAction
    extends TextAction {

        private static final long serialVersionUID = 1L;

        public AutoIndentAction() {
            super(DefaultEditorKit.insertBreakAction);
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            final JTextComponent textComponent = getTextComponent(event);
            if (textComponent == null) {
                return;
            }
            try {
                final Document doc = textComponent.getDocument();
                final Element rootElement = doc.getDefaultRootElement();
                final int selectionStart = textComponent.getSelectionStart();
                final int line = rootElement.getElementIndex(selectionStart);
                final int start = rootElement.getElement(line).getStartOffset();
                final int end = rootElement.getElement(line).getEndOffset();
                final int length = end - start;
                final String text = doc.getText(start, length);
                int offset;
                for (offset = 0; offset < length; offset++) {
                    final char c = text.charAt(offset);
                    if (c != ' ' && c != '\t') {
                        break;
                    }
                }
                if (selectionStart - start > offset) {
                    textComponent.replaceSelection("\n" + text.substring(0, offset));
                } else {
                    textComponent.replaceSelection("\n");
                }
            } catch (final BadLocationException e) {
                return;
            }
        }
    }

    private void notifyProvider() {
        if (buttonsProvider == null) {
            return;
        }
        buttonsProvider.updateButtons();
    }

    private void textChanged() {
        setChanged(true);
        setErrorHighlightedText(null, null);
    }

    private void init() {
        basusSyntaxDocument = new BasusSyntaxDocument();
        setStyledDocument(basusSyntaxDocument);
        undoManager = new UndoManager();
        getDocument().addUndoableEditListener(new UndoableEditListener() {
            @Override
            public void undoableEditHappened(final UndoableEditEvent e) {
                if (!basusSyntaxDocument.isHighlighting()) {
                    undoManager.addEdit(e.getEdit());
                    notifyProvider();
                }
            }
        });
        getDocument().addDocumentListener(this);
        addKeyListener(this);
        final ActionMap am = getActionMap();
        am.put(DefaultEditorKit.insertBreakAction, new AutoIndentAction());
    }

    private boolean isWordChar(final char c) {
        return Character.isLetter(c) || Character.isDigit(c) || c == '_';
    }

    private String getCurrentWord() {
        final String text = getText();
        int idx = getCaretPosition();
        while (idx > 0 && isWordChar(text.charAt(idx - 1))) {
            --idx;
        }
        final StringBuilder sb = new StringBuilder();
        while (idx < text.length() && isWordChar(text.charAt(idx))) {
            sb.append(text.charAt(idx));
            ++idx;
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    public synchronized void setHighlightedText(final DefaultHighlightPainter painter,
                                                final TextLocation startLocation,
                                       @SuppressWarnings("unused") final TextLocation endLocation) {
        final Highlighter highlighter = getHighlighter();
        if (currentHighlight != null) {
            highlighter.removeHighlight(currentHighlight);
            currentHighlight = null;
        }
        if (startLocation == null) {
            return;
        }
        final int line = startLocation.getLine() - 1;
        if (line >= 0) {
            try {
                final int start = getLineStartOffset(line);
                final int end = getLineEndOffset(line);
                currentHighlight = highlighter.addHighlight(start, end, painter);
            } catch (final BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    public Editor() {
        init();
    }

    /* Copied from JTextArea */
    int getLineCount() {
        final Element map = getDocument().getDefaultRootElement();
        return map.getElementCount();
    }

    /* Copied from JTextArea */
    public int getLineStartOffset(final int line)
    throws BadLocationException {
        final int lineCount = getLineCount();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= lineCount) {
            throw new BadLocationException("No such line", getDocument().getLength() + 1);
        } else {
            final Element map = getDocument().getDefaultRootElement();
            final Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    /* Copied from JTextArea */
    int getLineEndOffset(final int line)
    throws BadLocationException {
        final int lineCount = getLineCount();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line >= lineCount) {
            throw new BadLocationException("No such line", getDocument().getLength() + 1);
        } else {
            final Element map = getDocument().getDefaultRootElement();
            final Element lineElem = map.getElement(line);
            final int endOffset = lineElem.getEndOffset();
            // hide the implicit break at the end of the document
            return ((line == lineCount - 1) ? (endOffset - 1) : endOffset);
        }
    }

    public void setButtonsProvider(final EditorButtonsProvider buttonsProvider) {
        this.buttonsProvider = buttonsProvider;
        notifyProvider();
    }

    public void undo() {
        undoManager.undo();
        notifyProvider();
    }

    public void redo() {
        undoManager.redo();
        notifyProvider();
    }

    public boolean canUndo() {
        return undoManager.canUndo();
    }

    public boolean canRedo() {
        return undoManager.canUndo();
    }

    public boolean canCut() {
        /* if needed, figure out how canCut can return a reasonable value. */
        return true;
    }

    public boolean canCopy() {
        return canCut();
    }

    public boolean canPaste() {
        /* if needed, figure out how canPaste can return a reasonable value. */
        return true;
    }

    public void setErrorHighlightedText(final TextLocation startLocation,
                                        final TextLocation endLocation) {
        setHighlightedText(ERROR_HIGHLIGHT_PAINTER, startLocation, endLocation);
    }

    public void setStepHighlightedText(final TextLocation startLocation,
                                       final TextLocation endLocation) {
        setHighlightedText(STEP_HIGHLIGHT_PAINTER, startLocation, endLocation);
    }

    @Override
    public void changedUpdate(final DocumentEvent e) {
        //textChanged();
    }

    @Override
    public void insertUpdate(final DocumentEvent e) {
        textChanged();
    }

    @Override
    public void removeUpdate(final DocumentEvent e) {
        textChanged();
    }

    public void setChanged(final boolean changed) {
        this.changed = changed;
    }

    public boolean isChanged() {
        return changed;
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        final int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_HELP || keyCode == KeyEvent.VK_F1) {
            if (helpProvider == null) {
                return;
            }
            final String word = getCurrentWord();
            helpProvider.openContextHelp(word);
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
    }

    @Override
    public void keyTyped(final KeyEvent e) {
    }

    public void setHelpProvider(final HelpProvider helpProvider) {
        this.helpProvider = helpProvider;
    }

    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        final StyledDocument styledDocument = getStyledDocument();
        if (styledDocument == null) {
            return;
        }
        ((BasusSyntaxDocument) styledDocument).updateFontSize(font.getSize());
    }

}
