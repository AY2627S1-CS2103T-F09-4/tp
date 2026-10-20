package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.reminder.Reminder;

class JsonAdaptedReminderTest {
    @Test void conversion_validReminder_roundTrips() throws Exception {
        Reminder reminder = new Reminder(1, "Collect worksheets");
        String json = JsonUtil.toJsonString(new JsonAdaptedReminder(reminder));
        assertEquals(reminder, JsonUtil.fromJsonString(json, JsonAdaptedReminder.class).toModelType(1));
    }

    @Test void toModelType_nullBlankOrWhitespace_throwsIllegalValueException() {
        assertThrows(IllegalValueException.class, Reminder.MESSAGE_CONSTRAINTS, () ->
                new JsonAdaptedReminder((String) null).toModelType(1));
        assertThrows(IllegalValueException.class, Reminder.MESSAGE_CONSTRAINTS, () ->
                new JsonAdaptedReminder("").toModelType(1));
        assertThrows(IllegalValueException.class, Reminder.MESSAGE_CONSTRAINTS, () ->
                new JsonAdaptedReminder("  ").toModelType(1));
        assertThrows(IllegalValueException.class, Reminder.MESSAGE_ID_CONSTRAINTS, () ->
                new JsonAdaptedReminder("Valid").toModelType(0));
    }
}
