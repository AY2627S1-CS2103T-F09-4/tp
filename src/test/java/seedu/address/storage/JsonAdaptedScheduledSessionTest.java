package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;
import seedu.address.testutil.ScheduledSessionBuilder;

public class JsonAdaptedScheduledSessionTest {

    private static final List<Person> PERSONS = List.of(ALICE, BENSON);

    @Test
    public void toModelType_validSession_returnsSession() throws Exception {
        JsonAdaptedScheduledSession adapted = new JsonAdaptedScheduledSession(1, "TUESDAY", "09-30", "11-00");

        ScheduledSession expected = new ScheduledSessionBuilder().withStudent(BENSON).withDay(DayOfWeek.TUESDAY)
                .withTimeSlot("09-30", "11-00").build();
        assertTrue(expected.isSameSlot(adapted.toModelType(PERSONS)));
    }

    @Test
    public void conversion_roundTrip_preservesSession() throws Exception {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(BENSON).withDay(DayOfWeek.SUNDAY)
                .withTimeSlot("08-15", "23-45").build();

        JsonAdaptedScheduledSession adapted = new JsonAdaptedScheduledSession(session, PERSONS);

        assertNull(adapted.getStudentIndex());
        assertEquals(List.of(1), adapted.getStudentIndexes());
        assertEquals("SUNDAY", adapted.getDay());
        assertEquals("08-15", adapted.getFrom());
        assertEquals("23-45", adapted.getTo());
        assertEquals(session, adapted.toModelType(PERSONS));
    }

    @Test
    public void conversion_groupRoundTrip_preservesAllStudentsInOrder() throws Exception {
        ScheduledSession session = new ScheduledSessionBuilder().withStudents(BENSON, ALICE).build();

        JsonAdaptedScheduledSession adapted = new JsonAdaptedScheduledSession(session, PERSONS);

        assertEquals(List.of(1, 0), adapted.getStudentIndexes());
        assertEquals(session, adapted.toModelType(PERSONS));
    }

    @Test
    public void toModelType_studentIndexesTakePrecedenceOverLegacyIndex() throws Exception {
        JsonAdaptedScheduledSession adapted = new JsonAdaptedScheduledSession(0, List.of(1, 0), "MONDAY",
                "10-00", "12-00", null);

        assertEquals(List.of(BENSON, ALICE), adapted.toModelType(PERSONS).getStudents());
    }

    @Test
    public void toModelType_invalidStudentIndexes_throwsIllegalValueException() {
        assertInvalidIndexes(List.of(), JsonAdaptedScheduledSession.NO_STUDENTS_MESSAGE);
        assertInvalidIndexes(List.of(0, 2), JsonAdaptedScheduledSession.INVALID_STUDENT_INDEX_MESSAGE);
        assertInvalidIndexes(Arrays.asList(0, null), JsonAdaptedScheduledSession.INVALID_STUDENT_INDEX_MESSAGE);
        assertInvalidIndexes(List.of(1, 1), JsonAdaptedScheduledSession.DUPLICATE_STUDENT_INDEX_MESSAGE);
    }

    @Test
    public void toModelType_nullFields_throwsIllegalValueException() {
        String format = JsonAdaptedScheduledSession.MISSING_FIELD_MESSAGE_FORMAT;

        assertInvalid(null, "MONDAY", "10-00", "12-00", String.format(format, "studentIndexes"), PERSONS);
        assertInvalid(0, null, "10-00", "12-00", String.format(format, "day"), PERSONS);
        assertInvalid(0, "MONDAY", null, "12-00", String.format(format, "from"), PERSONS);
        assertInvalid(0, "MONDAY", "10-00", null, String.format(format, "to"), PERSONS);
    }

    @Test
    public void toModelType_studentIndexOutOfRange_throwsIllegalValueException() {
        String message = JsonAdaptedScheduledSession.INVALID_STUDENT_INDEX_MESSAGE;

        assertInvalid(-1, "MONDAY", "10-00", "12-00", message, PERSONS);
        assertInvalid(2, "MONDAY", "10-00", "12-00", message, PERSONS);
        assertInvalid(0, "MONDAY", "10-00", "12-00", message, List.of());
    }

    @Test
    public void toModelType_invalidDay_throwsIllegalValueException() {
        String message = JsonAdaptedScheduledSession.INVALID_DAY_MESSAGE;

        assertInvalid(0, "Monday", "10-00", "12-00", message, PERSONS);
        assertInvalid(0, "FUNDAY", "10-00", "12-00", message, PERSONS);
        assertInvalid(0, "", "10-00", "12-00", message, PERSONS);
    }

    @Test
    public void toModelType_invalidTimes_throwsIllegalValueException() {
        String message = TimeSlot.MESSAGE_CONSTRAINTS;

        assertInvalid(0, "MONDAY", "10:00", "12:00", message, PERSONS);
        assertInvalid(0, "MONDAY", "12-00", "10-00", message, PERSONS);
        assertInvalid(0, "MONDAY", "10-00", "10-00", message, PERSONS);
        assertInvalid(0, "MONDAY", "", "", message, PERSONS);
    }

    private static void assertInvalid(Integer studentIndex, String day, String from, String to, String message,
            List<Person> persons) {
        JsonAdaptedScheduledSession adapted = new JsonAdaptedScheduledSession(studentIndex, day, from, to);
        assertThrows(IllegalValueException.class, message, () -> adapted.toModelType(persons));
    }

    private static void assertInvalidIndexes(List<Integer> studentIndexes, String message) {
        JsonAdaptedScheduledSession adapted = new JsonAdaptedScheduledSession(null, studentIndexes, "MONDAY",
                "10-00", "12-00", null);
        assertThrows(IllegalValueException.class, message, () -> adapted.toModelType(PERSONS));
    }
}
