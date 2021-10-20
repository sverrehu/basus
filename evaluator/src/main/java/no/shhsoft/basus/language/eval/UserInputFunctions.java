package no.shhsoft.basus.language.eval;

import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.BooleanValue;
import no.shhsoft.basus.value.IntegerValue;
import no.shhsoft.basus.value.NumericValue;
import no.shhsoft.basus.value.StringValue;
import no.shhsoft.basus.value.Value;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
final class UserInputFunctions {

    private static final Function READLN = new Readln();
    private static final Function READ_CHAR = new ReadChar();
    private static final Function KEY_PRESSED = new KeyPressed();
    private static final Function WAIT_KEY = new WaitKey();
    private static final Function CLEAR_KEYBOARD_BUFFER = new ClearKeyboardBuffer();
    private static final Function MOUSE_X = new MouseX();
    private static final Function MOUSE_Y = new MouseY();
    private static final Function MOUSE_BUTTON_PRESSED = new MouseButtonPressed();
    static final List<Function> ALL_FUNCTIONS = Arrays.asList(
        READLN, READ_CHAR, WAIT_KEY, CLEAR_KEYBOARD_BUFFER, KEY_PRESSED,
        MOUSE_X, MOUSE_Y, MOUSE_BUTTON_PRESSED
    );

    private UserInputFunctions() {
    }

    private static final class Readln
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return new StringValue(context.getConsole().readln());
        }

        public Readln() {
            super("readln", 0, null);
        }

    }

    private static final class ReadChar
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return IntegerValue.get(context.getConsole().readKey());
        }

        public ReadChar() {
            super("readChar", 0, null);
        }

    }

    private static final class KeyPressed
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            int keyCode = ((NumericValue) args[0]).getValueAsInteger();
            if (keyCode >= 'a' && keyCode <= 'z') {
                keyCode = 'A' + (keyCode - 'a');
            }
            return BooleanValue.valueOf(context.getDrawingArea().isKeyPressed(keyCode));
        }

        public KeyPressed() {
            super("keyPressed", 1, new Class<?>[] { NumericValue.class });
        }

    }

    private static final class WaitKey
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return IntegerValue.get(context.getDrawingArea().waitForKeyPressed());
        }

        public WaitKey() {
            super("waitKey", 0, null);
        }

    }

    private static final class ClearKeyboardBuffer
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            context.getDrawingArea().clearKeyboardBuffer();
            return IntegerValue.ZERO;
        }

        public ClearKeyboardBuffer() {
            super("clearKeyboardBuffer", 0, null);
        }

    }

    private static final class MouseX
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return IntegerValue.get(context.getDrawingArea().getMouseX());
        }

        public MouseX() {
            super("mouseX", 0, null);
        }

    }

    private static final class MouseY
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            return IntegerValue.get(context.getDrawingArea().getMouseY());
        }

        public MouseY() {
            super("mouseY", 0, null);
        }

    }

    private static final class MouseButtonPressed
    extends BuiltinFunction {

        @Override
        protected Value implCall(final EvaluationContext context,
                                 final TextLocationHolder locationHolder, final Value[] args) {
            final int mouseButton = ((NumericValue) args[0]).getValueAsInteger();
            return BooleanValue.valueOf(context.getDrawingArea().isMouseButtonPressed(mouseButton));
        }

        public MouseButtonPressed() {
            super("mouseButtonPressed", 1, new Class<?>[] { NumericValue.class });
        }

    }

    public static void register(final SimpleEvaluationContext context) {
        for (final Function function : ALL_FUNCTIONS) {
            context.registerFunction(function);
        }
    }

}
