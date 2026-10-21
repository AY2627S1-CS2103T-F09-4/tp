package seedu.address.storage;

import java.time.DayOfWeek;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

/**
 * Jackson-friendly version of {@link ScheduledSession}.
 * The student is referenced by position in the saved {@code persons} list.
 */
class JsonAdaptedScheduledSession {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Scheduled session's %s field is missing!";
    public static final String INVALID_STUDENT_INDEX_MESSAGE =
            "Scheduled session refers to a student that does not exist.";
    public static final String INVALID_DAY_MESSAGE = "Scheduled session's day is invalid.";

    private final Integer studentIndex;
    private final String day;
    private final String from;
    private final String to;
    private final String sessionId;

    /**
     * Constructs a {@code JsonAdaptedScheduledSession} with the given details.
     */
    @JsonCreator
    public JsonAdaptedScheduledSession(@JsonProperty("studentIndex") Integer studentIndex,
            @JsonProperty("day") String day, @JsonProperty("from") String from, @JsonProperty("to") String to,
            @JsonProperty("sessionId") String sessionId) {
        this.studentIndex = studentIndex;
        this.day = day;
        this.from = from;
        this.to = to;
        this.sessionId = sessionId;
    }

    /** Compatibility constructor for schedules written before session identities. */
    public JsonAdaptedScheduledSession(Integer studentIndex, String day, String from, String to) {
        this(studentIndex, day, from, to, null);
    }

    /**
     * Converts a given {@code ScheduledSession} into this class for Jackson use.
     *
     * @param persons the persons list being saved alongside this session.
     */
    public JsonAdaptedScheduledSession(ScheduledSession source, List<Person> persons) {
        sessionId = source.getSessionId();
        studentIndex = persons.indexOf(source.getStudent());
        day = source.getDay().name();
        from = source.getTimeSlot().getStartString();
        to = source.getTimeSlot().getEndString();
    }

    @JsonProperty("studentIndex")
    public Integer getStudentIndex() {
        return studentIndex;
    }

    @JsonProperty("day")
    public String getDay() {
        return day;
    }

    @JsonProperty("from")
    public String getFrom() {
        return from;
    }

    @JsonProperty("to")
    public String getTo() {
        return to;
    }

    /**
     * Converts this adapted session into the model's {@code ScheduledSession}.
     *
     * @param persons the loaded persons list that {@code studentIndex} refers to.
     * @throws IllegalValueException if any field is missing or invalid.
     */
    public ScheduledSession toModelType(List<Person> persons) throws IllegalValueException {
        if (studentIndex == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "studentIndex"));
        }
        if (studentIndex < 0 || studentIndex >= persons.size()) {
            throw new IllegalValueException(INVALID_STUDENT_INDEX_MESSAGE);
        }
        if (day == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "day"));
        }
        if (from == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "from"));
        }
        if (to == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "to"));
        }

        DayOfWeek modelDay;
        try {
            modelDay = DayOfWeek.valueOf(day);
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException(INVALID_DAY_MESSAGE);
        }
        if (!TimeSlot.isValidTimeSlot(from, to)) {
            throw new IllegalValueException(TimeSlot.MESSAGE_CONSTRAINTS);
        }

        try {
            return sessionId == null
                    ? new ScheduledSession(persons.get(studentIndex), modelDay, TimeSlot.of(from, to))
                    : new ScheduledSession(sessionId, persons.get(studentIndex), modelDay, TimeSlot.of(from, to));
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException("Scheduled session ID must be a UUID.");
        }
    }
}
