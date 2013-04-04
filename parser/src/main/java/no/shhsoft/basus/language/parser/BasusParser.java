package no.shhsoft.basus.language.parser;

import java.util.ArrayList;
import java.util.List;

import no.shhsoft.basus.language.AbstractExpressionList;
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
import no.shhsoft.basus.utils.ErrorUtils;
import no.shhsoft.basus.utils.TextLocation;
import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.BooleanValue;
import no.shhsoft.basus.value.RealValue;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BasusParser {

    private final Tokenizer tokenizer;
    private Token token;
    private Token prevToken;
    private int blockLevel;

    private void error(final String key, final Object... args) {
        TextLocation startLocation = null;
        if (token != null) {
            startLocation = token.getStartLocation();
        }
        throw new ParserException(ErrorUtils.getMessage(key, startLocation, args), startLocation);
    }

    private void addTextLocations(final TextLocationHolder holder, final TextLocation start) {
        holder.setStartLocation(start);
        if (prevToken != null) {
            holder.setEndLocation(prevToken.getEndLocation());
        }
    }

    private BasusParser(final Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    private boolean eof() {
        return token == null;
    }

    private void next() {
        prevToken = token;
        token = tokenizer.nextToken();
    }

    private void nextCheckEof() {
        next();
        if (eof()) {
            error("err.unexpectedEndOfInput");
        }
    }

    private void pushBack() {
        tokenizer.pushBack(token);
    }

    private boolean isOperatorMatch(final OperatorType ot) {
        if (token == null) {
            return false;
        }
        if (token.getType() != TokenType.OPERATOR) {
            return false;
        }
        return token.getOperator() == ot;
    }

    private boolean isRelationalOperator() {
        if (token == null) {
            return false;
        }
        if (token.getType() != TokenType.OPERATOR) {
            return false;
        }
        return OperatorType.isRelational(token.getOperator());
    }

    private boolean isReservedMatch(final Reserved reserved) {
        if (token == null) {
            return false;
        }
        if (token.getType() != TokenType.RESERVED) {
            return false;
        }
        return token.getReserved() == reserved;
    }

    private boolean isReservedMatch(final Reserved[] reserveds) {
        if (reserveds == null) {
            return false;
        }
        for (final Reserved reserved : reserveds) {
            if (isReservedMatch(reserved)) {
                return true;
            }
        }
        return false;
    }

    private Expression simplifyAndAddTextLocations(final AbstractExpressionList expr,
                                                   final TextLocation startLocation) {
        if (expr.getNumExpressions() == 1) {
            return expr.getExpression(0);
        }
        addTextLocations(expr, startLocation);
        return expr;
    }

    private FunctionExpression parseFunctionExpression(final String name) {
        final TextLocation startLocation = token.getStartLocation();
        final List<Expression> args = new ArrayList<Expression>();
        while (!isOperatorMatch(OperatorType.RIGHT_PAREN)) {
            args.add(parseExpression());
            if (isOperatorMatch(OperatorType.COMMA)) {
                nextCheckEof();
                if (isOperatorMatch(OperatorType.RIGHT_PAREN)) {
                    error("err.syntax");
                }
            }
        }
        next();
        final FunctionExpression expression = new FunctionExpression(name, args);
        addTextLocations(expression, startLocation);
        return expression;
    }

    private Expression parseVariableExpression(final String name) {
        return new VariableExpression(name);
    }

    private Expression parseFunctionOrVariableExpression() {
        Expression expression;
        final String identifier = token.getIdentifer();
        next();
        if (isOperatorMatch(OperatorType.LEFT_PAREN)) {
            nextCheckEof();
            expression = parseFunctionExpression(identifier);
        } else {
            expression = parseVariableExpression(identifier);
        }
        return expression;
    }

    private Expression parsePrimaryExpression() {
        final TextLocation startLocation = token.getStartLocation();
        Expression expression = null;
        if (token.getType() == TokenType.CONSTANT) {
            expression = new ConstantExpression(token.getConstant());
            next();
        } else if (token.getType() == TokenType.RESERVED) {
            final Reserved reserved = token.getReserved();
            switch (reserved) {
                case TRUE:
                    expression = new ConstantExpression(BooleanValue.TRUE);
                    break;
                case FALSE:
                    expression = new ConstantExpression(BooleanValue.FALSE);
                    break;
                case PI:
                    expression = new ConstantExpression(new RealValue(Math.PI));
                    break;
                default:
            }
            if (expression != null) {
                next();
            }
        }
        if (expression == null && token.getType() == TokenType.IDENTIFIER) {
            expression = parseFunctionOrVariableExpression();
        }
        if (expression == null && isOperatorMatch(OperatorType.LEFT_PAREN)) {
            nextCheckEof();
            expression = parseExpression();
            if (!isOperatorMatch(OperatorType.RIGHT_PAREN)) {
                error("err.unmatchedParen");
            }
            next();
        }
        if (expression == null) {
            error("err.unexpectedOperator", token.getOperator().toString());
        }
        addTextLocations(expression, startLocation);
        return expression;
    }

    private Expression parsePostfixExpression() {
        final TextLocation startLocation = token.getStartLocation();
        final Expression preExpression = parsePrimaryExpression();
        final List<Expression> indexExpressions = new ArrayList<Expression>();
        if (isOperatorMatch(OperatorType.LEFT_BRACKET)) {
            nextCheckEof();
            for (;;) {
                indexExpressions.add(parseExpression());
                if (!isOperatorMatch(OperatorType.COMMA)) {
                    break;
                }
                nextCheckEof();
            }
            if (!isOperatorMatch(OperatorType.RIGHT_BRACKET)) {
                error("err.missingIndexRightParen");
            }
            next();
        }
        if (indexExpressions.size() == 0) {
            return preExpression;
        }
        final IndexExpression expression = new IndexExpression(preExpression, indexExpressions);
        addTextLocations(expression, startLocation);
        return expression;
    }

    private Expression parseUnaryExpression() {
        final TextLocation startLocation = token.getStartLocation();
        boolean negate = false;
        OperatorType operatorType = null;
        if (isOperatorMatch(OperatorType.MINUS)) {
            operatorType = OperatorType.MINUS;
            negate = true;
            nextCheckEof();
        } else if (isOperatorMatch(OperatorType.NOT)) {
            operatorType = OperatorType.NOT;
            negate = true;
            nextCheckEof();
        } else if (isOperatorMatch(OperatorType.PLUS)) {
            nextCheckEof();
        }
        Expression expression = parsePostfixExpression();
        if (negate) {
            expression = new UnaryExpression(expression, negate, operatorType);
            addTextLocations(expression, startLocation);
        }
        return expression;
    }

    private Expression parseExponentialExpression() {
        final TextLocation startLocation = token.getStartLocation();
        final List<Expression> expressions = new ArrayList<Expression>();
        final List<OperatorType> operators = new ArrayList<OperatorType>();
        expressions.add(parseUnaryExpression());
        while (isOperatorMatch(OperatorType.EXPONENTIATE)) {
            operators.add(token.getOperator());
            nextCheckEof();
            expressions.add(parseUnaryExpression());
        }
        return simplifyAndAddTextLocations(new ExponentialExpression(expressions, operators),
                                           startLocation);
    }

    private Expression parseMultiplicativeExpression() {
        final TextLocation startLocation = token.getStartLocation();
        final List<Expression> expressions = new ArrayList<Expression>();
        final List<OperatorType> operators = new ArrayList<OperatorType>();
        expressions.add(parseExponentialExpression());
        while (isOperatorMatch(OperatorType.MULTIPLY) || isOperatorMatch(OperatorType.DIVIDE)
               || isOperatorMatch(OperatorType.MODULUS)) {
            operators.add(token.getOperator());
            nextCheckEof();
            expressions.add(parseExponentialExpression());
        }
        return simplifyAndAddTextLocations(new MultiplicativeExpression(expressions, operators),
                                           startLocation);
    }

    private Expression parseAdditiveExpression() {
        final TextLocation startLocation = token.getStartLocation();
        final List<Expression> expressions = new ArrayList<Expression>();
        final List<OperatorType> operators = new ArrayList<OperatorType>();
        expressions.add(parseMultiplicativeExpression());
        while (isOperatorMatch(OperatorType.PLUS) || isOperatorMatch(OperatorType.MINUS)) {
            operators.add(token.getOperator());
            nextCheckEof();
            expressions.add(parseMultiplicativeExpression());
        }
        return simplifyAndAddTextLocations(new AdditiveExpression(expressions, operators),
                                           startLocation);
    }

    private Expression parseRelationalExpression() {
        final TextLocation startLocation = token.getStartLocation();
        final Expression lhs = parseAdditiveExpression();
        if (!isRelationalOperator()) {
            return lhs;
        }
        final OperatorType operator = token.getOperator();
        nextCheckEof();
        final Expression rhs = parseAdditiveExpression();
        final RelationalExpression expression = new RelationalExpression(lhs, operator, rhs);
        addTextLocations(expression, startLocation);
        return expression;
    }

    private Expression parseConditionalAndExpression() {
        final TextLocation startLocation = token.getStartLocation();
        final List<Expression> expressions = new ArrayList<Expression>();
        expressions.add(parseRelationalExpression());
        while (isOperatorMatch(OperatorType.AND)) {
            nextCheckEof();
            expressions.add(parseRelationalExpression());
        }
        return simplifyAndAddTextLocations(new ConditionalAndExpression(expressions),
                                           startLocation);
    }

    private Expression parseConditionalOrExpression() {
        final TextLocation startLocation = token.getStartLocation();
        final List<Expression> expressions = new ArrayList<Expression>();
        expressions.add(parseConditionalAndExpression());
        while (isOperatorMatch(OperatorType.OR)) {
            nextCheckEof();
            expressions.add(parseConditionalAndExpression());
        }
        return simplifyAndAddTextLocations(new ConditionalOrExpression(expressions),
                                           startLocation);
    }

    private Expression parseExpression() {
        return parseConditionalOrExpression();
    }

    private Token lookahead() {
        final Token currToken = token;
        nextCheckEof();
        final Token nextToken = token;
        pushBack();
        token = currToken;
        return nextToken;
    }

    private static final Reserved[] IF_ENDS = new Reserved[] {
        Reserved.ENDIF, Reserved.ELSE, Reserved.ELSEIF
    };
    private static final Reserved[] ELSE_ENDS = new Reserved[] { Reserved.ENDIF };
    private IfStatement parseIfStatement() {
        ++blockLevel;
        nextCheckEof();
        final List<Expression> conditions = new ArrayList<Expression>();
        final List<StatementList> conditionStatements = new ArrayList<StatementList>();
        StatementList elseStatements = null;
        for (;;) {
            final Expression condition = parseExpression();
            if (!isReservedMatch(Reserved.THEN)) {
                error("err.missingStatementKeyword",
                      Reserved.THEN.toString(), Reserved.IF.toString());
            }
            nextCheckEof();
            final StatementList statements = parseStatementList(IF_ENDS);
            conditions.add(condition);
            conditionStatements.add(statements);
            if (isReservedMatch(Reserved.ENDIF) || isReservedMatch(Reserved.ELSE)) {
                break;
            }
            if (!isReservedMatch(Reserved.ELSEIF)) {
                error("err.missingStatementKeyword3",
                      Reserved.ENDIF.toString(), Reserved.ELSE.toString(),
                      Reserved.ELSEIF.toString(), Reserved.IF.toString());
            }
            nextCheckEof();
        }
        if (isReservedMatch(Reserved.ELSE)) {
            nextCheckEof();
            elseStatements = parseStatementList(ELSE_ENDS);
            if (!isReservedMatch(Reserved.ENDIF)) {
                error("err.missingStatementKeyword",
                      Reserved.ENDIF.toString(), Reserved.IF.toString());
            }
        }
        next();
        --blockLevel;
        return new IfStatement(conditions, conditionStatements, elseStatements);
    }

    private static final Reserved[] FOR_ENDS = new Reserved[] { Reserved.DONE };
    private ForStatement parseForStatement() {
        ++blockLevel;
        nextCheckEof();
        final AssignmentStatement assignment = parseAssignmentStatement();
        if (!isReservedMatch(Reserved.TO)) {
            error("err.missingStatementKeyword",
                  Reserved.TO.toString(), Reserved.FOR.toString());
        }
        nextCheckEof();
        final Expression to = parseExpression();
        Expression step = null;
        if (isReservedMatch(Reserved.STEP)) {
            nextCheckEof();
            step = parseExpression();
        }
        if (!isReservedMatch(Reserved.DO)) {
            error("err.missingStatementKeyword",
                  Reserved.DO.toString(), Reserved.FOR.toString());
        }
        nextCheckEof();
        final StatementList statements = parseStatementList(FOR_ENDS);
        if (!isReservedMatch(Reserved.DONE)) {
            error("err.missingStatementKeyword",
                  Reserved.DONE.toString(), Reserved.FOR.toString());
        }
        next();
        --blockLevel;
        return new ForStatement(assignment.getLeftHandSide(), assignment.getRightHandSide(),
                                to, step, statements);
    }

    private static final Reserved[] REPEAT_ENDS = new Reserved[] { Reserved.DONE };
    private RepeatStatement parseRepeatStatement() {
        ++blockLevel;
        nextCheckEof();
        final Expression times = parseExpression();
        if (!isReservedMatch(Reserved.TIMES)) {
            error("err.missingStatementKeyword",
                  Reserved.TIMES.toString(), Reserved.REPEAT.toString());
        }
        nextCheckEof();
        final StatementList statements = parseStatementList(REPEAT_ENDS);
        if (!isReservedMatch(Reserved.DONE)) {
            error("err.missingStatementKeyword",
                  Reserved.DONE.toString(), Reserved.REPEAT.toString());
        }
        next();
        --blockLevel;
        return new RepeatStatement(times, statements);
    }

    private static final Reserved[] WHILE_ENDS = new Reserved[] { Reserved.DONE };
    private WhileStatement parseWhileStatement() {
        ++blockLevel;
        nextCheckEof();
        final Expression condition = parseExpression();
        if (!isReservedMatch(Reserved.DO)) {
            error("err.missingStatementKeyword",
                  Reserved.DO.toString(), Reserved.WHILE.toString());
        }
        nextCheckEof();
        final StatementList statements = parseStatementList(WHILE_ENDS);
        if (!isReservedMatch(Reserved.DONE)) {
            error("err.missingStatementKeyword",
                  Reserved.DONE.toString(), Reserved.WHILE.toString());
        }
        next();
        --blockLevel;
        return new WhileStatement(condition, statements);
    }

    private static final Reserved[] FUNCTION_ENDS = new Reserved[] { Reserved.ENDFUNC };
    private FunctionStatement parseFunctionStatement() {
        if (blockLevel != 0) {
            error("err.functionDefinedInBlock");
        }
        ++blockLevel;
        nextCheckEof();
        if (token.getType() != TokenType.IDENTIFIER) {
            error("err.functionDefNotIdentifier");
        }
        final String name = token.getIdentifer();
        nextCheckEof();
        if (!isOperatorMatch(OperatorType.LEFT_PAREN)) {
            error("err.missingFuncDefLeftParen");
        }
        nextCheckEof();
        final List<String> argumentList = new ArrayList<String>();
        while (!isOperatorMatch(OperatorType.RIGHT_PAREN)) {
            if (token.getType() != TokenType.IDENTIFIER) {
                error("err.functionDefVarNotIdentifier", token.toString());
            }
            argumentList.add(token.getIdentifer());
            nextCheckEof();
            if (isOperatorMatch(OperatorType.COMMA)) {
                nextCheckEof();
                if (isOperatorMatch(OperatorType.RIGHT_PAREN)) {
                    error("err.syntax");
                }
            }
        }
        final String[] arguments = argumentList.toArray(new String[argumentList.size()]);
        nextCheckEof();
        final StatementList statements = parseStatementList(FUNCTION_ENDS);
        if (!isReservedMatch(Reserved.ENDFUNC)) {
            error("err.missingStatementKeyword",
                  Reserved.ENDFUNC.toString(), Reserved.FUNCTION.toString());
        }
        next();
        --blockLevel;
        return new FunctionStatement(name, arguments, statements);
    }

    private ReturnStatement parseReturnStatement() {
        nextCheckEof();
        final Expression expression = parseExpression();
        return new ReturnStatement(expression);
    }

    private AssignmentStatement parseAssignmentStatement() {
        boolean local = false;
        if (isReservedMatch(Reserved.LOCAL)) {
            local = true;
            nextCheckEof();
        }
        final Expression lhs = parseExpression();
        if (!(lhs instanceof VariableExpression || lhs instanceof IndexExpression)) {
            error("err.lhsNotAssignable");
        }
        if (!isOperatorMatch(OperatorType.ASSIGN)) {
            error("err.missingAssignmentOp");
        }
        nextCheckEof();
        final Expression rhs = parseExpression();
        return new AssignmentStatement(local, (AssignableExpression) lhs, rhs);
    }

    private CallStatement parseCallStatement() {
        if (token.getType() != TokenType.IDENTIFIER) {
            error("err.functionNotIdentifier");
        }
        final String name = token.getIdentifer();
        nextCheckEof();
        if (!isOperatorMatch(OperatorType.LEFT_PAREN)) {
            error("err.missingLeftParen");
        }
        nextCheckEof();
        final FunctionExpression functionExpression = parseFunctionExpression(name);
        return new CallStatement(functionExpression);
    }

    private Statement parseStatement() {
        Statement statement = null;
        final TextLocation startLocation = token.getStartLocation();
        if (token.getType() == TokenType.RESERVED) {
            final Reserved reserved = token.getReserved();
            switch (reserved) {
                case IF:
                    statement = parseIfStatement();
                    break;
                case FOR:
                    statement = parseForStatement();
                    break;
                case REPEAT:
                    statement = parseRepeatStatement();
                    break;
                case WHILE:
                    statement = parseWhileStatement();
                    break;
                case FUNCTION:
                    statement = parseFunctionStatement();
                    break;
                case RETURN:
                    statement = parseReturnStatement();
                    break;
                case LOCAL:
                    statement = parseAssignmentStatement();
                    break;
                case BREAKPOINT:
                    statement = new BreakpointStatement();
                    nextCheckEof();
                    break;
                default:
                    error("err.unexpectedReserved", reserved.toString());
            }
        } else if (token.getType() == TokenType.COMMENT) {
            statement = new CommentStatement(token.getComment());
            nextCheckEof();
        } else {
            final Token nextToken = lookahead();
            if (nextToken.getType() == TokenType.OPERATOR
                && nextToken.getOperator() == OperatorType.LEFT_PAREN) {
                statement = parseCallStatement();
            } else {
                statement = parseAssignmentStatement();
            }
        }
        addTextLocations(statement, startLocation);
        return statement;
    }

    private StatementList parseStatementList(final Reserved[] endOfBlockMarkers) {
        final StatementList statementList = new StatementList();
        for (;;) {
            if (eof() || isReservedMatch(endOfBlockMarkers)) {
                break;
            }
            final Statement statement = parseStatement();
            statementList.addStatement(statement);
            /* whee!  there's actually a bug here: I wanted to _require_
             * semicolons between statements, but I forgot the assertion.
             * any semicolons will just be skipped.  Kjetil Valstadsve
             * discovered it, and insists I keep the bug in place.  he
             * doesn't like semicolons. :-)
             */
            while (isOperatorMatch(OperatorType.SEMICOLON)) {
                next();
            }
        }
        return statementList;
    }

    private StatementList parseAll() {
        nextCheckEof();
        final StatementList statementList = parseStatementList(null);
        if (!eof()) {
            error("err.unexpectedText");
        }
        return statementList;
    }

    public static StatementList parse(final String string) {
        return new BasusParser(new BasusTokenizer(string)).parseAll();
    }

    public static StatementList parse(final String string, final boolean commentsAreTokens) {
        return new BasusParser(new BasusTokenizer(string, commentsAreTokens)).parseAll();
    }

}
