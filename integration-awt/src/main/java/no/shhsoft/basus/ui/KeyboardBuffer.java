package no.shhsoft.basus.ui;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class KeyboardBuffer {

    private final char[] characters = new char[10];
    private int length;

    public synchronized void addCharacter(final char c) {
        if (length == characters.length) {
            return;
        }
        characters[length++] = c;
        notifyAll();
    }

    public synchronized boolean isCharacterWaiting() {
        return length > 0;
    }

    public synchronized char nextCharacter() {
        while (!isCharacterWaiting()) {
            try {
                wait();
            } catch (final InterruptedException e) {
                return Character.MIN_VALUE;
            }
        }
        final char c = characters[0];
        System.arraycopy(characters, 1, characters, 0, --length);
        return c;
    }

    public synchronized void clear() {
        length = 0;
    }

}
