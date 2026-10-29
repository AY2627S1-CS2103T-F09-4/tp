package seedu.address.testutil;

import java.time.DayOfWeek;
import java.util.List;
import java.util.UUID;

import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

/**
 * A utility class to help with building ScheduledSession objects.
 */
public class ScheduledSessionBuilder {

    public static final DayOfWeek DEFAULT_DAY = DayOfWeek.MONDAY;
    public static final String DEFAULT_FROM = "10-00";
    public static final String DEFAULT_TO = "12-00";

    private List<Person> students;
    private String sessionId = UUID.randomUUID().toString();
    private DayOfWeek day;
    private TimeSlot timeSlot;

    /**
     * Creates a builder with a default student, day and time slot.
     */
    public ScheduledSessionBuilder() {
        students = List.of(new PersonBuilder().build());
        day = DEFAULT_DAY;
        timeSlot = TimeSlot.of(DEFAULT_FROM, DEFAULT_TO);
    }

    /**
     * Initializes the builder with the data of {@code sessionToCopy}.
     */
    public ScheduledSessionBuilder(ScheduledSession sessionToCopy) {
        sessionId = sessionToCopy.getSessionId();
        students = sessionToCopy.getStudents();
        day = sessionToCopy.getDay();
        timeSlot = sessionToCopy.getTimeSlot();
    }

    /**
     * Sets the single student of the {@code ScheduledSession} that we are building.
     */
    public ScheduledSessionBuilder withStudent(Person student) {
        this.students = List.of(student);
        return this;
    }

    /**
     * Sets the students of the group {@code ScheduledSession} that we are building.
     */
    public ScheduledSessionBuilder withStudents(Person... students) {
        this.students = List.of(students);
        return this;
    }

    /**
     * Sets the day of the {@code ScheduledSession} that we are building.
     */
    public ScheduledSessionBuilder withDay(DayOfWeek day) {
        this.day = day;
        return this;
    }

    /**
     * Sets the time slot from two HH-mm strings.
     */
    public ScheduledSessionBuilder withTimeSlot(String from, String to) {
        this.timeSlot = TimeSlot.of(from, to);
        return this;
    }

    public ScheduledSession build() {
        return new ScheduledSession(sessionId, students, day, timeSlot);
    }
}
