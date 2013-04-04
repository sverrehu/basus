package no.shhsoft.basus.language.parser;

import no.shhsoft.basus.language.AbstractTextLocationHolder;
import no.shhsoft.basus.language.OperatorType;
import no.shhsoft.basus.language.Reserved;
import no.shhsoft.basus.utils.TextLocation;
import no.shhsoft.basus.value.Value;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class Token
extends AbstractTextLocationHolder {

    private TokenType type;
    private Value constant;
    private String identifier;
    private OperatorType operator;
    private Reserved reserved;
    private String comment;

    private void setConstant(final Value constant) {
        if (type != null && type != TokenType.CONSTANT) {
            throw new RuntimeException("cannot change type");
        }
        type = TokenType.CONSTANT;
        this.constant = constant;
    }

    private void setIdentifier(final String identifier) {
        if (type != null && type != TokenType.IDENTIFIER) {
            throw new RuntimeException("cannot change type");
        }
        type = TokenType.IDENTIFIER;
        this.identifier = identifier;
    }

    private void setOperator(final OperatorType operand) {
        if (type != null && type != TokenType.OPERATOR) {
            throw new RuntimeException("cannot change type");
        }
        type = TokenType.OPERATOR;
        this.operator = operand;
    }

    private void setReserved(final Reserved reserved) {
        if (type != null && type != TokenType.RESERVED) {
            throw new RuntimeException("cannot change type");
        }
        type = TokenType.RESERVED;
        this.reserved = reserved;
    }

    private Token(final TokenType type, final TextLocation start, final TextLocation end) {
        super(start, end);
        this.type = type;
    }

    public Token(final OperatorType operand, final TextLocation start, final TextLocation end) {
        super(start, end);
        setOperator(operand);
    }

    public Token(final Value number, final TextLocation start, final TextLocation end) {
        super(start, end);
        setConstant(number);
    }

    public Token(final String identifier, final TextLocation start, final TextLocation end) {
        super(start, end);
        setIdentifier(identifier);
    }

    public Token(final Reserved reserved, final TextLocation start, final TextLocation end) {
        super(start, end);
        setReserved(reserved);
    }

    public static Token commentToken(final String comment, final TextLocation start, final TextLocation end) {
        final Token token = new Token(TokenType.COMMENT, start, end);
        token.comment = comment;
        return token;
    }

    public TokenType getType() {
        return type;
    }

    public Value getConstant() {
        if (type != TokenType.CONSTANT) {
            throw new RuntimeException("getConstant called for a token that is not a constant");
        }
        return constant;
    }

    public String getIdentifer() {
        if (type != TokenType.IDENTIFIER) {
            throw new RuntimeException("getIdentifier called for a token that is not an identifier");
        }
        return identifier;
    }

    public OperatorType getOperator() {
        if (type != TokenType.OPERATOR) {
            throw new RuntimeException("getOperand called for a token that is not an operand ("
                                       + type.toString() + ")");
        }
        return operator;
    }

    public Reserved getReserved() {
        if (type != TokenType.RESERVED) {
            throw new RuntimeException("getReserved called for a token that is not a reserved word");
        }
        return reserved;
    }

    public String getComment() {
        if (type != TokenType.COMMENT) {
            throw new RuntimeException("getComment called for a token that is not a comment");
        }
        return comment;
    }

    @Override
    public String toString() {
        switch (getType()) {
            case IDENTIFIER:
                return "IDENTIFIER: " + getIdentifer();
            case CONSTANT:
                return "CONSTANT: " + getConstant();
            case OPERATOR:
                return "OPERATOR: " + getOperator();
            case RESERVED:
                return "RESERVED: " + getReserved();
            case COMMENT:
                return "COMMENT: " + getComment();
            default:
                throw new RuntimeException("Unhandled token type " + getType());
        }
    }

}
