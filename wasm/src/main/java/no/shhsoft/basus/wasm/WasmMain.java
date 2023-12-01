package no.shhsoft.basus.wasm;

import no.shhsoft.basus.language.eval.runtime.BasusRunner;
import no.shhsoft.basus.language.eval.runtime.Console;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class WasmMain {

    public static void main(final String[] args) {
        final String program = "a = 1; b = 2; c = a + b; println(c);";
        /*
        new BasusRunner().runProgram(program, new Console() {
            @Override
            public void print(final String s) {
                System.out.print(s);
            }

            @Override
            public void println(final String s) {
                System.out.println(s);
            }

            @Override
            public String readln() {
                throw new RuntimeException("Not implemented");
            }

            @Override
            public int readKey() {
                throw new RuntimeException("Not implemented");
            }
        }, null, null, null);
        */
    }

}
