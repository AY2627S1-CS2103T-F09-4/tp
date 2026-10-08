package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.PhotoPath;

/** Removes a photo from a parent contact. */
public class DeleteParentPhotoCommand extends Command {
    public static final String COMMAND_WORD = "delete-parent-photo";
    private final Index index;

    public DeleteParentPhotoCommand(Index index) {
        this.index = index;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (index.getZeroBased() >= model.getParentContactList().size()) {
            throw new CommandException("The parent index provided is invalid.");
        }
        ParentContact old = model.getParentContactList().get(index.getZeroBased());
        ParentContact updated = new ParentContact(
                old.getId(), old.getName(), old.getPhone(), old.getEmail(), old.getLinkedStudentId(),
                old.getRequirements(), PhotoPath.NONE);
        model.setParentContact(old, updated);
        return new CommandResult("Deleted photo from parent: " + updated.getName());
    }
}
