package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public enum OperatorType {

    LEFT_PAREN,
    RIGHT_PAREN,
    LEFT_BRACKET,
    RIGHT_BRACKET,
    COMMA,
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    MODULUS,
    EXPONENTIATE,
    SEMICOLON,
    ASSIGN,
    NOT,
    EQUAL,
    NOT_EQUAL,
    LESS,
    LESS_OR_EQUAL,
    GREATER,
    GREATER_OR_EQUAL,
    AND,
    OR;

    public static boolean isRelational(final OperatorType operator) {
        return operator == EQUAL || operator == NOT_EQUAL || operator == LESS
            || operator == LESS_OR_EQUAL || operator == GREATER || operator == GREATER_OR_EQUAL;
    }

}
