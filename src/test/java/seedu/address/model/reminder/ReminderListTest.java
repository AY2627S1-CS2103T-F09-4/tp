package seedu.address.model.reminder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;

class ReminderListTest {
    private static final Reminder FIRST = new Reminder("First");
    private static final Reminder SECOND = new Reminder("Second");

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

    @Test void withReminder_allowsDuplicatesAndDoesNotMutateOriginal() {
        ReminderList original = new ReminderList(List.of(FIRST));
        ReminderList updated = original.withReminder(FIRST).withReminder(SECOND);
        assertEquals(List.of(FIRST), original.getReminders());
        assertEquals(List.of(FIRST, FIRST, SECOND), updated.getReminders());
    }

    @Test void accessAndDeletion_useOneBasedIndex() {
        ReminderList list = new ReminderList(List.of(FIRST, SECOND));
        assertEquals(2, list.size());
        assertEquals(SECOND, list.getReminder(Index.fromOneBased(2)));
        assertEquals(List.of(SECOND), list.withoutReminder(Index.fromOneBased(1)).getReminders());
        assertThrows(IndexOutOfBoundsException.class, () ->
                list.getReminder(Index.fromOneBased(3)));
        assertThrows(IndexOutOfBoundsException.class, () ->
                list.withoutReminder(Index.fromOneBased(3)));
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
