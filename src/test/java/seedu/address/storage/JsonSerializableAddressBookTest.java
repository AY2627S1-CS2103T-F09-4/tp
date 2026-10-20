package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.testutil.TypicalPersons;
import seedu.address.testutil.TypicalSchedule;

public class JsonSerializableAddressBookTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableAddressBookTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsAddressBook.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonAddressBook.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonAddressBook.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        AddressBook addressBookFromFile = dataFromFile.toModelType();
        AddressBook typicalPersonsAddressBook = TypicalPersons.getTypicalAddressBook();
        assertEquals(addressBookFromFile, typicalPersonsAddressBook);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableAddressBook.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableAddressBook.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

    @Test
    public void toModelType_fileWithoutSchedule_loadsWithEmptySchedule() throws Exception {
        JsonSerializableAddressBook dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableAddressBook.class).get();
        assertEquals(List.of(), dataFromFile.toModelType().getScheduleList());
    }

    @Test
    public void conversion_addressBookWithSchedule_roundTripsThroughJson() throws Exception {
        AddressBook original = TypicalSchedule.getTypicalAddressBookWithSchedule();

        String json = JsonUtil.toJsonString(new JsonSerializableAddressBook(original));
        JsonSerializableAddressBook reloaded = JsonUtil.fromJsonString(json, JsonSerializableAddressBook.class);

        AddressBook restored = reloaded.toModelType();
        assertEquals(original, restored);
        assertEquals(TypicalSchedule.getTypicalSessions(), restored.getScheduleList());
    }

    @Test
    public void conversion_afterSessionRemoved_removedSessionIsNotSaved() throws Exception {
        AddressBook original = TypicalSchedule.getTypicalAddressBookWithSchedule();
        original.removeScheduledSession(TypicalSchedule.ALICE_MONDAY_MORNING);

        AddressBook restored = new JsonSerializableAddressBook(original).toModelType();

        assertEquals(List.of(TypicalSchedule.ALICE_WEDNESDAY, TypicalSchedule.BENSON_MONDAY_OVERLAP),
                restored.getScheduleList());
    }

    @Test
    public void toModelType_sessionsPointToLoadedPersons() throws Exception {
        AddressBook original = TypicalSchedule.getTypicalAddressBookWithSchedule();

        AddressBook restored = new JsonSerializableAddressBook(original).toModelType();

        for (ScheduledSession session : restored.getScheduleList()) {
            assertTrue(restored.getPersonList().stream().anyMatch(p -> p == session.getStudent()));
        }
    }

    @Test
    public void toModelType_sessionWithUnknownStudent_throwsIllegalValueException() {
        JsonSerializableAddressBook data = new JsonSerializableAddressBook(List.of(), List.of(),
                List.of(new JsonAdaptedScheduledSession(0, "MONDAY", "10-00", "12-00")));
        assertThrows(IllegalValueException.class, data::toModelType);
    }

}
