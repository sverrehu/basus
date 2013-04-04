package no.shhsoft.basus.tools.format;

import no.shhsoft.basus.language.AbstractExpressionList;
import no.shhsoft.basus.language.AbstractExpressionListWithOperator;
import no.shhsoft.basus.language.AdditiveExpression;
import no.shhsoft.basus.language.AssignableExpression;
import no.shhsoft.basus.language.AssignmentStatement;
import no.shhsoft.basus.language.BreakpointStatement;
import no.shhsoft.basus.language.CallStatement;
import no.shhsoft.basus.language.CommentStatement;
import no.shhsoft.basus.language.ConditionalAndExpression;
import no.shhsoft.basus.language.ConditionalOrExpression;
import no.shhsoft.basus.language.ConstantExpression;
import no.shhsoft.basus.language.ExponentialExpression;
import no.shhsoft.basus.language.Expression;
import no.shhsoft.basus.language.ForStatement;
import no.shhsoft.basus.language.FunctionExpression;
import no.shhsoft.basus.language.FunctionStatement;
import no.shhsoft.basus.language.IfStatement;
import no.shhsoft.basus.language.IndexExpression;
import no.shhsoft.basus.language.MultiplicativeExpression;
import no.shhsoft.basus.language.OperatorType;
import no.shhsoft.basus.language.RelationalExpression;
import no.shhsoft.basus.language.RepeatStatement;
import no.shhsoft.basus.language.Reserved;
import no.shhsoft.basus.language.ReturnStatement;
import no.shhsoft.basus.language.Statement;
import no.shhsoft.basus.language.StatementList;
import no.shhsoft.basus.language.UnaryExpression;
import no.shhsoft.basus.language.VariableExpression;
import no.shhsoft.basus.language.WhileStatement;
import no.shhsoft.basus.language.parser.BasusParser;
import no.shhsoft.basus.value.ArrayValue;
import no.shhsoft.basus.value.BooleanValue;
import no.shhsoft.basus.value.IntegerValue;
import no.shhsoft.basus.value.NumericValue;
import no.shhsoft.basus.value.RealValue;
import no.shhsoft.basus.value.StringValue;
import no.shhsoft.basus.value.Value;
import no.shhsoft.utils.IoUtils;
import no.shhsoft.utils.StringUtils;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BasusFormatter {

    private static final int PRI_NONE = 20;
    private static final int PRI_LOGICAL_OR = 12;
    private static final int PRI_LOGICAL_AND = 11;
    private static final int PRI_RELATIONAL = 6;
    private static final int PRI_ADDITIVE = 4;
    private static final int PRI_MULTIPLICATIVE = 3;
    private static final int PRI_EXPONENTIAL = 2;
    private static final int PRI_UNARY = 1;

    private int level = 0;
    private StringBuilder sb;
    private final String indentString = "    ";
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
            sb.append(indentString);
        }
    }

    private void formatAbstractExpressionListWithOperator(final AbstractExpressionListWithOperator expression,
                                                          final int encapsulatingPriority,
                                                          final int myPriority) {
        final boolean addParens = expression.getNumExpressions() > 1 && needsParens(encapsulatingPriority, myPriority);
        if (addParens) {
            sb.append('(');
        }
        final int numExpressions = expression.getNumExpressions();
        for (int q = 0; q < numExpressions; q++) {
            formatExpression(expression.getExpression(q), myPriority);
            if (q < numExpressions - 1) {
                formatOperatorType(expression.getOperator(q));
            }
        }
        if (addParens) {
            sb.append(')');
        }
    }

    private void formatBooleanValue(final BooleanValue value) {
        if (value.getValue()) {
            sb.append("TRUE");
        } else {
            sb.append("FALSE");
        }
    }

    private void formatIntegerValue(final IntegerValue value) {
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

    private void formatNumericValue(final NumericValue value) {
        if (value instanceof IntegerValue) {
            formatIntegerValue((IntegerValue) value);
        } else if (value instanceof RealValue) {
            sb.append(((RealValue) value).getValue());
        } else {
            throw new RuntimeException("Unhandled NumericValue type " + value.getClass().getName());
        }
    }

    private void formatStringValue(final StringValue value) {
        sb.append('"');
        sb.append(StringUtils.escapeJavaLikeString(value.getValue(), false, false));
        sb.append('"');
    }

    private void formatValue(final Value value) {
        if (value instanceof ArrayValue) {
            throw new RuntimeException("I though this would only happen when the Evaluator was involved.");
        } else if (value instanceof BooleanValue) {
            formatBooleanValue((BooleanValue) value);
        } else if (value instanceof NumericValue) {
            formatNumericValue((NumericValue) value);
        } else if (value instanceof StringValue) {
            formatStringValue((StringValue) value);
        } else {
            throw new RuntimeException("Unhandled Value type " + value.getClass().getName());
        }
    }

    private void formatVariableExpression(final VariableExpression expression) {
        sb.append(expression.getVariableName());
    }

    private void formatIndexExpression(final IndexExpression expression) {
        final IndexExpression indexExpression = expression;
        formatExpression(indexExpression.getArray(), PRI_NONE);
        sb.append('[');
        for (int q = 0; q < indexExpression.getNumExpressions(); q++) {
            if (q > 0) {
                sb.append(", ");
            }
            formatExpression(indexExpression.getExpression(q), PRI_NONE);
        }
        sb.append(']');
    }

    private void formatFunctionExpression(final FunctionExpression expression) {
        sb.append(expression.getFunctionName());
        sb.append('(');
        for (int q = 0; q < expression.getNumExpressions(); q++) {
            if (q > 0) {
                sb.append(", ");
            }
            formatExpression(expression.getExpression(q), PRI_NONE);
        }
        sb.append(')');
    }

    private void formatConditionalExpression(final AbstractExpressionList expressionList, final String separator,
                                             final int encapsulatingPriority, final int myPriority) {
        final boolean addParens = expressionList.getNumExpressions() > 1 && needsParens(encapsulatingPriority, myPriority);
        if (addParens) {
            sb.append('(');
        }
        for (int q = 0; q < expressionList.getNumExpressions(); q++) {
            if (q > 0) {
                sb.append(separator);
            }
            formatExpression(expressionList.getExpression(q), myPriority);
        }
        if (addParens) {
            sb.append(')');
        }
    }

    private void formatConditionalOrExpression(final ConditionalOrExpression expression, final int encapsulatingPriority) {
        formatConditionalExpression(expression, " or ", encapsulatingPriority, PRI_LOGICAL_OR);
    }

    private void formatConditionalAndExpression(final ConditionalAndExpression expression, final int encapsulatingPriority) {
        formatConditionalExpression(expression, " and ", encapsulatingPriority, PRI_LOGICAL_AND);
    }

    private void formatRelationalExpression(final RelationalExpression expression,
                                            @SuppressWarnings("unused") final int encapsulatingPriority) {
        formatExpression(expression.getLeftHandSide(), PRI_RELATIONAL);
        formatOperatorType(expression.getOperator());
        formatExpression(expression.getRightHandSide(), PRI_RELATIONAL);
    }

    private void formatAdditiveExpression(final AdditiveExpression expression, final int encapsulatingPriority) {
        formatAbstractExpressionListWithOperator(expression, encapsulatingPriority, PRI_ADDITIVE);
    }

    private void formatOperatorType(final OperatorType operator) {
        switch (operator) {
            case AND:
                sb.append(" and ");
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
                sb.append(" ^ ");
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
                sb.append("not ");
                break;
            case NOT_EQUAL:
                sb.append(" != ");
                break;
            case OR:
                sb.append(" or ");
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

    private void formatMultiplicativeExpression(final MultiplicativeExpression expression, final int encapsulatingPriority) {
        formatAbstractExpressionListWithOperator(expression, encapsulatingPriority, PRI_MULTIPLICATIVE);
    }

    private void formatExponentialExpression(final ExponentialExpression expression, final int encapsulatingPriority) {
        formatAbstractExpressionListWithOperator(expression, encapsulatingPriority, PRI_EXPONENTIAL);
    }

    private void formatUnaryExpression(final UnaryExpression expression,
                                       @SuppressWarnings("unused") final int encapsulatingPriority) {
        if (expression.isNegate()) {
            switch (expression.getOperatorType()) {
                case MINUS:
                    sb.append('-');
                    break;
                case NOT:
                    sb.append("not ");
                    break;
                default:
                    throw new RuntimeException("Unhandled negate operator: " + expression.getOperatorType().toString());
            }
        }
        formatExpression(expression.getExpression(), PRI_UNARY);
    }

    private void formatConstantExpression(final ConstantExpression expression) {
        final Value constant = expression.getConstant();
        formatValue(constant);
    }

    private void formatExpression(final Expression expression, final int encapsulatingPriority) {
        if (expression instanceof VariableExpression) {
            formatVariableExpression((VariableExpression) expression);
        } else if (expression instanceof IndexExpression) {
            formatIndexExpression((IndexExpression) expression);
        } else if (expression instanceof FunctionExpression) {
            formatFunctionExpression((FunctionExpression) expression);
        } else if (expression instanceof ConditionalOrExpression) {
            formatConditionalOrExpression((ConditionalOrExpression) expression, encapsulatingPriority);
        } else if (expression instanceof ConditionalAndExpression) {
            formatConditionalAndExpression((ConditionalAndExpression) expression, encapsulatingPriority);
        } else if (expression instanceof RelationalExpression) {
            formatRelationalExpression((RelationalExpression) expression, encapsulatingPriority);
        } else if (expression instanceof AdditiveExpression) {
            formatAdditiveExpression((AdditiveExpression) expression, encapsulatingPriority);
        } else if (expression instanceof MultiplicativeExpression) {
            formatMultiplicativeExpression((MultiplicativeExpression) expression, encapsulatingPriority);
        } else if (expression instanceof ExponentialExpression) {
            formatExponentialExpression((ExponentialExpression) expression, encapsulatingPriority);
        } else if (expression instanceof UnaryExpression) {
            formatUnaryExpression((UnaryExpression) expression, encapsulatingPriority);
        } else if (expression instanceof ConstantExpression) {
            formatConstantExpression((ConstantExpression) expression);
        } else {
            throw new RuntimeException("Unhandled Expression type " + expression.getClass().getName());
        }
    }

    private void formatCallStatement(final CallStatement statement) {
        formatFunctionExpression(statement.getFunctionExpression());
    }

    private void formatAssignableExpression(final AssignableExpression expression, final boolean local) {
        if (local) {
            sb.append(Reserved.LOCAL.toString());
            sb.append(' ');
        }
        if (expression instanceof VariableExpression) {
            formatVariableExpression((VariableExpression) expression);
        } else if (expression instanceof IndexExpression) {
            formatIndexExpression((IndexExpression) expression);
        } else {
            throw new RuntimeException("Something is forgotten in the state of Denmark");
        }
    }

    private void formatAssignmentStatement(final AssignmentStatement statement) {
        final AssignableExpression leftHandSide = statement.getLeftHandSide();
        formatAssignableExpression(leftHandSide, statement.isLocal());
        sb.append(" = ");
        formatExpression(statement.getRightHandSide(), PRI_NONE);
    }

    private void formatForStatement(final ForStatement statement) {
        sb.append("for ");
        formatAssignableExpression(statement.getAssignable(), false);
        sb.append(" = ");
        formatExpression(statement.getFrom(), PRI_NONE);
        sb.append(" to ");
        formatExpression(statement.getTo(), PRI_NONE);
        if (statement.getStep() != null) {
            sb.append(" step ");
            formatExpression(statement.getStep(), PRI_NONE);
        }
        sb.append(" do\n");
        incLevel();
        formatStatementList(statement.getStatements());
        decLevel();
        formatIndent();
        sb.append("done");
    }

    private void formatRepeatStatement(final RepeatStatement statement) {
        sb.append("repeat ");
        formatExpression(statement.getTimes(), PRI_NONE);
        sb.append(" times\n");
        incLevel();
        formatStatementList(statement.getStatements());
        decLevel();
        formatIndent();
        sb.append("done");
    }

    private void formatWhileStatement(final WhileStatement statement) {
        sb.append("while ");
        formatExpression(statement.getCondition(), PRI_NONE);
        sb.append(" do\n");
        incLevel();
        formatStatementList(statement.getStatements());
        decLevel();
        formatIndent();
        sb.append("done");
    }

    private void formatIfStatement(final IfStatement statement) {
        int q = 0;
        for (final Expression condition : statement.getConditions()) {
            if (q == 0) {
                sb.append("if ");
            } else {
                formatIndent();
                sb.append("elseif ");
            }
            formatExpression(condition, PRI_NONE);
            sb.append(" then\n");
            incLevel();
            formatStatementList(statement.getConditionStatements().get(q));
            decLevel();
            ++q;
        }
        if (statement.getElseStatements() != null) {
            formatIndent();
            sb.append("else\n");
            incLevel();
            formatStatementList(statement.getElseStatements());
            decLevel();
        }
        formatIndent();
        sb.append("endif");
    }

    private void formatReturnStatement(final ReturnStatement statement) {
        sb.append("return");
        if (statement.getExpression() != null) {
            sb.append(' ');
            formatExpression(statement.getExpression(), PRI_NONE);
        }
    }

    private void formatFunctionStatement(final FunctionStatement statement) {
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
        formatStatementList(statement.getStatements());
        decLevel();
        formatIndent();
        sb.append("endfunc");
    }

    private void formatCommentStatement(final CommentStatement statement) {
        /* TODO: format properly. */
        sb.append(statement.getComment());
    }

    private void formatStatement(final Statement statement) {
        if (needBlankLine) {
            sb.append('\n');
            needBlankLine = false;
        }
        boolean semicolonNeeded = true;
        formatIndent();
        if (statement instanceof CallStatement) {
            formatCallStatement((CallStatement) statement);
        } else if (statement instanceof AssignmentStatement) {
            formatAssignmentStatement((AssignmentStatement) statement);
        } else if (statement instanceof ForStatement) {
            formatForStatement((ForStatement) statement);
        } else if (statement instanceof RepeatStatement) {
            formatRepeatStatement((RepeatStatement) statement);
        } else if (statement instanceof WhileStatement) {
            formatWhileStatement((WhileStatement) statement);
        } else if (statement instanceof IfStatement) {
            formatIfStatement((IfStatement) statement);
        } else if (statement instanceof ReturnStatement) {
            formatReturnStatement((ReturnStatement) statement);
        } else if (statement instanceof FunctionStatement) {
            if (sb.length() > 1 && sb.charAt(sb.length() - 1) == '\n' && sb.charAt(sb.length() - 2) != '\n') {
                sb.append('\n');
            }
            formatFunctionStatement((FunctionStatement) statement);
            needBlankLine = true;
        } else if (statement instanceof CommentStatement) {
            formatCommentStatement((CommentStatement) statement);
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

    private void formatStatementList(final StatementList statementList) {
        for (int q = 0; q < statementList.getNumStatements(); q++) {
            formatStatement(statementList.getStatement(q));
        }
    }

    public synchronized String format(final String code) {
        level = 0;
        needBlankLine = false;
        sb = new StringBuilder();
        final StatementList statementList = BasusParser.parse(code, true);
        formatStatementList(statementList);
        final String formattedCode = sb.toString();
        /* Parse it on order to have an exception if the formatter generates invalid code. */
        BasusParser.parse(formattedCode);
        return formattedCode;
    }

    public static void main(final String[] args) {
        final String code = new String(IoUtils.readFile(System.getProperty("user.home") + "/basus/formatter-test.bus"));
        final String formatted = new BasusFormatter().format(code);
        System.out.println(formatted);
    }

}
