package no.shhsoft.basus.crosscompilers;

import no.shhsoft.basus.language.StatementList;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface CrossCompiler {

    void compile(StatementList statements, String baseName);

}
