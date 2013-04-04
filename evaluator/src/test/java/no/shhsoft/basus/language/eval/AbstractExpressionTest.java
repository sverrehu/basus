package no.shhsoft.basus.language.eval;

import org.junit.Test;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractExpressionTest
extends AbstractEvaluatorTest {

    @Test
    public final void testSimpleStuff1() {
        assertExpressionEquals("1+2", "3");
    }

    @Test
    public final void testSimpleStuff2() {
        assertExpressionEquals("1+2+3", "6");
    }

    @Test
    public final void testSimpleStuff3() {
        assertExpressionEquals("1+(2+3)", "6");
    }

    @Test
    public final void testSimpleStuff4() {
        assertExpressionEquals("5-4", "1");
    }

    @Test
    public final void testSimpleStuff5() {
        assertExpressionEquals("5-1-2", "2");
    }

    @Test
    public final void testSimpleStuff6() {
        assertExpressionEquals("5-(1-2)", "6");
    }

    @Test
    public final void testSimpleStuff7() {
        assertExpressionEquals("5+2*3", "11");
    }

    @Test
    public final void testSimpleStuff8() {
        assertExpressionEquals("(5+2)*3", "21");
    }

    @Test
    public final void testSimpleStuff9() {
        assertExpressionEquals("5*2+3", "13");
    }

    @Test
    public final void testSimpleStuff10() {
        assertExpressionEquals("5*(2+3)", "25");
    }

    @Test
    public final void testSimpleStuff11() {
        assertExpressionEquals("40/2", "20");
    }

    @Test
    public final void testSimpleStuff12() {
        assertExpressionEquals("40/5/2", "4");
    }

    @Test
    public final void testSimpleStuff13() {
        assertExpressionEquals("40/(5/2)", "20");
    }

    @Test
    public final void testSimpleStuff14() {
        assertExpressionEquals("40/(5.0/2)", "16.0");
    }

    @Test
    public final void testSimpleStuff15() {
        assertExpressionEquals("2^3", "8");
    }

    @Test
    public final void testSimpleStuff16() {
        assertExpressionEquals("2^2^3", "256");
    }

    @Test
    public final void testSimpleStuff17() {
        assertExpressionEquals("2^(2^3)", "256");
    }

    @Test
    public final void testSimpleStuff18() {
        assertExpressionEquals("(2^2)^3", "64");
    }

    @Test
    public final void testSimpleStuff19() {
        assertExpressionEquals("6--3", "9");
    }

    @Test
    public final void testSimpleStuff20() {
        assertExpressionEquals("6-(-3)", "9");
    }

    @Test
    public final void testSimpleStuff21() {
        assertExpressionEquals("6+-3", "3");
    }

    @Test
    public final void testSimpleStuff22() {
        assertExpressionEquals("6+(-3)", "3");
    }

    @Test
    public final void testSimpleStuff23() {
        assertExpressionEquals("-(5-3)", "-2");
    }

}
