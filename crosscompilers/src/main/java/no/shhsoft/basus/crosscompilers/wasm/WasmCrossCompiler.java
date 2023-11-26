package no.shhsoft.basus.crosscompilers.wasm;

import no.shhsoft.basus.crosscompilers.CrossCompiler;
import no.shhsoft.basus.language.AssignableExpression;
import no.shhsoft.basus.language.AssignmentStatement;
import no.shhsoft.basus.language.Expression;
import no.shhsoft.basus.language.Statement;
import no.shhsoft.basus.language.StatementList;
import no.shhsoft.basus.language.VariableExpression;
import no.shhsoft.basus.language.parser.BasusParser;
import no.shhsoft.wasm.io.ModuleWriter;
import no.shhsoft.wasm.model.Module;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class WasmCrossCompiler
implements CrossCompiler {

    private Module module;
    private IdentifierMapping functionMappings = new IdentifierMapping();
    private IdentifierMapping globalVarsMapping = new IdentifierMapping();

    @Override
    public void compile(final StatementList statements, final String baseName) {
        if (module != null) {
            throw new RuntimeException("Method is not reentrant. Need a new WasmCrossCompiler to call compile again.");
        }
        module= new Module(1);
        compileStatements(statements);
        new ModuleWriter().writeToFile(module, baseName + ".wasm");
    }

    private void compileStatements(final StatementList statements) {
        for (int q = 0; q < statements.getNumStatements(); q++) {
            compileStatement(statements.getStatement(q));
        }
    }

    private void compileStatement(final Statement statement) {
        if (statement instanceof AssignmentStatement) {
            compileAssignment((AssignmentStatement) statement);
        } else {
            throw new RuntimeException("Unhandled statement " + statement.getClass().getName());
        }
    }

    private void compileAssignment(final AssignmentStatement assignmentStatement) {
        compileExpression(assignmentStatement.getRightHandSide());
        compileAssignableExpression(assignmentStatement.getLeftHandSide(), assignmentStatement.isLocal());
        /* TODO: store */
    }

    private void compileAssignableExpression(final AssignableExpression assignableExpression, final boolean local) {
        if (assignableExpression instanceof VariableExpression) {
            compileVariableExpression((VariableExpression) assignableExpression, local);
        } else {
            throw new RuntimeException("Unhandled AssignableExpression " + assignableExpression.getClass().getName());
        }
    }

    private void compileVariableExpression(final VariableExpression variableExpression, final boolean local) {
        /* TODO: handle global/local */
        final int variableIndex = globalVarsMapping.getOrPut(variableExpression.getVariableName());
    }

    private void compileExpression(final Expression expression) {

    }

    public static void main(final String[] args) {
        final String program = "a = 1;";
        final StatementList statements = BasusParser.parse(program);
        new WasmCrossCompiler().compile(statements, "test");
    }

}
