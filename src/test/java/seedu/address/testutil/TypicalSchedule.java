package seedu.address.testutil;

import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.DayOfWeek;
import java.util.List;

import seedu.address.model.AddressBook;
import seedu.address.model.schedule.ScheduledSession;

/**
 * A utility class containing a list of {@code ScheduledSession} objects to be used in tests.
 */
public class TypicalSchedule {

    public static final ScheduledSession ALICE_MONDAY_MORNING = new ScheduledSessionBuilder()
            .withStudent(ALICE).withDay(DayOfWeek.MONDAY).withTimeSlot("10-00", "12-00").build();
    public static final ScheduledSession ALICE_WEDNESDAY = new ScheduledSessionBuilder()
            .withStudent(ALICE).withDay(DayOfWeek.WEDNESDAY).withTimeSlot("14-00", "15-30").build();
    // Overlaps ALICE_MONDAY_MORNING.
    public static final ScheduledSession BENSON_MONDAY_OVERLAP = new ScheduledSessionBuilder()
            .withStudent(BENSON).withDay(DayOfWeek.MONDAY).withTimeSlot("11-00", "13-00").build();

    private TypicalSchedule() {} // prevents instantiation

    /**
     * Returns the typical address book with the typical sessions added.
     */
    public static AddressBook getTypicalAddressBookWithSchedule() {
        AddressBook addressBook = TypicalPersons.getTypicalAddressBook();
        for (ScheduledSession session : getTypicalSessions()) {
            addressBook.addScheduledSession(session);
        }
        return addressBook;
    }

    public static List<ScheduledSession> getTypicalSessions() {
        return List.of(ALICE_MONDAY_MORNING, ALICE_WEDNESDAY, BENSON_MONDAY_OVERLAP);
    }
}
