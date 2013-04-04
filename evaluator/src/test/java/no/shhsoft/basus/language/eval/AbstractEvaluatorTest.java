package no.shhsoft.basus.language.eval;

import static org.junit.Assert.assertEquals;
import no.shhsoft.basus.language.eval.runtime.BasusRunner;
import no.shhsoft.basus.language.eval.runtime.Console;
import no.shhsoft.basus.tools.format.BasusFormatter;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractEvaluatorTest {

    private static final class StringBuilderConsole
    implements Console {

        private final StringBuilder sb = new StringBuilder();

        public StringBuilderConsole() {
        }

        @Override
        public void print(final String s) {
            sb.append(s);
        }

        @Override
        public void println(final String s) {
            sb.append(s);
            sb.append('\n');
        }

        @Override
        public int readKey() {
            throw new RuntimeException("not implemented");
        }

        @Override
        public String readln() {
            throw new RuntimeException("not implemented");
        }

        @Override
        public String toString() {
            return sb.toString();
        }

    }

    protected final String formatProgram(final String program) {
        return new BasusFormatter().format(program);
    }

    protected abstract String transformProgram(String program);

    protected final String evaluate(final String program) {
        final StringBuilderConsole console = new StringBuilderConsole();
        final BasusRunner runner = new BasusRunner();
        runner.runProgram(transformProgram(program), console, null, null, null);
        runner.waitForProgramToFinish();
        return console.toString().trim();
    }

    protected final void assertOutputEquals(final String program,
                                            final String expected) {
        final String actual = evaluate(program);
        assertEquals(expected, actual);
    }

    protected final void assertExpressionEquals(final String expression,
                                                final String expected) {
        assertOutputEquals("print(" + expression + ");", expected);
    }

    protected final void assertNoArgCallEquals(final String pre, final String functionBody,
                                          final String whatToPrint, final String expected) {
        assertOutputEquals(pre + ";function foo() " + functionBody
                           + ";endfunc;foo();print(" + whatToPrint + ");",
                           expected);
    }

    protected final void assertArgCallEquals(final String pre, final String functionBody,
                                             final String argName, final String argValue,
                                             final String whatToPrint, final String expected) {
        assertOutputEquals(pre + ";function foo(" + argName + ") " + functionBody
                           + ";endfunc;foo(" + argValue + ");print(" + whatToPrint + ");",
                           expected);
    }

}
