package seedu.address.model.reminder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class ReminderListTest {
    private static final Reminder FIRST = new Reminder(1, "First");
    private static final Reminder SECOND = new Reminder(2, "Second");

    @Test void constructor_rejectsNulls() {
        assertThrows(NullPointerException.class, () -> new ReminderList(null));
        assertThrows(NullPointerException.class, () -> new ReminderList(Arrays.asList(FIRST, null)));
    }

    @Test void constructor_defensivelyCopiesAndPreservesOrder() {
        List<Reminder> source = new ArrayList<>(List.of(FIRST, SECOND));
        ReminderList list = new ReminderList(source);
        source.clear();
        assertEquals(List.of(FIRST, SECOND), list.getReminders());
        assertThrows(UnsupportedOperationException.class, () -> list.getReminders().add(FIRST));
    }

    @Test void withReminder_allowsDuplicateTextAndDoesNotMutateOriginal() {
        ReminderList original = new ReminderList(List.of(FIRST));
        Reminder duplicateText = new Reminder(3, "First");
        ReminderList updated = original.withReminder(duplicateText).withReminder(SECOND);
        assertEquals(List.of(FIRST), original.getReminders());
        assertEquals(List.of(FIRST, duplicateText, SECOND), updated.getReminders());
        assertThrows(IllegalArgumentException.class, () -> original.withReminder(FIRST));
    }

    @Test void accessAndDeletion_useStableId() {
        ReminderList list = new ReminderList(List.of(FIRST, SECOND));
        assertEquals(2, list.size());
        assertEquals(SECOND, list.getReminderById(2));
        assertEquals(List.of(SECOND), list.withoutReminderId(1).getReminders());
        assertThrows(IllegalArgumentException.class, () -> list.getReminderById(3));
        assertThrows(IllegalArgumentException.class, () -> list.withoutReminderId(3));
    }

    @Test void equalityHashCodeAndToString() {
        ReminderList first = new ReminderList(List.of(FIRST, SECOND));
        ReminderList same = new ReminderList(List.of(FIRST, SECOND));
        ReminderList reversed = new ReminderList(List.of(SECOND, FIRST));
        assertEquals(first, first);
        assertEquals(first, same);
        assertEquals(first.hashCode(), same.hashCode());
        assertNotEquals(first, reversed);
        assertNotEquals(first, null);
        assertEquals("[First, Second]", first.toString());
    }
}
