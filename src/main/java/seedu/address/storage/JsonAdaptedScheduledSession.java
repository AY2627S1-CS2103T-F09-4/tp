package seedu.address.storage;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

/**
 * Jackson-friendly version of {@link ScheduledSession}.
 * Students are referenced by position in the saved {@code persons} list. Sessions are saved with
 * {@code studentIndexes}; files written before group sessions use a single {@code studentIndex} and still load.
 */
class JsonAdaptedScheduledSession {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Scheduled session's %s field is missing!";
    public static final String INVALID_STUDENT_INDEX_MESSAGE =
            "Scheduled session refers to a student that does not exist.";
    public static final String NO_STUDENTS_MESSAGE = "Scheduled session has no students.";
    public static final String DUPLICATE_STUDENT_INDEX_MESSAGE = "Scheduled session lists a student more than once.";
    public static final String INVALID_DAY_MESSAGE = "Scheduled session's day is invalid.";

    private final Integer studentIndex;
    private final List<Integer> studentIndexes;
    private final String day;
    private final String from;
    private final String to;
    private final String sessionId;

    /**
     * Constructs a {@code JsonAdaptedScheduledSession} with the given details.
     * {@code studentIndexes} is used when present; otherwise the legacy {@code studentIndex} is used.
     */
    @JsonCreator
    public JsonAdaptedScheduledSession(@JsonProperty("studentIndex") Integer studentIndex,
            @JsonProperty("studentIndexes") List<Integer> studentIndexes,
            @JsonProperty("day") String day, @JsonProperty("from") String from, @JsonProperty("to") String to,
            @JsonProperty("sessionId") String sessionId) {
        this.studentIndex = studentIndex;
        this.studentIndexes = studentIndexes == null ? null : new ArrayList<>(studentIndexes);
        this.day = day;
        this.from = from;
        this.to = to;
        this.sessionId = sessionId;
    }

    /** Compatibility constructor for one-student schedules written before group sessions. */
    public JsonAdaptedScheduledSession(Integer studentIndex, String day, String from, String to, String sessionId) {
        this(studentIndex, null, day, from, to, sessionId);
    }

    /** Compatibility constructor for schedules written before session identities. */
    public JsonAdaptedScheduledSession(Integer studentIndex, String day, String from, String to) {
        this(studentIndex, null, day, from, to, null);
    }

    /**
     * Converts a given {@code ScheduledSession} into this class for Jackson use.
     *
     * @param source The session to save.
     * @param persons The persons list being saved alongside this session.
     */
    public JsonAdaptedScheduledSession(ScheduledSession source, List<Person> persons) {
        sessionId = source.getSessionId();
        studentIndex = null;
        studentIndexes = source.getStudents().stream().map(persons::indexOf).toList();
        day = source.getDay().name();
        from = source.getTimeSlot().getStartString();
        to = source.getTimeSlot().getEndString();
    }

    @JsonProperty("studentIndex")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getStudentIndex() {
        return studentIndex;
    }

    @JsonProperty("studentIndexes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<Integer> getStudentIndexes() {
        return studentIndexes;
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
     * @param persons The loaded persons list that the student indexes refer to.
     * @throws IllegalValueException if any field is missing or invalid.
     */
    public ScheduledSession toModelType(List<Person> persons) throws IllegalValueException {
        List<Person> students = toStudents(persons);
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
                    ? new ScheduledSession(students, modelDay, TimeSlot.of(from, to))
                    : new ScheduledSession(sessionId, students, modelDay, TimeSlot.of(from, to));
        } catch (IllegalArgumentException e) {
            throw new IllegalValueException("Scheduled session ID must be a UUID.");
        }
    }

    /** Returns the students that the saved indexes refer to, checking that each index is valid and distinct. */
    private List<Person> toStudents(List<Person> persons) throws IllegalValueException {
        List<Integer> indexes;
        if (studentIndexes != null) {
            indexes = studentIndexes;
        } else if (studentIndex != null) {
            indexes = List.of(studentIndex);
        } else {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "studentIndexes"));
        }
        if (indexes.isEmpty()) {
            throw new IllegalValueException(NO_STUDENTS_MESSAGE);
        }

        List<Person> students = new ArrayList<>();
        for (Integer index : indexes) {
            if (index == null || index < 0 || index >= persons.size()) {
                throw new IllegalValueException(INVALID_STUDENT_INDEX_MESSAGE);
            }
            Person student = persons.get(index);
            if (students.contains(student)) {
                throw new IllegalValueException(DUPLICATE_STUDENT_INDEX_MESSAGE);
            }
            students.add(student);
        }
        return students;
    }
}
