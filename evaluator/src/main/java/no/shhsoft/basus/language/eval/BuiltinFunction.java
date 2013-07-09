package no.shhsoft.basus.language.eval;


/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
abstract class BuiltinFunction
extends AbstractFunction {

    public BuiltinFunction(final String name, final int numArgs, final Class<?>[] argTypes) {
        super(name, numArgs, argTypes);
    }

}
