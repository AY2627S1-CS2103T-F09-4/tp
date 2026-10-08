package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;

public class AddContactCommand extends Command {
    public static final String COMMAND_WORD = "add-parent";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Adds a parent contact. Parameters: n/NAME p/PHONE [e/EMAIL] "
            + "[link/STUDENT_INDEX] [r/REQUIREMENT]...\n"
            + "Example: " + COMMAND_WORD
            + " n/Mrs Tan p/98765432 e/tanmrs@email.com link/1 r/No food during session";
    public static final String MESSAGE_SUCCESS = "New parent contact added: %1$s";
    public static final String MESSAGE_DUPLICATE = "This parent contact already exists.";
    public static final String MESSAGE_INVALID_LINK = "The student index provided for linking is invalid.";
    private final ParentContact contact;
    private final Index linkIndex;

    public AddContactCommand(ParentContact contact, Index linkIndex) {
        this.contact = requireNonNull(contact); this.linkIndex = linkIndex;
    }

    @Override public CommandResult execute(Model model) throws CommandException {
        String studentId = null;
        if (linkIndex != null) {
            List<Person> students = model.getFilteredPersonList();
            if (linkIndex.getZeroBased() >= students.size()) throw new CommandException(MESSAGE_INVALID_LINK);
            Person student = students.get(linkIndex.getZeroBased());
            studentId = student.getName().fullName + "|" + student.getPhone().value;
        }
        ParentContact toAdd = new ParentContact(contact.getId(), contact.getName(), contact.getPhone(),
                contact.getEmail(), studentId, contact.getRequirements());
        if (model.hasParentContact(toAdd)) throw new CommandException(MESSAGE_DUPLICATE);
        final String resolvedStudentId = studentId;
        if (resolvedStudentId != null && model.getParentContactList().stream()
                .anyMatch(p -> resolvedStudentId.equals(p.getLinkedStudentId()))) {
            throw new CommandException("Each student can have at most one parent contact.");
        }
        model.addParentContact(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd.getName()));
    }
}
