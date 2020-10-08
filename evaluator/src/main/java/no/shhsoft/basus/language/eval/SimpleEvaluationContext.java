package no.shhsoft.basus.language.eval;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import no.shhsoft.basus.language.eval.runtime.Console;
import no.shhsoft.basus.language.eval.runtime.DrawingArea;
import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.ImageValue;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class SimpleEvaluationContext
implements EvaluationContext {

    private final long startTime;
    private final Map<String, Value> variables = new HashMap<>(1024);
    private final Console console;
    private final DrawingArea drawingArea;
    private SimpleEvaluationContext parent;
    private final Map<String, Function> functions;
    private boolean returnFromFunction;
    private boolean stopProgram;
    private final URL sourceUrl;

    private static void error(final String key, final TextLocationHolder holder,
                              final Object... args) {
        Evaluator.error(key, holder, args);
    }

    private SimpleEvaluationContext(final SimpleEvaluationContext parent) {
        this.parent = parent;
        this.console = parent.console;
        this.drawingArea = parent.drawingArea;
        this.functions = parent.functions;
        this.startTime = parent.startTime;
        this.sourceUrl = parent.sourceUrl;
    }

    public SimpleEvaluationContext(final Console console, final DrawingArea drawingArea,
                                   final URL sourceUrl) {
        this.console = console;
        this.drawingArea = drawingArea;
        this.sourceUrl = sourceUrl;
        functions = new HashMap<>(1024);
        startTime = System.currentTimeMillis();
        MathFunctions.register(this);
        ConversionFunctions.register(this);
        StringFunctions.register(this);
        MiscFunctions.register(this);
        UserInputFunctions.register(this);
        GraphicFunctions.register(this);
//        for (final Map.Entry<String, Function> function : functions.entrySet()) {
//            System.out.println(function.getKey());
//        }
    }

    @Override
    public EvaluationContext getWrappedContext() {
        SimpleEvaluationContext context = this;
        while (context.parent != null) {
            context = context.parent;
        }
        return new SimpleEvaluationContext(context);
    }

    @Override
    public Value callFunction(final String name, final Value[] args,
                              final TextLocationHolder holder) {
        final Function function = functions.get(name);
        if (function == null) {
            error("err.undefinedFunc", holder, name);
            /* just to please the compiler. */
            return null;
        }
        return function.call(this, holder, args);
    }

    @Override
    public void setLocalVariable(final String name, final Value value,
                                 final TextLocationHolder holder) {
        variables.put(name, value);
    }

    @Override
    public boolean isDefined(final String name) {
        if (variables.containsKey(name)) {
            return true;
        }
        if (parent != null) {
            return parent.isDefined(name);
        }
        return false;
    }

    @Override
    public void setVariable(final String name, final Value value,
                            final TextLocationHolder holder) {
        SimpleEvaluationContext context = this;
        while (!context.variables.containsKey(name) && context.parent != null) {
            context = context.parent;
        }
        context.setLocalVariable(name, value, holder);
    }

    @Override
    public Value getVariable(final String name, final TextLocationHolder holder) {
        final Value value = variables.get(name);
        if (value == null) {
            if (parent != null) {
                return parent.getVariable(name, holder);
            }
            error("err.undefinedVar", holder, name);
        }
        return value;
    }

    @Override
    public Set<String> getVariableNames() {
        final Set<String> variableNames = new HashSet<>();
        variableNames.addAll(variables.keySet());
        if (parent != null) {
            variableNames.addAll(parent.getVariableNames());
        }
        return variableNames;
    }

    @Override
    public void registerFunction(final Function function, final TextLocationHolder holder) {
        if (functions.containsKey(function.getName())) {
            error("err.functionAlreadyDefined", holder, function.getName());
        }
        functions.put(function.getName(), function);
    }

    public void registerFunction(final Function function) {
        registerFunction(function, null);
    }

    @Override
    public Console getConsole() {
        return console;
    }

    @Override
    public DrawingArea getDrawingArea() {
        return drawingArea;
    }

    @Override
    public long getStartTime() {
        return startTime;
    }

    @Override
    public void setReturnFromFunction(final boolean status) {
        returnFromFunction = status;
    }

    @Override
    public boolean isReturnFromFunction() {
        return returnFromFunction;
    }

    @Override
    public void setStopProgram(final boolean status) {
        stopProgram = status;
    }

    @Override
    public boolean isStopProgram() {
        return stopProgram;
    }

    @Override
    public ImageValue loadImage(final String name, final TextLocationHolder holder) {
        for (int q = name.length() - 1; q >= 0; q--) {
            final char c = name.charAt(q);
            if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9') {
                continue;
            }
            if (c == '.' || c == '_' || c == '-' || c == '/') {
                continue;
            }
            error("err.load.invalidName", holder, name);
        }
        if (name.contains("..") || name.startsWith("/")) {
            error("err.load.invalidName", holder, name);
        }
        URL url = null;
        try {
            url = new URL(sourceUrl, name);
            final BufferedImage image = getDrawingArea().loadImage(url);
            return new ImageValue(image);
        } catch (final Exception e) {
            /* try loading from resource before giving up. */
            try {
                url = this.getClass().getResource("/" + name);
                final BufferedImage image = getDrawingArea().loadImage(url);
                return new ImageValue(image);
            } catch (final Exception e2) {
                error("err.load.ioError", holder, url, e.getMessage());
                /* just to please the compiler */
                return null;
            }
        }
    }

}
