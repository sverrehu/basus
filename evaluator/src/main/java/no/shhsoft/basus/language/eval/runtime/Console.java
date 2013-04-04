package no.shhsoft.basus.language.eval.runtime;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface Console {

    void print(final String s);

    void println(final String s);

    String readln();

    int readKey();

}
