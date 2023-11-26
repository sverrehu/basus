package no.shhsoft.basus.crosscompilers.wasm;

import no.shhsoft.basus.crosscompilers.CrossCompiler;
import no.shhsoft.basus.language.StatementList;
import no.shhsoft.wasm.io.ModuleWriter;
import no.shhsoft.wasm.model.Module;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class WasmCrossCompiler
implements CrossCompiler {

    @Override
    public void compile(final StatementList statements, final String baseName) {
        final Module module= new Module(1);
        new ModuleWriter().writeToFile(module, baseName + ".wasm");
    }

}
