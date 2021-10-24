package no.shhsoft.basus.crosscompilers.javascript;

import no.shhsoft.basus.crosscompilers.CrossCompiler;
import no.shhsoft.basus.language.*;
import no.shhsoft.basus.language.eval.BuiltinFunctions;
import no.shhsoft.basus.language.parser.BasusParser;
import no.shhsoft.basus.value.*;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.StringUtils;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class JavaScriptCrossCompiler
implements CrossCompiler {

    private static final int PRI_NONE = 20;
    private static final int PRI_LOGICAL_OR = 12;
    private static final int PRI_LOGICAL_AND = 11;
    private static final int PRI_RELATIONAL = 6;
    private static final int PRI_ADDITIVE = 4;
    private static final int PRI_MULTIPLICATIVE = 3;
    private static final int PRI_EXPONENTIAL = 2;
    private static final int PRI_UNARY = 1;

    private static final String VARIABLE_ASSIGNMENT_KEYWORD = "var";
    private static final String INDENT_STRING = "    ";
    private static final String BUILTIN_FUNCTION_PREFIX = "__basus";
    private int level = 0;
    private StringBuilder sb;
    private boolean needBlankLine;

    private static void ignore() {
    }

    private static boolean needsParens(final int encapsulatingPriority, final int myPriority) {
        /* Include equality to have parens around eg. "1 / (2 / 3.0)", which forces
         * type conversion at the right point. */
        return myPriority >= encapsulatingPriority;
    }

    private void incLevel() {
        ++level;
    }

    private void decLevel() {
        --level;
    }

    private void formatIndent() {
        for (int q = level - 1; q >= 0; q--) {
            sb.append(INDENT_STRING);
        }
    }

    private void translateAbstractExpressionListWithOperator(final AbstractExpressionListWithOperator expression,
                                                             final int encapsulatingPriority, final int myPriority) {
        final boolean addParens = expression.getNumExpressions() > 1 && needsParens(encapsulatingPriority, myPriority);
        if (addParens) {
            sb.append('(');
        }
        final int numExpressions = expression.getNumExpressions();
        for (int q = 0; q < numExpressions; q++) {
            translateExpression(expression.getExpression(q), myPriority);
            if (q < numExpressions - 1) {
                translateOperatorType(expression.getOperator(q));
            }
        }
        if (addParens) {
            sb.append(')');
        }
    }

    private void translateBooleanValue(final BooleanValue value) {
        if (value.getValue()) {
            sb.append("true");
        } else {
            sb.append("false");
        }
    }

    private void translateIntegerValue(final IntegerValue value) {
        if (value.isFromCharacterConstant()) {
            sb.append('\'');
            switch (value.getValue()) {
                case '\r':
                    sb.append("\\r");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\'':
                    sb.append("\\'");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append((char) value.getValue());
            }
            sb.append('\'');
        } else {
            sb.append(value.getValue());
        }
    }

    private void translateNumericValue(final NumericValue value) {
        if (value instanceof IntegerValue) {
            translateIntegerValue((IntegerValue) value);
        } else if (value instanceof RealValue) {
            sb.append(((RealValue) value).getValue());
        } else {
            throw new RuntimeException("Unhandled NumericValue type " + value.getClass().getName());
        }
    }

    private void translateStringValue(final StringValue value) {
        sb.append('"');
        sb.append(StringUtils.escapeJavaLikeString(value.getValue(), true, true));
        sb.append('"');
    }

    private void translateValue(final Value value) {
        if (value instanceof ArrayValue) {
            throw new RuntimeException("I though this would only happen when the Evaluator was involved.");
        } else if (value instanceof BooleanValue) {
            translateBooleanValue((BooleanValue) value);
        } else if (value instanceof NumericValue) {
            translateNumericValue((NumericValue) value);
        } else if (value instanceof StringValue) {
            translateStringValue((StringValue) value);
        } else {
            throw new RuntimeException("Unhandled Value type " + value.getClass().getName());
        }
    }

    private void translateVariableExpression(final VariableExpression expression) {
        sb.append(expression.getVariableName());
    }

    private void translateIndexExpression(final IndexExpression expression) {
        translateExpression(expression.getArray(), PRI_NONE);
        sb.append('[');
        for (int q = 0; q < expression.getNumExpressions(); q++) {
            if (q > 0) {
                sb.append(", ");
            }
            translateExpression(expression.getExpression(q), PRI_NONE);
        }
        sb.append(']');
    }

    private void translateFunctionExpression(final FunctionExpression expression) {
        if (BuiltinFunctions.isBuiltin(expression.getFunctionName())) {
            sb.append(BUILTIN_FUNCTION_PREFIX + ".");
        }
        sb.append(expression.getFunctionName());
        sb.append('(');
        for (int q = 0; q < expression.getNumExpressions(); q++) {
            if (q > 0) {
                sb.append(", ");
            }
            translateExpression(expression.getExpression(q), PRI_NONE);
        }
        sb.append(')');
    }

    private void translateConditionalExpression(final AbstractExpressionList expressionList, final String separator,
                                                final int encapsulatingPriority, final int myPriority) {
        final boolean addParens = expressionList.getNumExpressions() > 1 && needsParens(encapsulatingPriority, myPriority);
        if (addParens) {
            sb.append('(');
        }
        for (int q = 0; q < expressionList.getNumExpressions(); q++) {
            if (q > 0) {
                sb.append(separator);
            }
            translateExpression(expressionList.getExpression(q), myPriority);
        }
        if (addParens) {
            sb.append(')');
        }
    }

    private void translateConditionalOrExpression(final ConditionalOrExpression expression, final int encapsulatingPriority) {
        translateConditionalExpression(expression, " || ", encapsulatingPriority, PRI_LOGICAL_OR);
    }

    private void translateConditionalAndExpression(final ConditionalAndExpression expression, final int encapsulatingPriority) {
        translateConditionalExpression(expression, " && ", encapsulatingPriority, PRI_LOGICAL_AND);
    }

    private void translateRelationalExpression(final RelationalExpression expression, @SuppressWarnings("unused") final int encapsulatingPriority) {
        translateExpression(expression.getLeftHandSide(), PRI_RELATIONAL);
        translateOperatorType(expression.getOperator());
        translateExpression(expression.getRightHandSide(), PRI_RELATIONAL);
    }

    private void formatAdditiveExpression(final AdditiveExpression expression, final int encapsulatingPriority) {
        translateAbstractExpressionListWithOperator(expression, encapsulatingPriority, PRI_ADDITIVE);
    }

    private void translateOperatorType(final OperatorType operator) {
        switch (operator) {
            case AND:
                sb.append(" && ");
                break;
            case ASSIGN:
                sb.append(" = ");
                break;
            case COMMA:
                sb.append(", ");
                break;
            case DIVIDE:
                sb.append(" / ");
                break;
            case EQUAL:
                sb.append(" == ");
                break;
            case EXPONENTIATE:
                sb.append(" ** ");
                break;
            case GREATER:
                sb.append(" > ");
                break;
            case GREATER_OR_EQUAL:
                sb.append(" >= ");
                break;
            case LEFT_BRACKET:
                sb.append("[");
                break;
            case LEFT_PAREN:
                sb.append("(");
                break;
            case LESS:
                sb.append(" < ");
                break;
            case LESS_OR_EQUAL:
                sb.append(" <= ");
                break;
            case MINUS:
                sb.append(" - ");
                break;
            case MODULUS:
                sb.append(" % ");
                break;
            case MULTIPLY:
                sb.append(" * ");
                break;
            case NOT:
                sb.append("!");
                break;
            case NOT_EQUAL:
                sb.append(" != ");
                break;
            case OR:
                sb.append(" || ");
                break;
            case PLUS:
                sb.append(" + ");
                break;
            case RIGHT_BRACKET:
                sb.append("]");
                break;
            case RIGHT_PAREN:
                sb.append(")");
                break;
            case SEMICOLON:
                sb.append(";");
                break;
            default:
                throw new RuntimeException("Unhandled OperatorType " + operator.toString());
        }
    }

    private void translateMultiplicativeExpression(final MultiplicativeExpression expression, final int encapsulatingPriority) {
        translateAbstractExpressionListWithOperator(expression, encapsulatingPriority, PRI_MULTIPLICATIVE);
    }

    private void translateExponentialExpression(final ExponentialExpression expression, final int encapsulatingPriority) {
        translateAbstractExpressionListWithOperator(expression, encapsulatingPriority, PRI_EXPONENTIAL);
    }

    private void translateUnaryExpression(final UnaryExpression expression, @SuppressWarnings("unused") final int encapsulatingPriority) {
        if (expression.isNegate()) {
            switch (expression.getOperatorType()) {
                case MINUS:
                    sb.append('-');
                    break;
                case NOT:
                    sb.append("!");
                    break;
                default:
                    throw new RuntimeException("Unhandled negate operator: " + expression.getOperatorType().toString());
            }
        }
        translateExpression(expression.getExpression(), PRI_UNARY);
    }

    private void translateConstantExpression(final ConstantExpression expression) {
        final Value constant = expression.getConstant();
        translateValue(constant);
    }

    private void translateExpression(final Expression expression, final int encapsulatingPriority) {
        if (expression instanceof VariableExpression) {
            translateVariableExpression((VariableExpression) expression);
        } else if (expression instanceof IndexExpression) {
            translateIndexExpression((IndexExpression) expression);
        } else if (expression instanceof FunctionExpression) {
            translateFunctionExpression((FunctionExpression) expression);
        } else if (expression instanceof ConditionalOrExpression) {
            translateConditionalOrExpression((ConditionalOrExpression) expression, encapsulatingPriority);
        } else if (expression instanceof ConditionalAndExpression) {
            translateConditionalAndExpression((ConditionalAndExpression) expression, encapsulatingPriority);
        } else if (expression instanceof RelationalExpression) {
            translateRelationalExpression((RelationalExpression) expression, encapsulatingPriority);
        } else if (expression instanceof AdditiveExpression) {
            formatAdditiveExpression((AdditiveExpression) expression, encapsulatingPriority);
        } else if (expression instanceof MultiplicativeExpression) {
            translateMultiplicativeExpression((MultiplicativeExpression) expression, encapsulatingPriority);
        } else if (expression instanceof ExponentialExpression) {
            translateExponentialExpression((ExponentialExpression) expression, encapsulatingPriority);
        } else if (expression instanceof UnaryExpression) {
            translateUnaryExpression((UnaryExpression) expression, encapsulatingPriority);
        } else if (expression instanceof ConstantExpression) {
            translateConstantExpression((ConstantExpression) expression);
        } else {
            throw new RuntimeException("Unhandled Expression type " + expression.getClass().getName());
        }
    }

    private void translateCallStatement(final CallStatement statement) {
        translateFunctionExpression(statement.getFunctionExpression());
    }

    private void translateAssignableExpression(final AssignableExpression expression, final boolean local) {
        if (local) {
            sb.append(VARIABLE_ASSIGNMENT_KEYWORD + " ");
        }
        if (expression instanceof VariableExpression) {
            translateVariableExpression((VariableExpression) expression);
        } else if (expression instanceof IndexExpression) {
            translateIndexExpression((IndexExpression) expression);
        } else {
            throw new RuntimeException("Something is forgotten in the state of Denmark");
        }
    }

    private void translateAssignmentStatement(final AssignmentStatement statement) {
        final AssignableExpression leftHandSide = statement.getLeftHandSide();
        translateAssignableExpression(leftHandSide, true);
        sb.append(" = ");
        translateExpression(statement.getRightHandSide(), PRI_NONE);
    }

    private void translateForStatement(final ForStatement statement) {
        sb.append("for (");
        translateAssignableExpression(statement.getAssignable(), true);
        sb.append(" = ");
        translateExpression(statement.getFrom(), PRI_NONE);
        sb.append("; ");
        translateAssignableExpression(statement.getAssignable(), false);
        sb.append(" <= ");
        translateExpression(statement.getTo(), PRI_NONE);
        sb.append("; ");
        translateAssignableExpression(statement.getAssignable(), false);
        if (statement.getStep() != null) {
            sb.append(" += ");
            translateExpression(statement.getStep(), PRI_NONE);
        } else {
            sb.append("++");
        }
        sb.append(") {\n");
        incLevel();
        translateStatementList(statement.getStatements());
        decLevel();
        formatIndent();
        sb.append("}");
    }

    private void translateRepeatStatement(final RepeatStatement statement) {
        sb.append("repeat ");
        translateExpression(statement.getTimes(), PRI_NONE);
        sb.append(" times\n");
        incLevel();
        translateStatementList(statement.getStatements());
        decLevel();
        formatIndent();
        sb.append("done");
    }

    private void translateWhileStatement(final WhileStatement statement) {
        sb.append("while (");
        translateExpression(statement.getCondition(), PRI_NONE);
        sb.append(") {\n");
        incLevel();
        translateStatementList(statement.getStatements());
        decLevel();
        formatIndent();
        sb.append("}");
    }

    private void translateIfStatement(final IfStatement statement) {
        int q = 0;
        for (final Expression condition : statement.getConditions()) {
            if (q == 0) {
                sb.append("if (");
            } else {
                formatIndent();
                sb.append("} else if (");
            }
            translateExpression(condition, PRI_NONE);
            sb.append(") {\n");
            incLevel();
            translateStatementList(statement.getConditionStatements().get(q));
            decLevel();
            ++q;
        }
        if (statement.getElseStatements() != null) {
            formatIndent();
            sb.append("} else {\n");
            incLevel();
            translateStatementList(statement.getElseStatements());
            decLevel();
        }
        formatIndent();
        sb.append("}");
    }

    private void translateReturnStatement(final ReturnStatement statement) {
        sb.append("return");
        if (statement.getExpression() != null) {
            sb.append(' ');
            translateExpression(statement.getExpression(), PRI_NONE);
        }
    }

    private void translateFunctionStatement(final FunctionStatement statement) {
        sb.append("function ");
        sb.append(statement.getName());
        sb.append('(');
        int q = 0;
        for (final String argumentName : statement.getArgumentNames()) {
            if (q++ > 0) {
                sb.append(", ");
            }
            sb.append(argumentName);
        }
        sb.append(")\n");
        incLevel();
        translateStatementList(statement.getStatements());
        decLevel();
        formatIndent();
        sb.append("endfunc");
    }

    private void translateCommentStatement(final CommentStatement statement) {
        /* TODO: format properly. */
        sb.append(statement.getComment());
    }

    private void translateStatement(final Statement statement) {
        if (needBlankLine) {
            sb.append('\n');
            needBlankLine = false;
        }
        boolean semicolonNeeded = true;
        formatIndent();
        if (statement instanceof CallStatement) {
            translateCallStatement((CallStatement) statement);
        } else if (statement instanceof AssignmentStatement) {
            translateAssignmentStatement((AssignmentStatement) statement);
        } else if (statement instanceof ForStatement) {
            translateForStatement((ForStatement) statement);
            semicolonNeeded = false;
        } else if (statement instanceof RepeatStatement) {
            translateRepeatStatement((RepeatStatement) statement);
            semicolonNeeded = false;
        } else if (statement instanceof WhileStatement) {
            translateWhileStatement((WhileStatement) statement);
            semicolonNeeded = false;
        } else if (statement instanceof IfStatement) {
            translateIfStatement((IfStatement) statement);
            semicolonNeeded = false;
        } else if (statement instanceof ReturnStatement) {
            translateReturnStatement((ReturnStatement) statement);
        } else if (statement instanceof FunctionStatement) {
            if (sb.length() > 1 && sb.charAt(sb.length() - 1) == '\n' && sb.charAt(sb.length() - 2) != '\n') {
                sb.append('\n');
            }
            translateFunctionStatement((FunctionStatement) statement);
            needBlankLine = true;
        } else if (statement instanceof CommentStatement) {
            translateCommentStatement((CommentStatement) statement);
            semicolonNeeded = false;
        } else if (statement instanceof BreakpointStatement) {
            ignore();
        } else {
            throw new RuntimeException("Unhandled Statement type " + statement.getClass().getName());
        }
        if (semicolonNeeded) {
            sb.append(';');
        }
        sb.append('\n');
    }

    private void translateStatementList(final StatementList statementList) {
        for (int q = 0; q < statementList.getNumStatements(); q++) {
            translateStatement(statementList.getStatement(q));
        }
    }

    @Override
    public String compile(final StatementList statementList) {
        sb = new StringBuilder();
        sb.append("shh.addOnload(function() {\n    \"use strict\";\n\n");
        level = 1;
        needBlankLine = false;
        translateStatementList(statementList);
        sb.append("\n});\n");
        return sb.toString();
    }

    public synchronized String compile(final String code) {
        return compile(BasusParser.parse(code, true));
    }

    private static void checkJavaScript(final String s) {
        final Parser parser = new Parser(new CompilerEnvirons(), new ErrorReporter() {
            @Override
            public void warning(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
                System.err.println("Line " + line + ": " + message);
            }

            @Override
            public void error(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
                System.err.println("Line " + line + ": " + message);
            }

            @Override
            public EvaluatorException runtimeError(final String message, final String sourceName, final int line, final String lineSource, final int lineOffset) {
                return new EvaluatorException(message);
            }
        });
        if (parser.parse(s, "", 0) == null) {
            throw new RuntimeException("JavaScript parsing failed.");
        }
    }

    public static void main(final String[] args) {
        //final String code = new String(IoUtils.readFile(System.getProperty("user.home") + "/basus/spaceWar.bus"));
        final String code = "for n = 0 to 10 do\n" +
                            "    println(random());\n" +
                            "done;\n";
        final String js = new JavaScriptCrossCompiler().compile(code);
        System.out.println(js);
        checkJavaScript(js);
    }

}
