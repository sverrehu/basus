package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface Function {

    int NUM_ARGS_ANY = -1;
    int NUM_ARGS_ANY_EVEN = -2;
    int NUM_ARGS_ZERO_OR_ONE = -3;
    int NUM_ARGS_ONE_OR_TWO = -4;
    int NUM_ARGS_ONE_OR_THREE = -5;
    int NUM_ARGS_THREE_OR_FOUR = -6;

    String getName();

    int getNumArgs();

    Class<?>[] getArgTypes();

    Value call(EvaluationContext context, TextLocationHolder locationHolder, Value[] args);

}
