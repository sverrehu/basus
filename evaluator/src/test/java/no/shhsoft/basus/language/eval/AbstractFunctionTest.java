package no.shhsoft.basus.language.eval;

import org.junit.Test;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractFunctionTest
extends AbstractEvaluatorTest {

    @Test
    public final void testGlobalAssignment() {
        assertNoArgCallEquals("x=1", "x=2", "x", "2");
    }

    @Test
    public final void testLocalAssignment() {
        assertNoArgCallEquals("x=1", "local x=2", "x", "1");
    }

    @Test
    public final void testLocalImplicitAssignment() {
        assertNoArgCallEquals("x=1", "local x=2; x=3", "x", "1");
    }

    @Test
    public final void testLocalArgumentAssignment() {
        assertArgCallEquals("x=1", "x=2", "x", "x", "x", "1");
    }

    @Test
    public final void testLocalAssignmentTwoFunctions1() {
        assertNoArgCallEquals("x=1;function f() x=2; endfunc;", "local x=3; f();", "x", "2");
    }

    @Test
    public final void testLocalAssignmentTwoFunctions2() {
        assertNoArgCallEquals("x=1;function f() x=2; endfunc;", "x=3; f();", "x", "2");
    }

    @Test
    public final void testLocalAssignmentTwoFunctions3() {
        assertNoArgCallEquals("x=1;function f() local x=2; endfunc;", "x=3; f();", "x", "3");
    }

}
