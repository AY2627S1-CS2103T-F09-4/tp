package seedu.address.model;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

import javafx.collections.ObservableList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.statistics.AssessmentRecord;
import seedu.address.model.statistics.LessonRecord;

/**
 * The API of the Model component.
 */
public interface Model {
    /** {@code Predicate} that always evaluates to true */
    Predicate<Person> PREDICATE_SHOW_ALL_PERSONS = unused -> true;
    Predicate<ParentContact> PREDICATE_SHOW_ALL_PARENTS = unused -> true;

    /** Replaces historical source records. */
    default void setHistory(List<LessonRecord> lessons, List<AssessmentRecord> assessments) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the user prefs.
     */
    ReadOnlyUserPrefs getUserPrefs();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Sets the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Replaces address book data with the data in {@code addressBook}.
     */
    void setAddressBook(ReadOnlyAddressBook addressBook);

    /** Returns the AddressBook */
    ReadOnlyAddressBook getAddressBook();

    /**
     * Returns true if a person with the same identity as {@code person} exists in the address book.
     */
    boolean hasPerson(Person person);

    /**
     * Deletes the given person.
     * The person must exist in the address book.
     */
    void deletePerson(Person target);

    /**
     * Adds the given person.
     * {@code person} must not already exist in the address book.
     */
    void addPerson(Person person);

    /**
     * Replaces the given person {@code target} with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    void setPerson(Person target, Person editedPerson);

    /** Returns an unmodifiable view of the filtered person list. */
    ObservableList<Person> getFilteredPersonList();

    /** Returns an unmodifiable view of the person list. */
    ObservableList<Person> getPersonList();

    /**
     * Updates the filter of the filtered person list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateFilteredPersonList(Predicate<Person> predicate);

    default void addParentContact(ParentContact contact) {
        getAddressBook();
    }
    default boolean hasParentContact(ParentContact contact) {
        return getAddressBook().getParentContactList().contains(contact);
    }
    default void setParentContact(ParentContact target, ParentContact replacement) {
        getAddressBook();
    }
    default void deleteParentContact(ParentContact target) {
        getAddressBook();
    }
    default javafx.collections.ObservableList<ParentContact> getParentContactList() {
        return getAddressBook().getParentContactList();
    }

    /**
     * Updates the filter of the filtered parent list to filter by the given {@code predicate}.
     * @throws NullPointerException if {@code predicate} is null.
     */
    void updateParentList(Predicate<ParentContact> predicate);

    /**
     * Adds {@code session} to the schedule. Overlapping sessions are allowed.
     * The session's student must exist in the address book.
     */
    void addScheduledSession(ScheduledSession session);

    /** Returns true if a session equal to {@code session} is in the schedule. */
    boolean hasScheduledSession(ScheduledSession session);

    /**
     * Removes one session equal to {@code session} from the schedule.
     * At least one such session must exist.
     */
    void deleteScheduledSession(ScheduledSession session);

    /** Returns an unmodifiable view of the scheduled sessions. */
    ObservableList<ScheduledSession> getScheduleList();

    default Reminder addReminder(String text, LocalDateTime dueAt) {
        throw new UnsupportedOperationException();
    }

    default void deleteReminder(int id) {
        throw new UnsupportedOperationException();
    }

    default java.util.List<Reminder> getReminderList() {
        return java.util.List.of();
    }

    default Clock getClock() {
        return Clock.systemDefaultZone();
    }

    /** Snapshots the current address book contents. */
    void save();

    /** Replaces address book data with the data in the previous snapshot. */
    void undo();

    /** Replaces address book data with the data in the previous snapshot. */
    void redo();
}
