package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.Schedule;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;
import seedu.address.model.statistics.LessonRecord;
import seedu.address.model.statistics.LessonRecord.Outcome;
import seedu.address.testutil.PersonBuilder;

public class AttendanceStorageTest {
    @Test
    public void oldSchedules_receiveDistinctIdsWithoutHistory_andIdsPersistOnSave() throws Exception {
        Person student = new PersonBuilder().build();
        String json = "{\"persons\":[" + JsonUtil.toJsonString(new JsonAdaptedPerson(student))
                + "],\"schedule\":[{\"studentIndex\":0,\"day\":\"MONDAY\",\"from\":\"10-00\",\"to\":\"11-00\"},"
                + "{\"studentIndex\":0,\"day\":\"MONDAY\",\"from\":\"10-00\",\"to\":\"11-00\"}]}";
        AddressBook migrated = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class).toModelType();
        assertTrue(migrated.getLessonRecords().isEmpty());
        assertNotEquals(migrated.getScheduleList().getFirst().getSessionId(),
                migrated.getScheduleList().getLast().getSessionId());
        assertTrue(migrated.getScheduleList().getFirst().isSameSlot(migrated.getScheduleList().getLast()));
        assertEquals(migrated, roundTrip(migrated));
    }

    @Test
    public void linkedHistoryAndSessionIdentities_surviveCopyResetReloadAndRemoval() throws Exception {
        AddressBook book = bookWithHistory();
        AddressBook copy = book.copy();
        assertEquals(book, copy);
        assertEquals(book.hashCode(), copy.hashCode());
        assertEquals(book, roundTrip(book));
        book.setHistory(List.of(), List.of());
        assertNotEquals(book, copy);
        book.resetData(copy);
        assertEquals(book, copy);
        book.removeScheduledSession(book.getScheduleList().getFirst());
        assertEquals(book, roundTrip(book));
        assertEquals(copy.getScheduleList().getFirst().getSessionId(),
                book.getLessonRecords().getFirst().sourceSessionId());
        assertNotEquals(book.getLessonRecords().getFirst(), new LessonRecord(
                copy.getLessonRecords().getFirst().id(), copy.getLessonRecords().getFirst().studentId(),
                "Amy Bee", "Math", LocalDate.of(2026, 10, 19), LocalTime.of(10, 0), LocalTime.of(11, 0),
                Outcome.COMPLETED));
    }

    @Test
    public void duplicateIdsAndDuplicateLinkedOccurrences_failLoading() throws Exception {
        AddressBook book = bookWithHistory();
        String session = JsonUtil.toJsonString(new JsonAdaptedScheduledSession(book.getScheduleList().getFirst(),
                book.getPersonList()));
        String lesson = JsonUtil.toJsonString(new JsonAdaptedLessonRecord(book.getLessonRecords().getFirst()));
        String student = JsonUtil.toJsonString(new JsonAdaptedPerson(book.getPersonList().getFirst()));
        for (String json : List.of("{\"persons\":[" + student + "],\"schedule\":[" + session + "," + session + "]}",
                "{\"lessonRecords\":[" + lesson + "," + lesson.replace(book.getLessonRecords().getFirst().id(),
                        UUID.randomUUID().toString()) + "]}")) {
            JsonSerializableAddressBook data = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);
            assertThrows(IllegalValueException.class, data::toModelType);
        }
    }

    @Test
    public void invalidIdsAndWrongStudentLinks_failLoading() throws Exception {
        AddressBook book = bookWithHistory();
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(book));
        String id = book.getScheduleList().getFirst().getSessionId();
        for (String bad : List.of(json.replace("\"sessionId\" : \"" + id + "\"", "\"sessionId\" : \"bad-id\""),
                json.replace("\"sourceSessionId\" : \"" + id + "\"", "\"sourceSessionId\" : \"bad-id\""))) {
            JsonSerializableAddressBook data = JsonUtil.fromJsonString(bad, JsonSerializableAddressBook.class);
            assertNotEquals(json, bad);
            assertThrows(IllegalValueException.class, data::toModelType);
        }
        LessonRecord old = book.getLessonRecords().getFirst();
        LessonRecord wrong = new LessonRecord(old.id(), UUID.randomUUID().toString(), old.studentName(), old.subject(),
                old.date(), old.start(), old.end(), old.outcome(), old.sourceSessionId());
        assertThrows(IllegalArgumentException.class, () -> book.setHistory(List.of(wrong), List.of()));
        String student = JsonUtil.toJsonString(new JsonAdaptedPerson(book.getPersonList().getFirst()));
        String session = JsonUtil.toJsonString(new JsonAdaptedScheduledSession(book.getScheduleList().getFirst(),
                book.getPersonList()));
        String lesson = JsonUtil.toJsonString(new JsonAdaptedLessonRecord(wrong));
        JsonSerializableAddressBook data = JsonUtil.fromJsonString("{\"persons\":[" + student
                + "],\"schedule\":[" + session + "],\"lessonRecords\":[" + lesson + "]}",
                JsonSerializableAddressBook.class);
        assertThrows(IllegalValueException.class, data::toModelType);
    }

    @Test
    public void sessionEqualityUsesIdentity_andDuplicateIdsAreRejected() {
        Person student = new PersonBuilder().build();
        ScheduledSession first = new ScheduledSession(student, DayOfWeek.MONDAY, TimeSlot.of("10-00", "11-00"));
        ScheduledSession second = new ScheduledSession(student, first.getDay(), first.getTimeSlot());
        assertNotEquals(first, second);
        assertTrue(first.isSameSlot(second));
        assertEquals(first.getSessionId(), first.withStudent(student.copy()).getSessionId());
        Schedule schedule = new Schedule();
        schedule.add(first);
        assertThrows(IllegalArgumentException.class, () -> schedule.add(first));
        assertThrows(IllegalArgumentException.class, () -> schedule.setSessions(List.of(first, first)));
        assertEquals(List.of(first), schedule.asUnmodifiableObservableList());
    }

    private AddressBook bookWithHistory() {
        AddressBook book = new AddressBook();
        Person student = new PersonBuilder().build();
        book.addPerson(student);
        ScheduledSession session = new ScheduledSession(student, DayOfWeek.MONDAY, TimeSlot.of("10-00", "11-00"));
        book.addScheduledSession(session);
        book.setHistory(List.of(new LessonRecord(UUID.randomUUID().toString(), student.getStudentId(), "Amy Bee",
                "Math", LocalDate.of(2026, 10, 19), LocalTime.of(10, 0), LocalTime.of(11, 0), Outcome.COMPLETED,
                session.getSessionId())), List.of());
        return book;
    }

    private AddressBook roundTrip(AddressBook book) throws Exception {
        return JsonUtil.fromJsonString(JsonUtil.toJsonString(new JsonSerializableAddressBook(book)),
                JsonSerializableAddressBook.class).toModelType();
    }
}
