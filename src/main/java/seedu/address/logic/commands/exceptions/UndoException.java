package seedu.address.logic.commands.exceptions;

/**
 * Represents when the user attempts undoing when it is not possible.
 */
public class UndoException extends CommandException {
    public UndoException(String message) {
        super(message);
    }
}
