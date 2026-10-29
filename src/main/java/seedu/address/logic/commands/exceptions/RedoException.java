package seedu.address.logic.commands.exceptions;

/**
 * Represents when the user attempts redoing when it is not possible.
 */
public class RedoException extends CommandException {
    public RedoException(String message) {
        super(message);
    }
}
