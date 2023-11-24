package no.shhsoft.basus.crosscompilers;

import no.shhsoft.basus.language.StatementList;
import no.shhsoft.basus.language.parser.BasusParser;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CrossCompilers {

    private static final CrossCompilers INSTANCE = new CrossCompilers();
    private final Map<String, CrossCompiler> crossCompilerMap = new HashMap<>();

    public static CrossCompilers getInstance() {
        return INSTANCE;
    }

    public int getNumCrossCompilers() {
        return crossCompilerMap.size();
    }

    public CrossCompiler getCrossCompiler(final String name) {
        return crossCompilerMap.get(name);
    }

    public void compile(final CrossCompiler crossCompiler, final String programFileName, final String program) {
        final String baseName = getBaseName(programFileName);
        final StatementList statements = BasusParser.parse(program);
        crossCompiler.compile(statements, baseName);
    }

    private static String getBaseName(final String path) {
        return path.replaceAll(".*[/\\\\](.*)\\.bus", "$1");
    }

}
