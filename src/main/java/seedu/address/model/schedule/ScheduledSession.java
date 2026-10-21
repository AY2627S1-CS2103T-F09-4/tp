package seedu.address.model.schedule;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.util.Objects;
import java.util.UUID;

import seedu.address.model.person.Person;

/**
 * Represents a student scheduled on a day and time slot.
 * Guarantees: immutable.
 */
public class ScheduledSession {

    private final String sessionId;
    private final Person student;
    private final DayOfWeek day;
    private final TimeSlot timeSlot;

    /**
     * Constructs a {@code ScheduledSession}. Every field must be non-null.
     */
    public ScheduledSession(Person student, DayOfWeek day, TimeSlot timeSlot) {
        this(UUID.randomUUID().toString(), student, day, timeSlot);
    }

    /** Restores a recurring session with its persistent identity. */
    public ScheduledSession(String sessionId, Person student, DayOfWeek day, TimeSlot timeSlot) {
        requireAllNonNull(student, day, timeSlot);
        this.sessionId = UUID.fromString(sessionId).toString();
        this.student = student;
        this.day = day;
        this.timeSlot = timeSlot;
    }

    public Person getStudent() {
        return student;
    }

    public String getSessionId() {
        return sessionId;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    /** Returns a copy of this session that refers to {@code newStudent}. */
    public ScheduledSession withStudent(Person newStudent) {
        return new ScheduledSession(sessionId, newStudent, day, timeSlot);
    }

    /** Matches the existing day/time/student syntax without confusing slot matching with identity. */
    public boolean isSameSlot(ScheduledSession other) {
        return other != null && student.equals(other.student) && day == other.day && timeSlot.equals(other.timeSlot);
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
        return sessionId.equals(otherSession.sessionId) && isSameSlot(otherSession);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, student, day, timeSlot);
    }
}
