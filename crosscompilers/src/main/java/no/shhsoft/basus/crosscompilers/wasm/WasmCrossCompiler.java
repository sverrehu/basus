package no.shhsoft.basus.crosscompilers.wasm;

import no.shhsoft.basus.crosscompilers.CrossCompiler;
import no.shhsoft.basus.language.Statement;
import no.shhsoft.basus.language.StatementList;
import no.shhsoft.wasm.io.ModuleWriter;
import no.shhsoft.wasm.model.Module;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class WasmCrossCompiler
implements CrossCompiler {

    private Module module;

    @Override
    public void compile(final StatementList statements, final String baseName) {
        if (module != null) {
            throw new RuntimeException("Method is not reentrant. Need a new WasmCrossCompiler to call compile again.");
        }
        module= new Module(1);
        for (int q = 0; q < statements.getNumStatements(); q++) {
            compileStatement(statements.getStatement(q));
        }
        new ModuleWriter().writeToFile(module, baseName + ".wasm");
    }

    private void compileStatement(final Statement statement) {

    }

}
