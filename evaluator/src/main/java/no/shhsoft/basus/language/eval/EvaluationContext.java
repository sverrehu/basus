package no.shhsoft.basus.language.eval;

import java.util.Set;

import no.shhsoft.basus.language.eval.runtime.Console;
import no.shhsoft.basus.language.eval.runtime.DrawingArea;
import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.ImageValue;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface EvaluationContext {

    void registerFunction(Function function, TextLocationHolder holder);

    EvaluationContext getWrappedContext();

    boolean isDefined(String name);

    void setVariable(String name, Value value, TextLocationHolder holder);

    void setLocalVariable(String name, Value value, TextLocationHolder holder);

    Value getVariable(String name, TextLocationHolder holder);

    Set<String> getVariableNames();

    Value callFunction(String name, Value[] args, TextLocationHolder holder);

    Console getConsole();

    DrawingArea getDrawingArea();

    long getStartTime();

    void setReturnFromFunction(boolean status);

    boolean isReturnFromFunction();

    void setStopProgram(boolean status);

    boolean isStopProgram();

    ImageValue loadImage(String name, TextLocationHolder holder);

}
