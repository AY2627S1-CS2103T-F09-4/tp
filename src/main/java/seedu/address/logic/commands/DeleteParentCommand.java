package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContact;

/** Deletes a parent contact. */
public class DeleteParentCommand extends Command {
    public static final String COMMAND_WORD = "delete-parent";
    private final Index index;

    public DeleteParentCommand(Index index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (index.getZeroBased() >= model.getParentContactList().size()) {
            throw new CommandException("The parent index provided is invalid.");
        }
        ParentContact target = model.getParentContactList().get(index.getZeroBased());
        model.deleteParentContact(target);
        return new CommandResult("Deleted parent contact: " + target.getName());
    }
}
