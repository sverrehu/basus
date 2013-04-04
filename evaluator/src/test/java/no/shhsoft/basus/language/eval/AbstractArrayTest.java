package no.shhsoft.basus.language.eval;

import org.junit.Test;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public abstract class AbstractArrayTest
extends AbstractEvaluatorTest {

    @Test
    public final void testAssignmentOneDimension() {
        assertOutputEquals("a[1]=1; print(a[1])", "1");
    }

    @Test
    public final void testAssignmentTwoDimensions() {
        assertOutputEquals("a[1,2]=1; print(a[1,2])", "1");
    }

    @Test
    public final void testAssignmentThreeDimensions() {
        assertOutputEquals("a[1,2,3]=1; print(a[1,2,3])", "1");
    }

    @Test
    public final void testFunctionArgument() {
        assertOutputEquals("a[1,2,3]=1; function f(x) print(x[1,2,3]); endfunc; f(a);", "1");
    }

    @Test
    public final void testFunctionReturn() {
        assertOutputEquals("function f() x[1,2,3]=1; return x; endfunc; print(f()[1,2,3]);", "1");
    }

}
