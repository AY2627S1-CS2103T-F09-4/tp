package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.List;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;

/**
 * Shows the student linked to a parent: switches to the Students tab and selects that student.
 */
public class ViewChildCommand extends Command {

    public static final String COMMAND_WORD = "view-child";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Shows the student linked to the parent identified by the index number used in the displayed "
            + "parent list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_VIEW_CHILD_SUCCESS = "Showing %1$s, the linked student of %2$s.";
    public static final String MESSAGE_NO_LINKED_STUDENT = "%1$s has no linked student. Link one with "
            + EditParentCommand.COMMAND_WORD + " INDEX link/STUDENT_INDEX.";
    public static final String MESSAGE_LINKED_STUDENT_NOT_FOUND =
            "The student linked to %1$s no longer exists.";

    private static final Logger logger = LogsCenter.getLogger(ViewChildCommand.class);

    private final Index parentIndex;

    /**
     * Creates a ViewChildCommand that shows the student linked to the parent at {@code parentIndex}.
     */
    public ViewChildCommand(Index parentIndex) {
        requireNonNull(parentIndex);
        this.parentIndex = parentIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<ParentContact> parents = model.getParentContactList();
        if (parentIndex.getZeroBased() >= parents.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PARENT_DISPLAYED_INDEX);
        }

        ParentContact parent = parents.get(parentIndex.getZeroBased());
        String parentName = parent.getName().fullName;
        String studentId = parent.getLinkedStudentId();
        if (studentId == null) {
            throw new CommandException(String.format(MESSAGE_NO_LINKED_STUDENT, parentName));
        }

        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        Person child = model.getFilteredPersonList().stream()
                .filter(student -> student.getStudentId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> {
                    logger.warning("Parent " + parentName + " is linked to a missing student " + studentId);
                    return new CommandException(String.format(MESSAGE_LINKED_STUDENT_NOT_FOUND, parentName));
                });

        assert child.getStudentId().equals(studentId) : "Found a student other than the linked one";
        logger.fine("Showing the student linked to " + parentName);
        return CommandResult.showingStudent(
                String.format(MESSAGE_VIEW_CHILD_SUCCESS, child.getName().fullName, parentName), studentId);
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ViewChildCommand otherViewChildCommand)) {
            return false;
        }
        return parentIndex.equals(otherViewChildCommand.parentIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("parentIndex", parentIndex)
                .toString();
    }
}
