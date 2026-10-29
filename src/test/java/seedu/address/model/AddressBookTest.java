package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;
import static seedu.address.testutil.TypicalSchedule.ALICE_MONDAY_MORNING;
import static seedu.address.testutil.TypicalSchedule.ALICE_WEDNESDAY;
import static seedu.address.testutil.TypicalSchedule.BENSON_MONDAY_OVERLAP;
import static seedu.address.testutil.TypicalSchedule.getTypicalAddressBookWithSchedule;
import static seedu.address.testutil.TypicalSchedule.getTypicalSessions;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.testutil.PersonBuilder;

public class AddressBookTest {

    private final AddressBook addressBook = new AddressBook();

    @Test
    public void constructor() {
        assertEquals(List.of(), addressBook.getPersonList());
        assertEquals(List.of(), addressBook.getReminderList());
    }

    @Test
    public void reminderOperations_preserveOrderAndCopyResetData() {
        Reminder first = new Reminder(1, "First");
        Reminder second = new Reminder(2, "Second");
        addressBook.addReminder(first);
        addressBook.addReminder(second);
        assertEquals(List.of(first, second), addressBook.getReminderList());

        AddressBook copy = addressBook.copy();
        addressBook.removeReminder(1);
        assertEquals(List.of(second), addressBook.getReminderList());
        assertEquals(List.of(first, second), copy.getReminderList());

        addressBook.resetData(copy);
        assertEquals(List.of(first, second), addressBook.getReminderList());
    }

    @Test
    public void resetData_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.resetData(null));
    }

    @Test
    public void resetData_withValidReadOnlyAddressBook_replacesData() {
        AddressBook newData = getTypicalAddressBook();
        addressBook.resetData(newData);
        assertEquals(newData, addressBook);
    }

    @Test
    public void resetData_withDuplicatePersons_throwsDuplicatePersonException() {
        // Two persons with the same identity fields
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        List<Person> newPersons = List.of(ALICE, editedAlice);
        AddressBookStub newData = new AddressBookStub(newPersons);

        assertThrows(DuplicatePersonException.class, () -> addressBook.resetData(newData));
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        assertTrue(addressBook.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personWithSameIdentityFieldsInAddressBook_returnsTrue() {
        addressBook.addPerson(ALICE);
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND)
                .build();
        assertTrue(addressBook.hasPerson(editedAlice));
    }

    @Test
    public void getPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getPersonList().remove(0));
    }

    @Test
    public void addScheduledSession_addsSessionAndAllowsOverlap() {
        AddressBook book = getTypicalAddressBook();
        book.addScheduledSession(ALICE_MONDAY_MORNING);
        book.addScheduledSession(BENSON_MONDAY_OVERLAP);

        assertEquals(List.of(ALICE_MONDAY_MORNING, BENSON_MONDAY_OVERLAP), book.getScheduleList());
        assertThrows(NullPointerException.class, () -> book.addScheduledSession(null));
    }

    @Test
    public void getScheduleList_modifyList_throwsUnsupportedOperationException() {
        addressBook.addScheduledSession(ALICE_MONDAY_MORNING);
        assertThrows(UnsupportedOperationException.class, () -> addressBook.getScheduleList().remove(0));
    }

    @Test
    public void resetData_copiesSchedule() {
        AddressBook newData = getTypicalAddressBookWithSchedule();
        addressBook.resetData(newData);

        assertEquals(getTypicalSessions(), addressBook.getScheduleList());
        assertEquals(newData, addressBook);
    }

    @Test
    public void setPerson_updatesStudentInTheirSessionsOnly() {
        AddressBook book = getTypicalAddressBookWithSchedule();
        Person editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();

        book.setPerson(ALICE, editedAlice);

        assertEquals(List.of(
                ALICE_MONDAY_MORNING.withStudentReplaced(ALICE, editedAlice),
                ALICE_WEDNESDAY.withStudentReplaced(ALICE, editedAlice),
                BENSON_MONDAY_OVERLAP), book.getScheduleList());
    }

    @Test
    public void removePerson_removesTheirSessionsOnly() {
        AddressBook book = getTypicalAddressBookWithSchedule();

        book.removePerson(ALICE);

        assertEquals(List.of(BENSON_MONDAY_OVERLAP), book.getScheduleList());
    }

    @Test
    public void copy_sessionsReferenceTheCopiedPersons() {
        AddressBook book = getTypicalAddressBookWithSchedule();

        AddressBook copy = book.copy();

        assertEquals(book, copy);
        for (ScheduledSession session : copy.getScheduleList()) {
            assertTrue(copy.getPersonList().stream().anyMatch(p -> p == session.getStudents().getFirst()));
            assertTrue(book.getPersonList().stream().noneMatch(p -> p == session.getStudents().getFirst()));
        }
    }

    @Test
    public void hasScheduledSession_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.hasScheduledSession(null));
    }

    @Test
    public void hasScheduledSession_matchesByEquality() {
        AddressBook book = getTypicalAddressBookWithSchedule();

        assertTrue(book.hasScheduledSession(ALICE_MONDAY_MORNING));
        assertFalse(addressBook.hasScheduledSession(ALICE_MONDAY_MORNING));
    }

    @Test
    public void removeScheduledSession_nullSession_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> addressBook.removeScheduledSession(null));
    }

    @Test
    public void removeScheduledSession_absentSession_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> addressBook.removeScheduledSession(ALICE_MONDAY_MORNING));
    }

    @Test
    public void removeScheduledSession_existingSession_removesOnlyOneCopy() {
        AddressBook book = getTypicalAddressBookWithSchedule();
        ScheduledSession duplicate = new ScheduledSession(ALICE, ALICE_MONDAY_MORNING.getDay(),
                ALICE_MONDAY_MORNING.getTimeSlot());
        book.addScheduledSession(duplicate);

        book.removeScheduledSession(ALICE_MONDAY_MORNING);

        // the first copy is removed, so the remaining copy is last
        assertEquals(List.of(ALICE_WEDNESDAY, BENSON_MONDAY_OVERLAP, duplicate),
                book.getScheduleList());
    }

    @Test
    public void copy_afterSessionRemoved_equalsOriginal() {
        AddressBook book = getTypicalAddressBookWithSchedule();
        book.removeScheduledSession(ALICE_WEDNESDAY);

        assertEquals(book, book.copy());
    }

    @Test
    public void readOnlyAddressBook_defaultListsAreEmpty() {
        AddressBookStub stub = new AddressBookStub(List.of(ALICE));

        assertEquals(List.of(), stub.getScheduleList());
        assertEquals(List.of(), stub.getParentContactList());
    }

    @Test
    public void toStringMethod() {
        String expected = AddressBook.class.getCanonicalName() + "{persons=" + addressBook.getPersonList() + "}";
        assertEquals(expected, addressBook.toString());
    }

    /**
     * A stub ReadOnlyAddressBook whose persons list can violate interface constraints.
     */
    private static class AddressBookStub implements ReadOnlyAddressBook {
        private final ObservableList<Person> persons = FXCollections.observableArrayList();

        AddressBookStub(Collection<Person> persons) {
            this.persons.setAll(persons);
        }

        @Override
        public ObservableList<Person> getPersonList() {
            return persons;
        }
    }

}
