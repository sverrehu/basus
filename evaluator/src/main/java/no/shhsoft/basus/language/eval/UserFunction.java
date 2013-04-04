package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.language.FunctionStatement;
import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.IntegerValue;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class UserFunction
extends AbstractFunction {

    public static final String RETURN_VARIABLE_NAME = "%%ret";
    private final Evaluator evaluator;
    private final FunctionStatement functionStatement;

    @Override
    protected Value implCall(final EvaluationContext context,
                             final TextLocationHolder locationHolder, final Value[] args) {
        final EvaluationContext localContext = context.getWrappedContext();
        localContext.setLocalVariable(RETURN_VARIABLE_NAME, IntegerValue.ZERO, locationHolder);
        for (int q = 0; q < args.length; q++) {
            localContext.setLocalVariable(functionStatement.getArgumentNames()[q], args[q],
                                          locationHolder);
        }
        evaluator.evaluate(functionStatement.getStatements(), localContext);
        localContext.setReturnFromFunction(false);
        return localContext.getVariable(RETURN_VARIABLE_NAME, locationHolder);
    }

    public UserFunction(final FunctionStatement functionStatement, final Evaluator evlauator) {
        super(functionStatement.getName(), functionStatement.getArgumentNames().length, null);
        this.evaluator = evlauator;
        this.functionStatement = functionStatement;
    }

}
