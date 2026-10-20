package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.storage.SnapshotManager;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final FilteredList<ParentContact> parentList;
    private final SnapshotManager snapshotManager;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        this.filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        this.parentList = new FilteredList<>(this.addressBook.getParentContactList());
        this.snapshotManager = new SnapshotManager();
        snapshotManager.saveSnapshot(this.addressBook);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    /**
     * Returns an unmodifiable view of the unfiltered list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getPersonList() {
        return this.addressBook.getPersonList();
    }

    @Override public void addParentContact(ParentContact contact) {
        addressBook.addParentContact(contact); }
    @Override public boolean hasParentContact(ParentContact contact) {
        return addressBook.hasParentContact(contact); }
    @Override public void setParentContact(ParentContact target, ParentContact replacement) {
        addressBook.setParentContact(target, replacement);
    }
    @Override public void deleteParentContact(ParentContact target) {
        addressBook.removeParentContact(target); }

    @Override public ObservableList<ParentContact> getParentContactList() {
        return parentList; }

    @Override
    public void updateParentList(Predicate<ParentContact> predicate) {
        requireNonNull(predicate);
        parentList.setPredicate(predicate);
    }

    @Override
    public void addScheduledSession(ScheduledSession session) {
        requireNonNull(session);
        addressBook.addScheduledSession(session);
    }

    @Override
    public boolean hasScheduledSession(ScheduledSession session) {
        requireNonNull(session);
        return addressBook.hasScheduledSession(session);
    }

    @Override
    public void deleteScheduledSession(ScheduledSession session) {
        requireNonNull(session);
        addressBook.removeScheduledSession(session);
    }

    @Override
    public ObservableList<ScheduledSession> getScheduleList() {
        return addressBook.getScheduleList();
    }

    @Override
    public void addReminder(Reminder reminder) {
        requireNonNull(reminder);
        addressBook.addReminder(reminder);
    }

    @Override
    public void deleteReminder(seedu.address.commons.core.index.Index index) {
        requireNonNull(index);
        addressBook.removeReminder(index);
    }

    @Override
    public java.util.List<Reminder> getReminderList() {
        return addressBook.getReminderList();
    }

    @Override
    public void save() {
        snapshotManager.saveSnapshot(addressBook);
    }

    @Override
    public void undo() {
        AddressBook newAddressBook = snapshotManager.undo();

        if (newAddressBook != null) {
            this.setAddressBook(newAddressBook);
        }
    }

    @Override
    public void redo() {
        AddressBook newAddressBook = snapshotManager.redo();

        if (newAddressBook != null) {
            this.setAddressBook(newAddressBook);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
