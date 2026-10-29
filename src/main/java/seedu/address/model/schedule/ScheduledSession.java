package seedu.address.model.schedule;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import seedu.address.model.person.Person;

/**
 * Represents one or more students scheduled together on a day and time slot.
 * Guarantees: immutable; has at least one student; no student appears twice.
 */
public class ScheduledSession {

    public static final String MESSAGE_NO_STUDENTS = "A scheduled session must have at least one student.";
    public static final String MESSAGE_DUPLICATE_STUDENTS = "A student cannot appear twice in the same session.";

    private final String sessionId;
    private final List<Person> students;
    private final DayOfWeek day;
    private final TimeSlot timeSlot;

    /**
     * Constructs a one-student {@code ScheduledSession}. Every field must be non-null.
     */
    public ScheduledSession(Person student, DayOfWeek day, TimeSlot timeSlot) {
        this(UUID.randomUUID().toString(), student, day, timeSlot);
    }

    /** Restores a one-student recurring session with its persistent identity. */
    public ScheduledSession(String sessionId, Person student, DayOfWeek day, TimeSlot timeSlot) {
        this(sessionId, List.of(requireNonNull(student)), day, timeSlot);
    }

    /**
     * Constructs a {@code ScheduledSession} for a group of students. Every field must be non-null.
     */
    public ScheduledSession(List<Person> students, DayOfWeek day, TimeSlot timeSlot) {
        this(UUID.randomUUID().toString(), students, day, timeSlot);
    }

    /**
     * Restores a recurring session for a group of students with its persistent identity.
     *
     * @throws IllegalArgumentException if {@code students} is empty or has the same student twice.
     */
    public ScheduledSession(String sessionId, List<Person> students, DayOfWeek day, TimeSlot timeSlot) {
        requireAllNonNull(sessionId, students, day, timeSlot);
        requireAllNonNull(students);
        checkArgument(!students.isEmpty(), MESSAGE_NO_STUDENTS);
        checkArgument(hasDistinctStudents(students), MESSAGE_DUPLICATE_STUDENTS);
        this.sessionId = UUID.fromString(sessionId).toString();
        this.students = List.copyOf(students);
        this.day = day;
        this.timeSlot = timeSlot;
    }

    private static boolean hasDistinctStudents(List<Person> students) {
        return students.stream().map(Person::getStudentId).distinct().count() == students.size();
    }

    /** Returns an unmodifiable list of the students in this session, in the order they were scheduled. */
    public List<Person> getStudents() {
        return students;
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

    /** Returns true if {@code student} is in this session. */
    public boolean hasStudent(Person student) {
        requireNonNull(student);
        return students.contains(student);
    }

    /** Returns true if this session is on {@code otherDay} at exactly {@code otherTimeSlot}. */
    public boolean isAt(DayOfWeek otherDay, TimeSlot otherTimeSlot) {
        requireAllNonNull(otherDay, otherTimeSlot);
        return day == otherDay && timeSlot.equals(otherTimeSlot);
    }

    /** Returns a copy of this session, with the same identity, that holds {@code newStudents}. */
    public ScheduledSession withStudents(List<Person> newStudents) {
        return new ScheduledSession(sessionId, newStudents, day, timeSlot);
    }

    /**
     * Returns a copy of this session in which {@code target} is replaced by {@code replacement}.
     * Returns this session unchanged if {@code target} is not in it.
     */
    public ScheduledSession withStudentReplaced(Person target, Person replacement) {
        requireAllNonNull(target, replacement);
        if (!hasStudent(target)) {
            return this;
        }
        return withStudents(students.stream()
                .map(student -> student.equals(target) ? replacement : student)
                .toList());
    }

    /**
     * Returns the students that would remain in this session after removing {@code student}.
     * The result is empty if {@code student} was the only one.
     *
     * @throws IllegalArgumentException if {@code student} is not in this session.
     */
    public List<Person> getStudentsWithout(Person student) {
        checkArgument(hasStudent(student), "Student is not in this session.");
        List<Person> remainingStudents = new ArrayList<>(students);
        remainingStudents.remove(student);
        return List.copyOf(remainingStudents);
    }

    /** Matches the existing day/time/students syntax without confusing slot matching with identity. */
    public boolean isSameSlot(ScheduledSession other) {
        return other != null && students.equals(other.students) && isAt(other.day, other.timeSlot);
    }

    @Override
    public String toString() {
        String names = students.stream()
                .map(student -> student.getName().toString())
                .collect(Collectors.joining(", "));
        return names + " on " + day + " " + timeSlot;
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
        return Objects.hash(sessionId, students, day, timeSlot);
    }
}
