package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.reminder.Reminder;

class AddReminderCommandTest {
    private static final Reminder REMINDER = new Reminder("Collect worksheets");

    @Test void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new AddReminderCommand(null));
    }

    @Test void execute_addsReminderAndAllowsDuplicates() {
        Model model = new ModelManager();
        Model expected = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expected.addReminder(REMINDER);
        assertCommandSuccess(new AddReminderCommand(REMINDER), model,
                String.format(AddReminderCommand.MESSAGE_SUCCESS, REMINDER), expected);

        expected.addReminder(REMINDER);
        assertCommandSuccess(new AddReminderCommand(REMINDER), model,
                String.format(AddReminderCommand.MESSAGE_SUCCESS, REMINDER), expected);
    }

    @Test void equalityAndToString() {
        AddReminderCommand command = new AddReminderCommand(REMINDER);
        assertEquals(command, command);
        assertEquals(command, new AddReminderCommand(REMINDER));
        assertNotEquals(command, new AddReminderCommand(new Reminder("Different")));
        assertNotEquals(command, null);
        assertEquals(AddReminderCommand.class.getCanonicalName() + "{reminder=Collect worksheets}",
                command.toString());
    }
}
