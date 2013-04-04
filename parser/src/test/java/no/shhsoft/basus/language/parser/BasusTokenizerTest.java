package no.shhsoft.basus.language.parser;

import static org.junit.Assert.assertEquals;
import no.shhsoft.basus.value.NumericValue;

import org.junit.Test;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BasusTokenizerTest {

    private void assertNumber(final String expression, final double number) {
        final Token token = new BasusTokenizer(expression).nextToken();
        assertEquals(TokenType.CONSTANT, token.getType());
        assertEquals(number, ((NumericValue) token.getConstant()).getValueAsDouble(), 0.0000001);
    }

    @Test
    public void shouldParseNumbers()
    throws Exception {
        assertNumber("1", 1.0);
        assertNumber("123", 123);
        assertNumber("3.1415", 3.1415);
        assertNumber(".1234", 0.1234);
        assertNumber("1.2e1", 12);
        assertNumber("1.2e2", 120);
        assertNumber("1.2e-1", 0.12);
        assertNumber("1.2e-2", 0.012);
        assertNumber("1.2E-2", 0.012);
    }

}
