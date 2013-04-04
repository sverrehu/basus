package no.shhsoft.basus.language.parser;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface Tokenizer {

    Token nextToken();

    void pushBack(Token token);

}
