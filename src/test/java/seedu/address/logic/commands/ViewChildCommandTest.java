package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ParentContact;
import seedu.address.testutil.ParentContactBuilder;

public class ViewChildCommandTest {
    private static final Index FIRST_PARENT = Index.fromOneBased(1);

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ViewChildCommand(null));
    }

    @Test
    public void execute_linkedParent_showsChild() throws Exception {
        ParentContact parent = addParentLinkedTo(BENSON.getStudentId());

        CommandResult result = new ViewChildCommand(FIRST_PARENT).execute(model);

        assertEquals(String.format(ViewChildCommand.MESSAGE_VIEW_CHILD_SUCCESS, BENSON.getName().fullName,
                parent.getName().fullName), result.getFeedbackToUser());
        assertEquals(Optional.of(BENSON.getStudentId()), result.getStudentIdToShow());
    }

    @Test
    public void execute_childHiddenBySearch_showsAllStudents() throws Exception {
        addParentLinkedTo(BENSON.getStudentId());
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertFalse(model.getFilteredPersonList().contains(BENSON));

        new ViewChildCommand(FIRST_PARENT).execute(model);

        assertEquals(getTypicalAddressBook().getPersonList().size(), model.getFilteredPersonList().size());
        assertTrue(model.getFilteredPersonList().contains(BENSON));
    }

    @Test
    public void execute_invalidParentIndex_throwsCommandException() {
        addParentLinkedTo(BENSON.getStudentId());

        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PARENT_DISPLAYED_INDEX, () ->
                new ViewChildCommand(INDEX_SECOND_PERSON).execute(model));
    }

    @Test
    public void execute_parentWithoutLinkedStudent_throwsCommandException() {
        ParentContact parent = new ParentContactBuilder().withLinkedStudentId(null).build();
        model.addParentContact(parent);

        assertThrows(CommandException.class,
                String.format(ViewChildCommand.MESSAGE_NO_LINKED_STUDENT, parent.getName().fullName), () ->
                        new ViewChildCommand(FIRST_PARENT).execute(model));
    }

    @Test
    public void execute_linkedStudentDeleted_throwsCommandException() {
        ParentContact parent = addParentLinkedTo(BENSON.getStudentId());
        model.deletePerson(BENSON);

        String expectedMessage =
                String.format(ViewChildCommand.MESSAGE_LINKED_STUDENT_NOT_FOUND, parent.getName().fullName);
        assertThrows(CommandException.class, expectedMessage, () ->
                new ViewChildCommand(FIRST_PARENT).execute(model));
    }

    @Test
    public void isReadOnly_returnsTrue() {
        assertTrue(new ViewChildCommand(FIRST_PARENT).isReadOnly());
    }

    @Test
    public void equals() {
        ViewChildCommand first = new ViewChildCommand(FIRST_PARENT);

        assertTrue(first.equals(first));
        assertTrue(first.equals(new ViewChildCommand(Index.fromOneBased(1))));
        assertFalse(first.equals(new ViewChildCommand(Index.fromOneBased(2))));
        assertFalse(first.equals(null));
        assertFalse(first.equals(1));
    }

    @Test
    public void toStringMethod() {
        assertEquals(ViewChildCommand.class.getCanonicalName() + "{parentIndex=" + FIRST_PARENT + "}",
                new ViewChildCommand(FIRST_PARENT).toString());
    }

    private ParentContact addParentLinkedTo(String studentId) {
        ParentContact parent = new ParentContactBuilder().withLinkedStudentId(studentId).build();
        model.addParentContact(parent);
        return parent;
    }
}
