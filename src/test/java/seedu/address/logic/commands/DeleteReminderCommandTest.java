package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.reminder.Reminder;

class DeleteReminderCommandTest {
    private static final Index FIRST = Index.fromOneBased(1);

    @Test void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new DeleteReminderCommand(null));
    }

    @Test void execute_validIndex_deletesExactlyIndexedReminder() {
        Model model = modelWithReminders();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expected.deleteReminder(Index.fromOneBased(2));
        assertCommandSuccess(new DeleteReminderCommand(Index.fromOneBased(2)), model,
                "Deleted reminder: Second", expected);
    }

    @Test void execute_emptyOrOutOfRange_failure() {
        assertCommandFailure(new DeleteReminderCommand(FIRST), new ModelManager(),
                DeleteReminderCommand.MESSAGE_INVALID_INDEX);
        Model model = modelWithReminders();
        assertCommandFailure(new DeleteReminderCommand(Index.fromOneBased(3)), model,
                DeleteReminderCommand.MESSAGE_INVALID_INDEX);
    }

    @Test void equalityAndToString() {
        DeleteReminderCommand command = new DeleteReminderCommand(FIRST);
        assertEquals(command, command);
        assertEquals(command, new DeleteReminderCommand(FIRST));
        assertNotEquals(command, new DeleteReminderCommand(Index.fromOneBased(2)));
        assertNotEquals(command, null);
        assertEquals(DeleteReminderCommand.class.getCanonicalName() + "{targetIndex=" + FIRST + "}",
                command.toString());
    }

    private static Model modelWithReminders() {
        Model model = new ModelManager();
        model.addReminder(new Reminder("First"));
        model.addReminder(new Reminder("Second"));
        return model;
    }
}
