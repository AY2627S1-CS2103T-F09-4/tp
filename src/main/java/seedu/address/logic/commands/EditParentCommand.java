package seedu.address.logic.commands;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Phone;

/** Edits an existing parent contact. */
public class EditParentCommand extends Command {
    public static final String COMMAND_WORD = "edit-parent";
    private final Index index;
    private final Name name;
    private final Phone phone;
    private final Email email;

    /**
     * Creates a command to update an existing parent contact.
     */
    public EditParentCommand(Index index, Name name, Phone phone, Email email) {
        this.index = index;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (index.getZeroBased() >= model.getParentContactList().size()) {
            throw new CommandException("The parent index provided is invalid.");
        }
        ParentContact old = model.getParentContactList().get(index.getZeroBased());
        ParentContact updated = new ParentContact(
                old.getId(),
                name == null ? old.getName() : name,
                phone == null ? old.getPhone() : phone,
                email == null ? old.getEmail() : email,
                old.getLinkedStudentId(),
                old.getRequirements());
        model.setParentContact(old, updated);
        return new CommandResult("Edited parent contact: " + updated.getName());
    }
}
