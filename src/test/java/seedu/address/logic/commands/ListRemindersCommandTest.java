package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;

class ListRemindersCommandTest {
    @Test void execute_emptyList_reportsNoReminders() throws Exception {
        CommandResult result = new ListRemindersCommand().execute(new ModelManager());
        assertEquals(ListRemindersCommand.MESSAGE_NO_REMINDERS, result.getFeedbackToUser());
    }

    @Test void execute_reminders_printsNumberedInsertionOrder() throws Exception {
        Model model = new ModelManager();
        model.addReminder("First", null);
        model.addReminder("Second", null);
        CommandResult result = new ListRemindersCommand().execute(model);
        assertEquals("Reminders:" + System.lineSeparator() + "[ID 1] First [EVERY STARTUP]"
                + System.lineSeparator() + "[ID 2] Second [EVERY STARTUP]", result.getFeedbackToUser());
    }

    @Test void equality() {
        assertEquals(new ListRemindersCommand(), new ListRemindersCommand());
    }

    @Test void execute_allStatuses_printsSharedUrgencyOrder() throws Exception {
        Clock clock = Clock.fixed(Instant.parse("2026-10-20T04:00:00Z"), ZoneId.of("Asia/Singapore"));
        Model model = new ModelManager(new AddressBook(), new UserPrefs(), clock);
        model.addReminder("Untimed", null);
        model.addReminder("Upcoming", LocalDateTime.of(2026, 10, 22, 12, 1));
        model.addReminder("Due soon", LocalDateTime.of(2026, 10, 21, 12, 0));
        model.addReminder("Overdue", LocalDateTime.of(2026, 10, 20, 11, 59));

        String result = new ListRemindersCommand().execute(model).getFeedbackToUser();

        assertEquals("Reminders:" + System.lineSeparator()
                + "[ID 4] Overdue [2026-10-20 11:59] [OVERDUE]" + System.lineSeparator()
                + "[ID 3] Due soon [2026-10-21 12:00] [DUE SOON]" + System.lineSeparator()
                + "[ID 2] Upcoming [2026-10-22 12:01] [UPCOMING]" + System.lineSeparator()
                + "[ID 1] Untimed [EVERY STARTUP]", result);
    }
}
