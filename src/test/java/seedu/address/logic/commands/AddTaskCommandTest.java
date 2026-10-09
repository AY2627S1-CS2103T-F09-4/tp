package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.task.Task;
import seedu.address.testutil.PersonBuilder;

public class AddTaskCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndex_appendsTaskInOrder() {
        Person personToUpdate = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person personWithExistingTask = new PersonBuilder(personToUpdate).withTasks("revise notes").build();
        model.setPerson(personToUpdate, personWithExistingTask);
        Person updatedPerson = new PersonBuilder(personWithExistingTask)
                .withTasks("revise notes", "do homework").build();
        AddTaskCommand command = new AddTaskCommand(INDEX_FIRST_PERSON, new Task("do homework"));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personWithExistingTask, updatedPerson);
        String expectedMessage = String.format(AddTaskCommand.MESSAGE_ADD_TASK_SUCCESS, Messages.format(updatedPerson));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(new Task("revise notes"), new Task("do homework")),
                model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()).getTaskList().getTasks());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndex() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        Person personToUpdate = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person updatedPerson = new PersonBuilder(personToUpdate).withTasks("do homework").build();
        AddTaskCommand command = new AddTaskCommand(INDEX_FIRST_PERSON, new Task("do homework"));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToUpdate, updatedPerson);
        String expectedMessage = String.format(AddTaskCommand.MESSAGE_ADD_TASK_SUCCESS, Messages.format(updatedPerson));

        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(new Task("do homework")), updatedPerson.getTaskList().getTasks());
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index outOfBoundsIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        AddTaskCommand command = new AddTaskCommand(outOfBoundsIndex, new Task("do homework"));

        assertCommandFailure(command, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        AddTaskCommand first = new AddTaskCommand(INDEX_FIRST_PERSON, new Task("do homework"));
        AddTaskCommand same = new AddTaskCommand(INDEX_FIRST_PERSON, new Task("do homework"));
        AddTaskCommand differentIndex = new AddTaskCommand(INDEX_SECOND_PERSON, new Task("do homework"));
        AddTaskCommand differentTask = new AddTaskCommand(INDEX_FIRST_PERSON, new Task("revise notes"));

        assertTrue(first.equals(first));
        assertTrue(first.equals(same));
        assertFalse(first.equals(differentIndex));
        assertFalse(first.equals(differentTask));
        assertFalse(first.equals(null));
        assertFalse(first.equals(new ClearCommand()));
    }

    @Test
    public void toStringMethod() {
        AddTaskCommand command = new AddTaskCommand(INDEX_FIRST_PERSON, new Task("do homework"));
        String expected = AddTaskCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON
                + ", task=do homework}";

        assertEquals(expected, command.toString());
    }
}