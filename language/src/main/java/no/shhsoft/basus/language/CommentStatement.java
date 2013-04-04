package no.shhsoft.basus.language;

/**
 * @author <a href="mailto:shh@thathost.com">Sverre H. Huseby</a>
 */
public final class CommentStatement
extends AbstractStatement {

    private final String comment;

    public CommentStatement(final String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

}
