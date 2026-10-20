package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.reminder.Reminder;

class DeleteReminderCommandTest {
    @Test void execute_validId_deletesExactlyIdentifiedReminder() {
        Model model = modelWithReminders();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expected.deleteReminder(2);
        assertCommandSuccess(new DeleteReminderCommand(2), model,
                "Deleted reminder: Second", expected);
    }

    @Test void execute_emptyOrOutOfRange_failure() {
        assertCommandFailure(new DeleteReminderCommand(1), new ModelManager(),
                DeleteReminderCommand.MESSAGE_INVALID_ID);
        Model model = modelWithReminders();
        assertCommandFailure(new DeleteReminderCommand(3), model, DeleteReminderCommand.MESSAGE_INVALID_ID);
    }

    @Test void equalityAndToString() {
        DeleteReminderCommand command = new DeleteReminderCommand(1);
        assertEquals(command, command);
        assertEquals(command, new DeleteReminderCommand(1));
        assertNotEquals(command, new DeleteReminderCommand(2));
        assertNotEquals(command, null);
        assertEquals(DeleteReminderCommand.class.getCanonicalName() + "{targetId=1}",
                command.toString());
    }

    private static Model modelWithReminders() {
        Model model = new ModelManager();
        model.addReminder("First", null);
        model.addReminder("Second", null);
        return model;
    }

    @Test void execute_displayOrderDiffersFromIds_deletesById() throws Exception {
        Model model = new ModelManager();
        model.addReminder("Untimed", null);
        model.addReminder("Overdue", java.time.LocalDateTime.of(2000, 1, 1, 0, 0));

        new DeleteReminderCommand(1).execute(model);

        assertEquals(java.util.List.of(new Reminder(2, "Overdue",
                java.time.LocalDateTime.of(2000, 1, 1, 0, 0))), model.getReminderList());
    }
}
