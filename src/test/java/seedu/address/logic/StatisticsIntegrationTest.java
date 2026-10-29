package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.SnapshotManager;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class StatisticsIntegrationTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-21T00:00:00Z"), ZoneId.of("Asia/Singapore"));
    private static final String LESSON = "mark-attendance 1 date/2026-10-21 status/completed";
    private static final String ASSESSMENT = "record-assessment 1 date/2026-10-21 subject/Math"
            + " category/Quiz title/Test score/8 max/10";

    @TempDir
    public Path temporaryFolder;

    private ModelManager model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;

    @BeforeEach
    public void setUp() throws Exception {
        SnapshotManager.reset();
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().build());
        book.addPerson(new PersonBuilder().withName("Bob").withPhone("90000000").build());
        model = new ModelManager(book, new UserPrefs(), CLOCK);
        storage = new JsonAddressBookStorage(temporaryFolder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))));
        logic.execute("schedule-student 1 day/wednesday from/10-00 to/11-30");
        logic.execute("schedule-student 2 day/wednesday from/10-00 to/11-30");
    }

    @Test
    public void reports_defaultYearThroughTodayAndExplicitInclusiveBoundaries() throws Exception {
        logic.execute(LESSON.replace("2026-10-21", "2025-12-31"));
        logic.execute("schedule-student 1 day/thursday from/10-00 to/11-30");
        logic.execute("mark-attendance 3 date/2026-01-01 status/completed");
        logic.execute(LESSON);
        String current = feedback("stats");
        assertTrue(current.contains("2026-01-01 to 2026-10-21"));
        assertTrue(current.contains("Completed lessons: 2; teaching hours: 3.00"));
        assertTrue(feedback("stats from/2025-12-31 to/2025-12-31").contains("Completed lessons: 1"));
        assertTrue(feedback("stats-student 1").contains("completed: 2"));
    }

    @Test
    public void filteredIndices_targetDisplayedStudent_butBusinessUsesAllHistory() throws Exception {
        logic.execute(LESSON);
        Person bob = model.getPersonList().get(1);
        filterListToBob(bob);
        logic.execute(LESSON.replace("attendance 1", "attendance 2"));
        filterListToBob(bob);
        logic.execute(ASSESSMENT);
        filterListToBob(bob);
        assertEquals(bob.getStudentId(), model.getAddressBook().getLessonRecords().getLast().studentId());
        assertEquals(bob.getStudentId(), model.getAddressBook().getAssessmentRecords().getFirst().studentId());
        assertTrue(feedback("stats-student 1").contains("Student: Bob"));
        assertTrue(feedback("stats").contains(
                "Completed lessons: 2; teaching hours: 3.00; distinct students taught: 2"));
        assertThrows(CommandException.class, () -> logic.execute("stats-student 2"));
        assertThrows(CommandException.class, () -> logic.execute(LESSON.replace("attendance 1", "attendance 3")));
        assertEquals(1, model.getFilteredPersonList().size());
    }

    private void filterListToBob(Person bob) {
        model.updateFilteredPersonList(p -> p.getStudentId().equals(bob.getStudentId()));
    }

    @Test
    public void reportsAndLists_doNotWriteDataOrDiscardRedoHistory() throws Exception {
        logic.execute(LESSON);
        String id = model.getAddressBook().getLessonRecords().getFirst().id();
        logic.execute(ASSESSMENT);
        logic.execute("undo");
        String saved = Files.readString(storage.getAddressBookFilePath());
        ReadOnlyAddressBook before = new AddressBook(model.getAddressBook());
        for (String report : new String[]{"stats", "stats-student 1", "list-lessons", "list-assessments"}) {
            logic.execute(report);
            assertTrue(SnapshotManager.canRedo());
            assertEquals(before, model.getAddressBook());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        }
        logic.execute("redo");
        assertEquals(1, model.getAddressBook().getAssessmentRecords().size());
        assertEquals(id, model.getAddressBook().getLessonRecords().getFirst().id());
    }

    @Test
    public void identities_surviveEditsCopiesSaveReloadUndoRedoAndTaskChanges() throws Exception {
        String studentId = model.getPersonList().getFirst().getStudentId();
        logic.execute(LESSON);
        logic.execute(ASSESSMENT);
        logic.execute("edit-student 1 n/New Name p/91234567 s/English");
        assertEquals(studentId, model.getPersonList().getFirst().getStudentId());
        logic.execute("add-task 1 task/Homework");
        assertEquals(studentId, model.getPersonList().getFirst().getStudentId());
        logic.execute("undo");
        logic.execute("undo");
        assertEquals(studentId, model.getPersonList().getFirst().getStudentId());
        logic.execute("redo");
        ReadOnlyAddressBook restored = storage.readAddressBook().orElseThrow();
        assertEquals(studentId, restored.getPersonList().getFirst().getStudentId());
        assertEquals(model.getAddressBook(), restored);
        assertTrue(feedback("stats-student 1").contains("completed: 1"));
        assertTrue(feedback("list-lessons").contains("Mathematics"));
        assertTrue(feedback("list-lessons").contains("New Name"));
    }

    @Test
    public void deletion_keepsIdentifiableHistory_andRecreatedStudentDoesNotInheritIt() throws Exception {
        logic.execute(LESSON);
        logic.execute(ASSESSMENT);
        String oldId = model.getPersonList().getFirst().getStudentId();
        logic.execute("delete-student 1");
        assertTrue(feedback("list-lessons").contains("Amy Bee (deleted)"));
        assertTrue(feedback("list-assessments").contains("Amy Bee"));
        assertTrue(feedback("stats").contains("Completed lessons: 1"));
        assertEquals(1, storage.readAddressBook().orElseThrow().getLessonRecords().size());
        logic.execute("undo");
        assertEquals(oldId, model.getPersonList().getFirst().getStudentId());
        logic.execute("redo");
        Person recreated = new PersonBuilder().build();
        model.addPerson(recreated);
        assertNotEquals(oldId, recreated.getStudentId());
        assertTrue(feedback("stats-student 2").contains("completed: 0"));
    }

    @Test
    public void assessmentCorrectionsAndDeletes_areUndoableEvenForDeletedStudents() throws Exception {
        logic.execute(LESSON);
        logic.execute(ASSESSMENT);
        String assessmentId = model.getAddressBook().getAssessmentRecords().getFirst().id();
        logic.execute("delete-student 1");
        logic.execute("edit-assessment 1"
                + " date/2026-01-01 subject/English category/Exam title/Corrected score/9 max/10");
        assertEquals(assessmentId, model.getAddressBook().getAssessmentRecords().getFirst().id());
        logic.execute("delete-assessment 1");
        assertTrue(model.getAddressBook().getAssessmentRecords().isEmpty());
        assertEquals(1, model.getAddressBook().getLessonRecords().size());
        logic.execute("undo");
        assertTrue(feedback("list-assessments").contains("Corrected"));
        assertEquals(assessmentId, model.getAddressBook().getAssessmentRecords().getFirst().id());
        logic.execute("redo");
        assertTrue(model.getAddressBook().getAssessmentRecords().isEmpty());
    }

    @Test
    public void invalidFutureDatesAndUnknownNumbers_leaveHistoryAndRedoUnchanged() throws Exception {
        logic.execute(LESSON);
        logic.execute("undo");
        assertThrows(CommandException.class, () -> logic.execute(LESSON.replace("2026-10-21", "2026-10-22")));
        assertThrows(CommandException.class, () -> logic.execute(ASSESSMENT.replace("2026-10-21", "2026-10-22")));
        assertThrows(CommandException.class, () ->
                logic.execute("delete-assessment 1"));
        assertTrue(model.getAddressBook().getLessonRecords().isEmpty());
        assertTrue(SnapshotManager.canRedo());
        logic.execute("redo");
        assertEquals(1, model.getAddressBook().getLessonRecords().size());
    }

    @Test
    public void weeklySchedule_neverCreatesHistoricalActivity() throws Exception {
        logic.execute("schedule-student 1 day/monday from/10-00 to/11-00");
        assertFalse(model.getAddressBook().getScheduleList().isEmpty());
        assertTrue(model.getAddressBook().getLessonRecords().isEmpty());
        assertTrue(feedback("stats").contains("Completed lessons: 0"));
        assertTrue(feedback("stats-student 1").contains("Attendance rate: N/A"));
    }

    @Test
    public void historyHeadings_areReadableForEmptyAndPopulatedLists() throws Exception {
        assertEquals("Assessments (all dates): none", feedback("list-assessments"));
        assertEquals("Recorded lessons (all dates): none", feedback("list-lessons"));
        logic.execute(ASSESSMENT);
        logic.execute(LESSON);
        assertTrue(feedback("list-assessments").startsWith("Assessments (all dates):\n1. "));
        assertTrue(feedback("list-lessons").startsWith("Recorded lessons (all dates):\n1. "));
    }

    @Test
    public void executionValidationErrors_includeSyntaxAndExampleWithoutChangingData() throws Exception {
        AddressBook before = new AddressBook(model.getAddressBook());
        for (String input : new String[] {"stats-student 3", ASSESSMENT.replace("assessment 1", "assessment 3"),
            ASSESSMENT.replace("2026-10-21", "2026-10-22"), "delete-assessment 1"}) {
            CommandException error = assertThrows(CommandException.class, () -> logic.execute(input));
            String command = input.split(" ")[0];
            assertTrue(error.getMessage().contains("\n" + command + ": "));
            assertTrue(error.getMessage().contains("Example: " + command));
            assertEquals(before, model.getAddressBook());
        }
    }

    private String feedback(String command) throws Exception {
        return logic.execute(command).getFeedbackToUser();
    }

    @Test
    public void historicalNumbers_useWholeCollectionsAndRenumberWithoutChangingIdentities() throws Exception {
        logic.execute(LESSON);
        logic.execute(LESSON.replace("attendance 1", "attendance 2"));
        logic.execute(ASSESSMENT);
        logic.execute(ASSESSMENT.replace("assessment 1", "assessment 2"));
        String secondLessonId = model.getAddressBook().getLessonRecords().getLast().id();
        String secondAssessmentId = model.getAddressBook().getAssessmentRecords().getLast().id();
        model.updateFilteredPersonList(p -> false);
        assertTrue(feedback("list-lessons").contains("2. Bob"));
        assertTrue(feedback("list-assessments").contains("2. Bob"));
        logic.execute(LESSON.replace("attendance 1", "attendance 2").replace("completed", "absent"));
        logic.execute("edit-assessment 2 date/2026-10-20 subject/Math category/Quiz title/Updated score/5 max/10");
        assertEquals(secondLessonId, model.getAddressBook().getLessonRecords().getLast().id());
        assertEquals(secondAssessmentId, model.getAddressBook().getAssessmentRecords().getLast().id());
        logic.execute("delete-assessment 1");
        assertTrue(feedback("list-lessons").contains("2. Bob"));
        assertTrue(feedback("list-assessments").contains("1. Bob"));
        assertEquals(secondLessonId, model.getAddressBook().getLessonRecords().getLast().id());
        assertEquals(secondAssessmentId, model.getAddressBook().getAssessmentRecords().getFirst().id());
        logic.execute("undo");
        assertTrue(feedback("list-assessments").contains("2. Bob"));
        logic.execute("redo");
        assertTrue(feedback("list-assessments").contains("1. Bob"));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void shortOutputs_doNotExposeInternalIds_andDeletedStudentsRemainNamed() throws Exception {
        String uuidPattern = "(?s).*[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}.*";
        assertFalse(feedback(LESSON).matches(uuidPattern));
        assertFalse(feedback(ASSESSMENT).matches(uuidPattern));
        assertFalse(feedback("list-lessons").matches(uuidPattern));
        assertFalse(feedback("list-assessments").matches(uuidPattern));
        assertFalse(feedback("stats-student 1").matches(uuidPattern));
        assertFalse(feedback(LESSON.replace("completed", "absent"))
                .matches(uuidPattern));
        assertFalse(feedback("edit-assessment 1 date/2026-10-21 subject/Math category/Quiz title/Test score/5 max/10")
                .matches(uuidPattern));
        logic.execute("delete-student 1");
        assertTrue(feedback("list-lessons").contains("Amy Bee (deleted)"));
        assertFalse(feedback("list-lessons").matches(uuidPattern));
        assertFalse(feedback("delete-assessment 1").matches(uuidPattern));
    }

    @Test
    public void historicalNumbers_outOfRangeLeaveDataAndRedoUnchanged() throws Exception {
        logic.execute(LESSON);
        logic.execute("undo");
        assertThrows(CommandException.class, () -> logic.execute("delete-assessment 1"));
        assertThrows(CommandException.class, () -> logic.execute(
                "edit-assessment 1 date/2026-10-21 subject/Math category/Quiz title/Test score/5 max/10"));
        assertTrue(SnapshotManager.canRedo());
        logic.execute("redo");
        assertEquals(1, model.getAddressBook().getLessonRecords().size());
    }
}
