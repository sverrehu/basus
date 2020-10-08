package no.shhsoft.basus.crosscompilers;

import no.shhsoft.basus.language.StatementList;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface CrossCompiler {

    String compile(StatementList statements);

}
