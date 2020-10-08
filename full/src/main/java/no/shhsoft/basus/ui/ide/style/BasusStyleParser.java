package no.shhsoft.basus.ui.ide.style;

import java.util.ArrayList;
import java.util.List;

import no.shhsoft.basus.language.OperatorType;
import no.shhsoft.basus.language.parser.BasusTokenizer;
import no.shhsoft.basus.language.parser.ParserException;
import no.shhsoft.basus.language.parser.Token;
import no.shhsoft.basus.utils.TextLocationHolder;
import no.shhsoft.basus.value.StringValue;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class BasusStyleParser {

    private void appendStyle(final List<StyleArea> styleAreas,
                             final int from, final int to, final Style style) {
           styleAreas.add(new StyleArea(from, to, style));
       }

    private void appendStyle(final List<StyleArea> styleAreas,
                          final TextLocationHolder textLocationHolder, final Style style) {
        final int from = textLocationHolder.getStartLocation().getOffset();
        final int to = textLocationHolder.getEndLocation().getOffset();
        appendStyle(styleAreas, from, to, style);
    }

    private void determineAndAppendStyle(final List<StyleArea> styleAreas, final Token token) {
        switch (token.getType()) {
            case IDENTIFIER:
                break;
            case CONSTANT:
                if (token.getConstant() instanceof StringValue) {
                    appendStyle(styleAreas, token, Style.STRING);
                }
                break;
            case COMMENT:
                appendStyle(styleAreas, token, Style.COMMENT);
                break;
            case RESERVED:
                appendStyle(styleAreas, token, Style.RESERVED_WORD);
                break;
            case OPERATOR:
                final OperatorType type = token.getOperator();
                if (type == OperatorType.AND || type == OperatorType.OR || type == OperatorType.NOT) {
                    appendStyle(styleAreas, token, Style.RESERVED_WORD);
                }
                break;
            default:
        }
    }

    public List<StyleArea> getStyleAreas(final String basusProgram) {
        final List<StyleArea> styleAreas = new ArrayList<>();
        try {
            final BasusTokenizer tokenizer = new BasusTokenizer(basusProgram, true);
            for (;;) {
                final Token token = tokenizer.nextToken();
                if (token == null) {
                    break;
                }
                determineAndAppendStyle(styleAreas, token);
            }
        } catch (final ParserException e) {
            appendStyle(styleAreas, e.getStartLocation().getOffset(), basusProgram.length() - 1, Style.ERROR);
        }
        return styleAreas;
    }

}
