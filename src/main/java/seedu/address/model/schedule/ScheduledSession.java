package seedu.address.model.schedule;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.util.Objects;

import seedu.address.model.person.Person;

/**
 * Represents a student scheduled on a day and time slot.
 * Guarantees: immutable.
 */
public class ScheduledSession {

    private final Person student;
    private final DayOfWeek day;
    private final TimeSlot timeSlot;

    /**
     * Constructs a {@code ScheduledSession}. Every field must be non-null.
     */
    public ScheduledSession(Person student, DayOfWeek day, TimeSlot timeSlot) {
        requireAllNonNull(student, day, timeSlot);
        this.student = student;
        this.day = day;
        this.timeSlot = timeSlot;
    }

    public Person getStudent() {
        return student;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /** Returns a copy of this session that refers to {@code newStudent}. */
    public ScheduledSession withStudent(Person newStudent) {
        return new ScheduledSession(newStudent, day, timeSlot);
    }

    @Override
    public String toString() {
        return student.getName() + " on " + day + " " + timeSlot;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ScheduledSession otherSession)) {
            return false;
        }
        return student.equals(otherSession.student)
                && day == otherSession.day
                && timeSlot.equals(otherSession.timeSlot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(student, day, timeSlot);
    }
}
