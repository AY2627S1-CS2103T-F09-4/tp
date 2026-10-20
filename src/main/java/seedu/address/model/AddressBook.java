package seedu.address.model;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.model.person.UniquePersonList;
import seedu.address.model.schedule.Schedule;
import seedu.address.model.schedule.ScheduledSession;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final javafx.collections.ObservableList<ParentContact> parentContacts = FXCollections.observableArrayList();
    private final Schedule schedule = new Schedule();

    public AddressBook() {}

    /**
     * Creates an AddressBook using the Persons in the {@code toBeCopied}
     */
    public AddressBook(ReadOnlyAddressBook toBeCopied) {
        this();
        resetData(toBeCopied);
    }

    //// list overwrite operations

    /**
     * Replaces the contents of the person list with {@code persons}.
     * {@code persons} must not contain duplicate persons.
     */
    public void setPersons(List<Person> persons) {
        this.persons.setPersons(persons);
    }

    /**
     * Resets the existing data of this {@code AddressBook} with {@code newData}.
     */
    public void resetData(ReadOnlyAddressBook newData) {
        requireNonNull(newData);

        setPersons(newData.getPersonList());
        parentContacts.setAll(newData.getParentContactList());
        schedule.setSessions(newData.getScheduleList());
    }

    //// person-level operations

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return persons.contains(person);
    }

    /**
     * Adds a person to the address book.
     * The person must not already exist in the address book.
     */
    public void addPerson(Person p) {
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        persons.setPerson(target, editedPerson);
        List<ScheduledSession> updated = schedule.asUnmodifiableObservableList().stream()
                .map(s -> s.getStudent().equals(target) ? s.withStudent(editedPerson) : s)
                .toList();
        schedule.setSessions(updated);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        schedule.removeIf(s -> s.getStudent().equals(key));
    }

    //// schedule-level operations

    /**
     * Adds a session to the schedule. Overlapping sessions are allowed.
     * The session's student must exist in the address book.
     */
    public void addScheduledSession(ScheduledSession session) {
        requireNonNull(session);
        schedule.add(session);
    }

    /**
     * Returns true if a session equal to {@code session} is in the schedule.
     */
    public boolean hasScheduledSession(ScheduledSession session) {
        requireNonNull(session);
        return schedule.contains(session);
    }

    /**
     * Removes one session equal to {@code session} from the schedule.
     * At least one such session must exist.
     */
    public void removeScheduledSession(ScheduledSession session) {
        requireNonNull(session);
        if (!schedule.remove(session)) {
            throw new IllegalArgumentException("Session is not in the schedule.");
        }
    }

    /**
     * Adds a parent contact to the address book.
     */
    public void addParentContact(ParentContact contact) {
        requireNonNull(contact);
        if (parentContacts.stream().anyMatch(contact::isSameContact)) {
            throw new seedu.address.model.person.exceptions.DuplicatePersonException();
        }
        parentContacts.add(contact);
    }

    public boolean hasParentContact(ParentContact contact) {
        return parentContacts.stream().anyMatch(contact::isSameContact);
    }

    public void setParentContact(ParentContact target, ParentContact replacement) {
        int index = parentContacts.indexOf(target);
        if (index < 0) {
            throw new seedu.address.model.person.exceptions.PersonNotFoundException();
        }
        if (!target.isSameContact(replacement) && hasParentContact(replacement)) {
            throw new seedu.address.model.person.exceptions.DuplicatePersonException();
        }
        parentContacts.set(index, replacement);
    }

    public void removeParentContact(ParentContact contact) {
        parentContacts.remove(contact);
    }

    /**
     * Creates a copy of the address book by copying every {@code Person} and {@code ParentContact}.
     *
     * @return the copied AddressBook.
     */
    public AddressBook copy() {
        AddressBook copy = new AddressBook();

        for (Person student : persons) {
            copy.addPerson(student.copy());
        }
        for (ParentContact parent : parentContacts) {
            copy.addParentContact(parent.copy());
        }
        for (ScheduledSession session : schedule.asUnmodifiableObservableList()) {
            Person studentCopy = copy.persons.asUnmodifiableObservableList().stream()
                    .filter(p -> p.equals(session.getStudent()))
                    .findFirst()
                    .orElseThrow();
            copy.addScheduledSession(session.withStudent(studentCopy));
        }

        return copy;
    }

    //// util methods

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("persons", persons)
                .toString();
    }

    @Override
    public ObservableList<Person> getPersonList() {
        return persons.asUnmodifiableObservableList();
    }

    @Override
    public ObservableList<ParentContact> getParentContactList() {
        return FXCollections.unmodifiableObservableList(parentContacts);
    }

    @Override
    public ObservableList<ScheduledSession> getScheduleList() {
        return schedule.asUnmodifiableObservableList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddressBook otherAddressBook)) {
            return false;
        }

        return persons.equals(otherAddressBook.persons)
                && parentContacts.equals(otherAddressBook.parentContacts)
                && schedule.equals(otherAddressBook.schedule);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, parentContacts, schedule);
    }
}
