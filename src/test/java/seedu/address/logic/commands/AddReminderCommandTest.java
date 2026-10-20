package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.reminder.ReminderDisplay;

class AddReminderCommandTest {
    private static final String TEXT = "Collect worksheets";

    @Test void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddReminderCommand(null));
    }

    @Test void execute_addsReminderAndAllowsDuplicates() {
        Model model = new ModelManager();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        seedu.address.model.reminder.Reminder first = expected.addReminder(TEXT, null);
        assertCommandSuccess(new AddReminderCommand(TEXT), model,
                String.format(AddReminderCommand.MESSAGE_SUCCESS, ReminderDisplay.format(first, model.getClock())),
                expected);

        seedu.address.model.reminder.Reminder second = expected.addReminder(TEXT, null);
        assertCommandSuccess(new AddReminderCommand(TEXT), model,
                String.format(AddReminderCommand.MESSAGE_SUCCESS, ReminderDisplay.format(second, model.getClock())),
                expected);
    }

    @Test void equalityAndToString() {
        AddReminderCommand command = new AddReminderCommand(TEXT);
        assertEquals(command, command);
        assertEquals(command, new AddReminderCommand(TEXT));
        assertNotEquals(command, new AddReminderCommand("Different"));
        assertNotEquals(command, null);
        assertEquals(AddReminderCommand.class.getCanonicalName() + "{text=Collect worksheets, dueAt=null}",
                command.toString());
    }

    @Test void execute_timedReminderAndDeletedIdIsNotReused() throws Exception {
        Model model = new ModelManager();
        Reminder first = model.addReminder("Temporary", null);
        model.deleteReminder(first.getId());
        LocalDateTime dueAt = LocalDateTime.of(2026, 10, 30, 9, 0);

        new AddReminderCommand("Prepare notes", dueAt).execute(model);

        assertEquals(2, model.getReminderList().get(0).getId());
        assertEquals(dueAt, model.getReminderList().get(0).getDueAt().orElseThrow());
    }
}
