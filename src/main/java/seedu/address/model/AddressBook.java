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
import seedu.address.model.reminder.Reminder;
import seedu.address.model.reminder.ReminderList;
import seedu.address.model.schedule.Schedule;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.statistics.AssessmentRecord;
import seedu.address.model.statistics.LessonRecord;

/**
 * Wraps all data at the address-book level.
 * Duplicates are not allowed (by .isSamePerson comparison).
 */
public class AddressBook implements ReadOnlyAddressBook {

    private final UniquePersonList persons = new UniquePersonList();
    private final javafx.collections.ObservableList<ParentContact> parentContacts = FXCollections.observableArrayList();
    private final Schedule schedule = new Schedule();
    private ReminderList reminders = new ReminderList();
    private int nextReminderId = 1;
    private List<LessonRecord> lessons = List.of();
    private List<AssessmentRecord> assessments = List.of();

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
        reminders = new ReminderList(newData.getReminderList());
        nextReminderId = newData.getNextReminderId();
        setHistory(newData.getLessonRecords(), newData.getAssessmentRecords());
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
        if (getPersonList().stream().anyMatch(person -> person.getStudentId().equals(p.getStudentId()))) {
            throw new IllegalArgumentException("Student IDs must be unique.");
        }
        persons.add(p);
    }

    /**
     * Replaces the given person {@code target} in the list with {@code editedPerson}.
     * {@code target} must exist in the address book.
     * The person identity of {@code editedPerson} must not be the same as another existing person in the address book.
     */
    public void setPerson(Person target, Person editedPerson) {
        requireNonNull(editedPerson);

        Person identifiedPerson = editedPerson.withStudentId(target.getStudentId());
        persons.setPerson(target, identifiedPerson);
        List<ScheduledSession> updated = schedule.asUnmodifiableObservableList().stream()
                .map(s -> s.withStudentReplaced(target, identifiedPerson))
                .toList();
        schedule.setSessions(updated);
    }

    /**
     * Removes {@code key} from this {@code AddressBook}.
     * {@code key} must exist in the address book.
     * {@code key} is also removed from every group session, and sessions left with no students are deleted.
     */
    public void removePerson(Person key) {
        persons.remove(key);
        List<ScheduledSession> updated = schedule.asUnmodifiableObservableList().stream()
                .filter(s -> !s.hasStudent(key) || s.getStudents().size() > 1)
                .map(s -> s.hasStudent(key) ? s.withStudents(s.getStudentsWithout(key)) : s)
                .toList();
        schedule.setSessions(updated);
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
     * Replaces {@code target} in the schedule with {@code editedSession}, which must have the same session ID.
     * {@code target} must be in the schedule.
     */
    public void setScheduledSession(ScheduledSession target, ScheduledSession editedSession) {
        requireNonNull(target);
        requireNonNull(editedSession);
        schedule.setSession(target, editedSession);
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

    /** Adds an already identified reminder, primarily while loading or copying data. */
    public void addReminder(Reminder reminder) {
        reminders = reminders.withReminder(reminder);
        nextReminderId = Math.max(nextReminderId, reminder.getId() + 1);
    }

    /** Creates and adds a reminder with the next never-reused ID. */
    public Reminder createReminder(String text, java.time.LocalDateTime dueAt) {
        Reminder reminder = new Reminder(nextReminderId, text, dueAt);
        addReminder(reminder);
        return reminder;
    }

    public void removeReminder(int id) {
        reminders = reminders.withoutReminderId(id);
    }

    /** Replaces reminders and ID allocation state when loading persisted data. */
    public void setReminderData(List<Reminder> reminders, int nextReminderId) {
        if (nextReminderId <= 0 || reminders.stream().anyMatch(reminder -> reminder.getId() >= nextReminderId)) {
            throw new IllegalArgumentException("Next reminder ID must exceed all reminder IDs.");
        }
        this.reminders = new ReminderList(reminders);
        this.nextReminderId = nextReminderId;
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
            List<Person> studentCopies = session.getStudents().stream()
                    .map(student -> copy.persons.asUnmodifiableObservableList().stream()
                            .filter(p -> p.equals(student))
                            .findFirst()
                            .orElseThrow())
                    .toList();
            copy.addScheduledSession(session.withStudents(studentCopies));
        }
        for (Reminder reminder : reminders.getReminders()) {
            copy.addReminder(reminder.copy());
        }
        copy.nextReminderId = nextReminderId;
        copy.setHistory(lessons, assessments);

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
    public List<Reminder> getReminderList() {
        return reminders.getReminders();
    }

    @Override
    public int getNextReminderId() {
        return nextReminderId;
    }

    @Override
    public List<LessonRecord> getLessonRecords() {
        return lessons;
    }

    @Override
    public List<AssessmentRecord> getAssessmentRecords() {
        return assessments;
    }

    /** Replaces source history atomically; immutable records are safe to share between snapshots. */
    public void setHistory(List<LessonRecord> lessons, List<AssessmentRecord> assessments) {
        List<LessonRecord> newLessons = List.copyOf(lessons);
        List<AssessmentRecord> newAssessments = List.copyOf(assessments);
        if (newLessons.stream().map(LessonRecord::id).distinct().count() != newLessons.size()
                || newAssessments.stream().map(AssessmentRecord::id).distinct().count() != newAssessments.size()) {
            throw new IllegalArgumentException("Historical record IDs must be unique within each collection.");
        }
        // A group session links one lesson per student, so a linked occurrence is identified by session, student
        // and date. Membership is not re-checked here: a student who left a group keeps their earlier lessons.
        List<LessonRecord> linked = newLessons.stream().filter(r -> r.sourceSessionId() != null).toList();
        if (linked.stream().map(r -> r.sourceSessionId() + "/" + r.studentId() + "/" + r.date()).distinct().count()
                != linked.size()) {
            throw new IllegalArgumentException(
                    "Only one linked lesson is allowed per student, scheduled session and date.");
        }
        this.lessons = newLessons;
        this.assessments = newAssessments;
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
                && schedule.equals(otherAddressBook.schedule)
                && reminders.equals(otherAddressBook.reminders)
                && nextReminderId == otherAddressBook.nextReminderId
                && lessons.equals(otherAddressBook.lessons)
                && assessments.equals(otherAddressBook.assessments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(persons, parentContacts, schedule, reminders, nextReminderId, lessons, assessments);
    }
}
