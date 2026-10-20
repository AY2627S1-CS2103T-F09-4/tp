package seedu.address.testutil;

import java.time.DayOfWeek;

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

    private Person student;
    private DayOfWeek day;
    private TimeSlot timeSlot;

    /**
     * Creates a builder with a default student, day and time slot.
     */
    public ScheduledSessionBuilder() {
        student = new PersonBuilder().build();
        day = DEFAULT_DAY;
        timeSlot = TimeSlot.of(DEFAULT_FROM, DEFAULT_TO);
    }

    /**
     * Initializes the builder with the data of {@code sessionToCopy}.
     */
    public ScheduledSessionBuilder(ScheduledSession sessionToCopy) {
        student = sessionToCopy.getStudent();
        day = sessionToCopy.getDay();
        timeSlot = sessionToCopy.getTimeSlot();
    }

    /**
     * Sets the student of the {@code ScheduledSession} that we are building.
     */
    public ScheduledSessionBuilder withStudent(Person student) {
        this.student = student;
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
        return new ScheduledSession(student, day, timeSlot);
    }
}
