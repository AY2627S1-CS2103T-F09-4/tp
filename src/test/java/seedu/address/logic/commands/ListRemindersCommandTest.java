package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.reminder.Reminder;

class ListRemindersCommandTest {
    @Test void execute_emptyList_reportsNoReminders() throws Exception {
        CommandResult result = new ListRemindersCommand().execute(new ModelManager());
        assertEquals(ListRemindersCommand.MESSAGE_NO_REMINDERS, result.getFeedbackToUser());
    }

    @Test void execute_reminders_printsNumberedInsertionOrder() throws Exception {
        Model model = new ModelManager();
        model.addReminder(new Reminder("First"));
        model.addReminder(new Reminder("Second"));
        CommandResult result = new ListRemindersCommand().execute(model);
        assertEquals("Reminders:" + System.lineSeparator() + "1. First"
                + System.lineSeparator() + "2. Second", result.getFeedbackToUser());
    }

    @Test void equality() {
        assertEquals(new ListRemindersCommand(), new ListRemindersCommand());
    }
}
