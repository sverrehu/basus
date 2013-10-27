package no.shhsoft.basus;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import no.shhsoft.awt.FullScreenDisplayModeOptimizer;
import no.shhsoft.basus.language.eval.runtime.BasusRunner;
import no.shhsoft.basus.language.eval.runtime.Console;
import no.shhsoft.basus.language.eval.runtime.DrawingArea;
import no.shhsoft.basus.language.eval.runtime.TerminationRequestListener;
import no.shhsoft.basus.language.parser.ParserException;
import no.shhsoft.basus.tools.format.BasusFormatter;
import no.shhsoft.basus.ui.FullScreenOutput;
import no.shhsoft.basus.ui.OutputCanvas;
import no.shhsoft.basus.ui.WindowedOutput;
import no.shhsoft.basus.ui.ide.BasusIde;
import no.shhsoft.basus.ui.ide.style.BasusToHtmlConverter;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.OptionParser;
import no.shhsoft.utils.OptionParser.Opt;
import no.shhsoft.utils.UncheckedIoException;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Main
implements TerminationRequestListener {

    private enum RunType {
        EDITOR,
        FULL_SCREEN,
        WINDOWED,
        HTML_CONVERTER,
        FORMATTER,
    }
    private static final boolean IDE_AVAILABLE;
    private final Set<RunType> runTypes = new HashSet<RunType>();
    private final BasusRunner runner = new BasusRunner();

    static {
        boolean ideAvailable = true;
        try {
            Class.forName("no.shhsoft.basus.ui.ide.BasusIde");
        } catch (final ClassNotFoundException e) {
            ideAvailable = false;
        }
        IDE_AVAILABLE = ideAvailable;
    }

    private Main() {
    }

    private void fatal(final String message) {
        System.err.println(message);
        System.exit(1);
    }

    private String loadProgram(final String fileName) {
        byte[] data = null;
        try {
            data = IoUtils.readFile(fileName);
        } catch (final UncheckedIoException e) {
            fatal("Unable to load `" + fileName + "'");
        }
        String text;
        try {
            text = new String(data, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return text;
    }

    private void runProgram(final String program, final Console console,
                            final DrawingArea drawingArea, final URL sourceUrl) {
        runner.runProgram(program, console, drawingArea, null, sourceUrl);
        runner.waitForProgramToFinish();
    }

    private void runFullScreen(final String fileName) {
        final String program = loadProgram(fileName);
        final FullScreenOutput output = new FullScreenOutput();
        if (!output.isFullScreenSupported()) {
            fatal("Full-screen mode is not supported");
        }
        output.getOutputCanvas().addTerminationRequestListener(this);
        final FullScreenDisplayModeOptimizer.FullScreenTransform fullScreenTransform
            = output.enterFullScreen(OutputCanvas.AREA_WIDTH, OutputCanvas.AREA_HEIGHT);
        output.getOutputCanvas().setFullScreenTransform(fullScreenTransform);
        runProgram(program, output.getOutputCanvas(), output.getOutputCanvas(),
                   IoUtils.fileToUrl(fileName));
        output.leaveFullScreen();
        /* Need to enforce exit, since AWT has a couple of non-daemon threads
         * running.  Would really like to know how to terminate them without
         * doing an exit. */
        System.exit(0);
    }

    private void runWindowed(final String fileName) {
        final String program = loadProgram(fileName);
        final WindowedOutput output = new WindowedOutput();
        output.getOutputCanvas().addTerminationRequestListener(this);
        output.getOutputCanvas().waitUntilInitiated();
        runProgram(program, output.getOutputCanvas(), output.getOutputCanvas(),
                   IoUtils.fileToUrl(fileName));
        /* See note above about exit. */
        System.exit(0);
    }

    private void convertToHtml(final String fileName) {
        final String program = loadProgram(fileName);
        final String html = BasusToHtmlConverter.toHtml(program);
        System.out.println(html);
    }

    private void format(final String fileName) {
        final String program = loadProgram(fileName);
        try {
            final String formatted = new BasusFormatter().format(program);
            System.out.println(formatted);
        } catch (final ParserException e) {
            System.err.println("Cannot format, since the program contains errors.");
        }
    }

    private BasusIde openEditor(final String fileName) {
        final BasusIde basusIde = new BasusIde();
        if (fileName != null) {
            basusIde.loadFile(fileName);
        }
        return basusIde;
    }

    private void doit(final String[] args) {
        final String[] remainingArgs = new OptionParser(new Opt[] {
            new Opt('e', "editor", "runInEditor"),
            new Opt('f', "full-screen", "runFullScreen"),
            new Opt('h', "help", "help"),
            new Opt('r', "run", "runWindowed"),
            new Opt('\0', "html", "convertToHtml"),
            new Opt('\0', "format", "format"),
        }).parse(args, this);
        if (remainingArgs.length > 1) {
            fatal("Only one program filename may be given");
        }
        String programFileName = null;
        if (remainingArgs.length > 0) {
            programFileName = remainingArgs[0];
        }
        if (runTypes.size() > 1) {
            fatal("It doesn't make any sense to specify multiple run types");
        }
        if (runTypes.size() > 0) {
            if (programFileName == null) {
                fatal("You need to provide a program filename to run");
            }
            switch (runTypes.iterator().next()) {
                case EDITOR:
                    if (!IDE_AVAILABLE) {
                        fatal("There is no editor available in this version of Basus.  Please download the full version.");
                    }
                    openEditor(programFileName).runCurrentProgram();
                    break;
                case FULL_SCREEN:
                    runFullScreen(programFileName);
                    break;
                case WINDOWED:
                    runWindowed(programFileName);
                    break;
                case HTML_CONVERTER:
                    convertToHtml(programFileName);
                    break;
                case FORMATTER:
                    format(programFileName);
                    break;
                default:
                    throw new RuntimeException("strange");
            }
        } else {
            if (IDE_AVAILABLE) {
                openEditor(programFileName);
            } else {
                if (programFileName == null) {
                    fatal("You need to provide a program filename to run");
                }
                runWindowed(programFileName);
            }
        }
    }

    public void help() {
        System.out.println("usage: java -jar basus.jar [options] [program-file]");
        System.out.println();
        if (IDE_AVAILABLE) {
            System.out.println("  -e, --editor        Run program in editor");
        }
        System.out.println("  -f, --full-screen   Run program in full-screen mode");
        System.out.println("  -r, --run           Run program in window");
        System.out.println("      --html          Convert program to HTML on standard out");
        System.out.println("      --format        Pretty-format program to standard out");
        System.exit(0);
    }

    public void runInEditor() {
        runTypes.add(RunType.EDITOR);
    }

    public void runFullScreen() {
        runTypes.add(RunType.FULL_SCREEN);
    }

    public void runWindowed() {
        runTypes.add(RunType.WINDOWED);
    }

    public void convertToHtml() {
        runTypes.add(RunType.HTML_CONVERTER);
    }

    public void format() {
        runTypes.add(RunType.FORMATTER);
    }

    @Override
    public void terminationRequested() {
        runner.stopProgram();
    }

    public static void main(final String[] args) {
        new Main().doit(args);
    }

}
