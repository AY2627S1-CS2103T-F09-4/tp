package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.storage.SnapshotManager;

/**
 * Redoes the last {@code Undo's} changes on the address book.
 * Throws a {@code CommandException} if not possible.
 */
public class RedoCommand extends Command {
    public static final String COMMAND_WORD = "redo";
    public static final String MESSAGE_SUCCESS = "Last command redone: ";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        String feedbackToUser;
        if (SnapshotManager.canRedo()) {
            feedbackToUser = model.redo();
        } else {
            throw new CommandException(Messages.MESSAGE_UNABLE_TO_REDO);
        }
        return new CommandResult(MESSAGE_SUCCESS + feedbackToUser, false, false, false, true);
    }
}
