package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public enum Reserved {

    IF("if"),
    THEN("then"),
    ELSE("else"),
    ELSEIF("elseif"),
    ENDIF("endif"),
    FOR("for"),
    TO("to"),
    STEP("step"),
    DO("do"),
    DONE("done"),
    REPEAT("repeat"),
    TIMES("times"),
    WHILE("while"),
    FUNCTION("function"),
    ENDFUNC("endfunc"),
    RETURN("return"),
    LOCAL("local"),
    AND("and"),
    OR("or"),
    NOT("not"),
    TRUE("TRUE"),
    FALSE("FALSE"),
    PI("PI"),
    BREAKPOINT("breakpoint");

    private final String word;

    private Reserved(final String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return word;
    }

    public static Reserved getReservedEnum(final String s) {
        if (IF.toString().equals(s)) {
            return IF;
        }
        if (THEN.toString().equals(s)) {
            return THEN;
        }
        if (ELSE.toString().equals(s)) {
            return ELSE;
        }
        if (ELSEIF.toString().equals(s)) {
            return ELSEIF;
        }
        if (ENDIF.toString().equals(s)) {
            return ENDIF;
        }
        if (FOR.toString().equals(s)) {
            return FOR;
        }
        if (TO.toString().equals(s)) {
            return TO;
        }
        if (STEP.toString().equals(s)) {
            return STEP;
        }
        if (DO.toString().equals(s)) {
            return DO;
        }
        if (DONE.toString().equals(s)) {
            return DONE;
        }
        if (REPEAT.toString().equals(s)) {
            return REPEAT;
        }
        if (TIMES.toString().equals(s)) {
            return TIMES;
        }
        if (WHILE.toString().equals(s)) {
            return WHILE;
        }
        if (FUNCTION.toString().equals(s)) {
            return FUNCTION;
        }
        if (ENDFUNC.toString().equals(s)) {
            return ENDFUNC;
        }
        if (RETURN.toString().equals(s)) {
            return RETURN;
        }
        if (LOCAL.toString().equals(s)) {
            return LOCAL;
        }
        if (AND.toString().equals(s)) {
            return AND;
        }
        if (OR.toString().equals(s)) {
            return OR;
        }
        if (NOT.toString().equals(s)) {
            return NOT;
        }
        if (TRUE.toString().equals(s)) {
            return TRUE;
        }
        if (FALSE.toString().equals(s)) {
            return FALSE;
        }
        if (PI.toString().equals(s)) {
            return PI;
        }
        if (BREAKPOINT.toString().equals(s)) {
            return BREAKPOINT;
        }
        return null;
    }

}
