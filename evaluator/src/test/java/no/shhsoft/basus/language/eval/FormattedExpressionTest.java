package no.shhsoft.basus.language.eval;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class FormattedExpressionTest
extends AbstractExpressionTest {

    @Override
    protected String transformProgram(final String program) {
        return formatProgram(program);
    }

}
