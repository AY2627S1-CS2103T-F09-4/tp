package seedu.address.logic.commands;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/** Edits an existing parent contact. */
public class EditParentCommand extends Command {
    public static final String COMMAND_WORD = "edit-parent";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Edits a parent. Parameters: INDEX [n/NAME] [p/PHONE] [e/EMAIL] "
            + "[link/STUDENT_INDEX] [r/REQUIREMENT]...\n"
            + "At least one field is required. Empty link/ unlinks the student; empty r/ clears requirements.\n"
            + "Example: edit-parent 1 link/2 r/No food r/Send weekly updates";
    private final Index index;
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final boolean linkSpecified;
    private final Index linkIndex;
    private final List<String> requirements;

    /**
     * Creates a command to update an existing parent contact.
     */
    public EditParentCommand(Index index, Name name, Phone phone, Email email) {
        this(index, name, phone, email, false, null, null);
    }

    /**
     * Creates an edit, distinguishing omitted fields from explicit clearing.
     * A specified null link clears the link; null requirements leave requirements unchanged.
     */
    public EditParentCommand(Index index, Name name, Phone phone, Email email,
            boolean linkSpecified, Index linkIndex, List<String> requirements) {
        this.index = index;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.linkSpecified = linkSpecified;
        this.linkIndex = linkIndex;
        this.requirements = requirements == null ? null : List.copyOf(requirements);
    }

    /** Returns a validation reason followed by this command's usage instructions. */
    public static String withUsage(String reason) {
        return reason + "\n" + MESSAGE_USAGE;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (index.getZeroBased() >= model.getParentContactList().size()) {
            throw new CommandException(withUsage(Messages.MESSAGE_INVALID_PARENT_DISPLAYED_INDEX));
        }
        ParentContact old = model.getParentContactList().get(index.getZeroBased());
        String studentId = old.getLinkedStudentId();
        String studentName = old.getLinkedStudentName();
        if (linkSpecified) {
            studentId = null;
            studentName = null;
            if (linkIndex != null) {
                List<Person> students = model.getFilteredPersonList();
                if (linkIndex.getZeroBased() >= students.size()) {
                    throw new CommandException(withUsage(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX));
                }
                Person student = students.get(linkIndex.getZeroBased());
                studentId = student.getStudentId();
                studentName = student.getName().fullName;
                final String linkedId = studentId;
                boolean alreadyLinked = model.getAddressBook().getParentContactList().stream()
                        .anyMatch(parent -> !parent.getId().equals(old.getId())
                                && linkedId.equals(parent.getLinkedStudentId()));
                if (alreadyLinked) {
                    throw new CommandException(withUsage(AddContactCommand.MESSAGE_STUDENT_ALREADY_LINKED));
                }
            }
        }
        ParentContact updated = new ParentContact(
                old.getId(),
                name == null ? old.getName() : name,
                phone == null ? old.getPhone() : phone,
                email == null ? old.getEmail() : email,
                studentId,
                studentName,
                requirements == null ? old.getRequirements() : requirements,
                old.getPhotoPath());
        boolean duplicate = model.getAddressBook().getParentContactList().stream()
                .anyMatch(parent -> !parent.getId().equals(old.getId()) && parent.isSameContact(updated));
        if (duplicate) {
            throw new CommandException(withUsage(AddContactCommand.MESSAGE_DUPLICATE_PARENT));
        }
        model.setParentContact(old, updated);
        return new CommandResult("Edited parent contact: " + updated.getName());
    }
}
