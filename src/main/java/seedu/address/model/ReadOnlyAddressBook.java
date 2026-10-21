package seedu.address.model;

import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.statistics.AssessmentRecord;
import seedu.address.model.statistics.LessonRecord;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /** Returns immutable dated lesson history, independent of the weekly schedule. */
    default List<LessonRecord> getLessonRecords() {
        return List.of();
    }

    /** Returns immutable assessment history. */
    default List<AssessmentRecord> getAssessmentRecords() {
        return List.of();
    }

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    ObservableList<Person> getPersonList();

    default ObservableList<ParentContact> getParentContactList() {
        return FXCollections.observableArrayList();
    }

    /** Returns an unmodifiable view of the scheduled sessions. */
    default ObservableList<ScheduledSession> getScheduleList() {
        return FXCollections.observableArrayList();
    }

    /** Returns the tutor-level reminders in insertion order. */
    default List<Reminder> getReminderList() {
        return List.of();
    }

    /** Returns the next stable reminder ID. */
    default int getNextReminderId() {
        return 1;
    }

}
