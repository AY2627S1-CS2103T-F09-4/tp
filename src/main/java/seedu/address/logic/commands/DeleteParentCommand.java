package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContact;

/** Deletes a parent contact. */
public class DeleteParentCommand extends Command {
    public static final String COMMAND_WORD = "delete-parent";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the parent identified by the index number used in the displayed parent list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PARENT_SUCCESS = "Deleted parent: %1$s";

    private final Index index;

    public DeleteParentCommand(Index index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (index.getZeroBased() >= model.getParentContactList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PARENT_DISPLAYED_INDEX);
        }
        ParentContact target = model.getParentContactList().get(index.getZeroBased());
        model.deleteParentContact(target);
        return new CommandResult(String.format(MESSAGE_DELETE_PARENT_SUCCESS, Messages.format(target)));
    }
}
