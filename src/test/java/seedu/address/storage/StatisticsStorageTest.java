package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.statistics.AssessmentRecord;
import seedu.address.model.statistics.LessonRecord;
import seedu.address.model.statistics.LessonRecord.Outcome;
import seedu.address.testutil.PersonBuilder;

public class StatisticsStorageTest {
    @Test
    public void oldJson_assignsStudentIdsWithoutInventingHistory_andPersistsIdsOnNextSave() throws Exception {
        AddressBook old = JsonUtil.readJsonFile(Path.of("src/test/data/JsonSerializableAddressBookTest/"
                + "typicalPersonsAddressBook.json"), JsonSerializableAddressBook.class).orElseThrow().toModelType();
        assertTrue(old.getLessonRecords().isEmpty());
        assertTrue(old.getAssessmentRecords().isEmpty());
        assertEquals(old.getPersonList().size(), old.getPersonList().stream().map(Person::getStudentId)
                .distinct().count());
        AddressBook restored = roundTrip(old);
        assertEquals(old.getPersonList().stream().map(Person::getStudentId).toList(),
                restored.getPersonList().stream().map(Person::getStudentId).toList());
    }

    @Test
    public void sourceRecordsAndDeletedStudentHistory_roundTripWithStableIds() throws Exception {
        AddressBook original = historyBook();
        String studentId = original.getPersonList().getFirst().getStudentId();
        original.removePerson(original.getPersonList().getFirst());
        AddressBook restored = roundTrip(original);
        assertEquals(original, restored);
        assertEquals(studentId, restored.getLessonRecords().getFirst().studentId());
        assertEquals(studentId, restored.getAssessmentRecords().getFirst().studentId());
        assertEquals("Amy Bee", restored.getLessonRecords().getFirst().studentName());
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(original));
        assertFalse(json.contains("percentage"));
        assertFalse(json.contains("teachingHours"));
    }

    @Test
    public void history_participatesInEqualityCopyAndReset_withoutSharingMutableLists() {
        AddressBook original = historyBook();
        AddressBook copy = original.copy();
        assertEquals(original, copy);
        assertEquals(original.hashCode(), copy.hashCode());
        assertEquals(original.getPersonList().getFirst().getStudentId(),
                copy.getPersonList().getFirst().getStudentId());
        original.setHistory(List.of(), List.of());
        assertNotEquals(original, copy);
        assertEquals(1, copy.getLessonRecords().size());
        original.resetData(copy);
        assertEquals(original, copy);
        original.resetData(new AddressBook());
        assertTrue(original.getLessonRecords().isEmpty());
        assertTrue(original.getAssessmentRecords().isEmpty());
    }

    @Test
    public void malformedHistoricalJson_rejectedRatherThanSilentlyDroppingRecords() throws Exception {
        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(historyBook()));
        for (String bad : List.of(json.replace("2026-01-01", "2026-02-30"),
                json.replace("COMPLETED", "PRESENT"), json.replace("11:30", "09:00"),
                json.replace("\"score\" : 8", "\"score\" : -1"),
                json.replace("\"maximum\" : 1E+1", "\"maximum\" : 0"),
                json.replace("\"studentName\" : \"Amy Bee\"", "\"studentName\" : \"\""))) {
            JsonSerializableAddressBook data = JsonUtil.fromJsonString(bad, JsonSerializableAddressBook.class);
            assertNotEquals(json, bad, "Each malformed fixture must actually change the valid JSON");
            assertThrows(IllegalValueException.class, data::toModelType, bad);
        }
        for (String bad : List.of("{\"lessonRecords\":null}", "{\"assessmentRecords\":[null]}")) {
            JsonSerializableAddressBook data = JsonUtil.fromJsonString(bad, JsonSerializableAddressBook.class);
            assertThrows(IllegalValueException.class, data::toModelType);
        }
    }

    @Test
    public void duplicateStudentOrRecordIds_rejected() throws Exception {
        AddressBook book = historyBook();
        String lesson = JsonUtil.toJsonString(new JsonAdaptedLessonRecord(book.getLessonRecords().getFirst()));
        String assessment = JsonUtil.toJsonString(new JsonAdaptedAssessmentRecord(
                book.getAssessmentRecords().getFirst()));
        for (String json : List.of("{\"lessonRecords\":[" + lesson + "," + lesson + "]}",
                "{\"assessmentRecords\":[" + assessment + "," + assessment + "]}")) {
            JsonSerializableAddressBook data = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);
            assertThrows(IllegalValueException.class, data::toModelType);
        }
        Person first = book.getPersonList().getFirst();
        Person second = new PersonBuilder().withName("Bob").withPhone("90000000").build()
                .withStudentId(first.getStudentId());
        String json = "{\"persons\":[" + JsonUtil.toJsonString(new JsonAdaptedPerson(first)) + ","
                + JsonUtil.toJsonString(new JsonAdaptedPerson(second)) + "]}";
        JsonSerializableAddressBook data = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);
        assertThrows(IllegalValueException.class, data::toModelType);
    }

    private AddressBook historyBook() {
        AddressBook book = new AddressBook();
        Person student = new PersonBuilder().build();
        book.addPerson(student);
        book.setHistory(List.of(new LessonRecord(UUID.randomUUID().toString(), student.getStudentId(),
                student.getName().fullName, "Math", LocalDate.of(2026, 1, 1), LocalTime.of(10, 0),
                LocalTime.of(11, 30), Outcome.COMPLETED)),
                List.of(new AssessmentRecord(UUID.randomUUID().toString(), student.getStudentId(),
                    student.getName().fullName, LocalDate.of(2026, 1, 1), "Math", "Quiz", "Test",
                    BigDecimal.valueOf(8), BigDecimal.TEN)));
        return book;
    }

    private AddressBook roundTrip(AddressBook book) throws Exception {
        return JsonUtil.fromJsonString(JsonUtil.toJsonString(new JsonSerializableAddressBook(book)),
                JsonSerializableAddressBook.class).toModelType();
    }
}
