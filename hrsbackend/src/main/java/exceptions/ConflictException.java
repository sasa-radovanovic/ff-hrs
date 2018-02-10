package exceptions;

/**
 * Created by sasaradovanovic on 12/30/17.
 */
public class ConflictException extends Exception {

    public String message;

    public ConflictException(String s) {
        super();
        this.message = s;
    }

    public String getConflictMessage() {
        return this.message;
    }

}
