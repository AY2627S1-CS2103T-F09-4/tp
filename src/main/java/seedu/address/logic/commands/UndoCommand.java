package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.commands.exceptions.UndoException;
import seedu.address.model.Model;
import seedu.address.storage.SnapshotManager;

/**
 * Undoes the last command's changes on the address book.
 * Throws a {@code CommandException} if not possible.
 */
public class UndoCommand extends Command {
    public static final String COMMAND_WORD = "undo";
    public static final String MESSAGE_SUCCESS = "Last command undone: ";

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        String feedbackToUser;
        if (SnapshotManager.canUndo()) {
            feedbackToUser = model.undo();
        } else {
            throw new UndoException(Messages.MESSAGE_UNABLE_TO_UNDO);
        }
        return new CommandResult(MESSAGE_SUCCESS + feedbackToUser, false, false, true, false);
    }
}
