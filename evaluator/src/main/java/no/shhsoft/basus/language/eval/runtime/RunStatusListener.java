package no.shhsoft.basus.language.eval.runtime;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public interface RunStatusListener {

    void notifyStarting();

    void notifyStopping();

}
