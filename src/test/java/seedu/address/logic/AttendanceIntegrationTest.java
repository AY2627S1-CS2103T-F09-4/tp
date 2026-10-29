package seedu.address.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.statistics.LessonRecord;
import seedu.address.model.statistics.LessonRecord.Outcome;
import seedu.address.storage.JsonAddressBookStorage;
import seedu.address.storage.JsonUserPrefsStorage;
import seedu.address.storage.SnapshotManager;
import seedu.address.storage.StorageManager;
import seedu.address.testutil.PersonBuilder;

public class AttendanceIntegrationTest {
    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-20T17:00:00Z"), ZoneId.of("Asia/Singapore"));

    @TempDir
    public Path folder;

    private ModelManager model;
    private LogicManager logic;
    private JsonAddressBookStorage storage;
    private String sessionId;

    @BeforeEach
    public void setUp() throws Exception {
        SnapshotManager.reset();
        AddressBook book = new AddressBook();
        book.addPerson(new PersonBuilder().build());
        model = new ModelManager(book, new UserPrefs(), CLOCK);
        storage = new JsonAddressBookStorage(folder.resolve("addressbook.json"));
        logic = new LogicManager(model, new StorageManager(storage,
                new JsonUserPrefsStorage(folder.resolve("preferences.json"))));
        logic.execute("schedule-student 1 day/monday from/10-00 to/11-30");
        sessionId = model.getScheduleList().getFirst().getSessionId();
    }

    @Test
    public void markAttendance_snapshotsDetailsAndFeedsStatistics_withoutChangingRecurringSchedule() throws Exception {
        ScheduledSession session = model.getScheduleList().getFirst();
        String result = mark(1, "2026-10-19", "completed");
        LessonRecord record = model.getAddressBook().getLessonRecords().getFirst();
        assertEquals(sessionId, record.sourceSessionId());
        assertEquals(session.getStudents().getFirst().getStudentId(), record.studentId());
        assertEquals("Mathematics", record.subject());
        assertEquals(session.getTimeSlot().start, record.start());
        assertEquals(session.getTimeSlot().end, record.end());
        assertTrue(result.contains("Amy Bee on 2026-10-19 10:00-11:30: completed"));
        assertTrue(result.contains("(lesson #1)"));
        assertEquals(List.of(session), model.getScheduleList());
        assertTrue(feedback("stats").contains("Completed lessons: 1; teaching hours: 1.50"));
        assertFalse(feedback("list-lessons").contains(sessionId));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void repeatedMarking_updatesOneRecord_allOutcomesAndSeparateDates() throws Exception {
        mark(1, "2026-10-19", "completed");
        String recordId = model.getAddressBook().getLessonRecords().getFirst().id();
        for (Outcome outcome : Outcome.values()) {
            mark(1, "2026-10-19", outcome.name());
            assertEquals(1, model.getAddressBook().getLessonRecords().size());
            assertEquals(recordId, model.getAddressBook().getLessonRecords().getFirst().id());
            assertEquals(outcome, model.getAddressBook().getLessonRecords().getFirst().outcome());
        }
        mark(1, "2026-10-12", "absent");
        assertEquals(2, model.getAddressBook().getLessonRecords().size());
        assertTrue(feedback("stats-student 1").contains("absent: 1\ncancelled: 0\nunrecorded: 1"));
        assertTrue(feedback("stats-student 1").contains("Attendance rate: 0.00%"));
    }

    @Test
    public void correctionsAndStudentEdits_doNotRewriteCapturedDetailsOrLoseLink() throws Exception {
        mark(1, "2026-10-19", "completed");
        logic.execute("edit-student 1 n/New Name s/English");
        assertEquals(sessionId, model.getScheduleList().getFirst().getSessionId());
        mark(1, "2026-10-19", "absent");
        mark(1, "2026-10-19", "completed");
        LessonRecord updated = model.getAddressBook().getLessonRecords().getFirst();
        assertEquals("Mathematics", updated.subject());
        assertEquals("10:00", updated.start().toString());
        assertEquals("Amy Bee", updated.studentName());
        assertEquals(sessionId, updated.sourceSessionId());
        mark(1, "2026-10-12", "completed");
        assertEquals("English", model.getAddressBook().getLessonRecords().getLast().subject());
    }

    @Test
    public void undoRedoAndReload_preserveSessionAndRecordIdentities_andListingDoesNotWrite() throws Exception {
        mark(1, "2026-10-19", "completed");
        String recordId = model.getAddressBook().getLessonRecords().getFirst().id();
        mark(1, "2026-10-19", "absent");
        logic.execute("undo");
        assertEquals(Outcome.COMPLETED, model.getAddressBook().getLessonRecords().getFirst().outcome());
        String saved = Files.readString(storage.getAddressBookFilePath());
        assertTrue(feedback("list-schedule").contains("1. Amy Bee"));
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
        assertTrue(SnapshotManager.canRedo());
        logic.execute("redo");
        assertEquals(Outcome.ABSENT, model.getAddressBook().getLessonRecords().getFirst().outcome());
        assertEquals(recordId, model.getAddressBook().getLessonRecords().getFirst().id());
        logic.execute("undo");
        logic.execute("undo");
        assertTrue(model.getAddressBook().getLessonRecords().isEmpty());
        logic.execute("redo");
        assertEquals(recordId, model.getAddressBook().getLessonRecords().getFirst().id());
        logic.execute("redo");
        ModelManager reloaded = new ModelManager(storage.readAddressBook().orElseThrow(), new UserPrefs(), CLOCK);
        logic = new LogicManager(reloaded, new StorageManager(storage,
                new JsonUserPrefsStorage(folder.resolve("preferences.json"))));
        model = reloaded;
        mark(1, "2026-10-19", "cancelled");
        assertEquals(1, model.getAddressBook().getLessonRecords().size());
        assertEquals(recordId, model.getAddressBook().getLessonRecords().getFirst().id());
        assertEquals(sessionId, model.getScheduleList().getFirst().getSessionId());
        logic.execute("undo");
        assertEquals(Outcome.ABSENT, model.getAddressBook().getLessonRecords().getFirst().outcome());
        logic.execute("redo");
        assertEquals(recordId, model.getAddressBook().getLessonRecords().getFirst().id());
        assertEquals(Outcome.CANCELLED, model.getAddressBook().getLessonRecords().getFirst().outcome());
    }

    @Test
    public void duplicateSlots_areDistinct_andUnschedulingOrDeletingKeepsHistory() throws Exception {
        logic.execute("schedule-student 1 day/monday from/10-00 to/11-30");
        String duplicateId = model.getScheduleList().getLast().getSessionId();
        assertNotEquals(sessionId, duplicateId);
        mark(1, "2026-10-19", "completed");
        mark(2, "2026-10-19", "absent");
        assertEquals(2, model.getAddressBook().getLessonRecords().size());
        logic.execute("unschedule-student 1 day/monday from/10-00 to/11-30");
        assertEquals(duplicateId, model.getScheduleList().getFirst().getSessionId());
        assertThrows(CommandException.class, () -> mark(2, "2026-10-19", "cancelled"));
        assertEquals(2, model.getAddressBook().getLessonRecords().size());
        logic.execute("delete-student 1");
        assertTrue(model.getScheduleList().isEmpty());
        assertTrue(feedback("list-lessons").contains("(deleted)"));
        assertEquals(2, storage.readAddressBook().orElseThrow().getLessonRecords().size());
        logic.execute("undo");
        assertEquals(duplicateId, model.getScheduleList().getFirst().getSessionId());
    }

    @Test
    public void futureWrongWeekdayAndUnknownSessions_rejectedWithoutMutatingOrDiscardingRedo() throws Exception {
        mark(1, "2026-10-19", "completed");
        logic.execute("undo");
        String saved = Files.readString(storage.getAddressBookFilePath());
        assertThrows(CommandException.class, () -> mark(1, "2026-10-26", "completed"));
        assertThrows(CommandException.class, () -> mark(1, "2026-10-20", "completed"));
        assertThrows(CommandException.class, () ->
                mark(2, "2026-10-19", "completed"));
        assertTrue(model.getAddressBook().getLessonRecords().isEmpty());
        assertTrue(SnapshotManager.canRedo());
        assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
    }

    @Test
    public void listingAndAttendance_ignoreStudentFilters_andLocalTodayUsesInjectedZone() throws Exception {
        model.updateFilteredPersonList(p -> false);
        assertTrue(feedback("list-schedule").contains("1. Amy Bee"));
        mark(1, "2026-10-19", "completed");
        assertTrue(!model.getFilteredPersonList().isEmpty());
        model.updateFilteredPersonList(p -> true);
        logic.execute("schedule-student 1 day/wednesday from/10-00 to/11-00");
        mark(2, "2026-10-21", "completed");
        assertEquals(2, model.getAddressBook().getLessonRecords().size());
    }

    @Test
    public void legacyManualRecords_remainUnlinkedAcrossMarkingAndReload() throws Exception {
        LessonRecord legacy = new LessonRecord(UUID.randomUUID().toString(),
                model.getPersonList().getFirst().getStudentId(), "Amy Bee", "Mathematics",
                LocalDate.of(2026, 10, 19), LocalTime.of(10, 0), LocalTime.of(11, 30), Outcome.COMPLETED);
        model.setHistory(List.of(legacy), List.of());
        mark(1, "2026-10-19", "completed");
        mark(1, "2026-10-19", "absent");
        assertEquals(2, model.getAddressBook().getLessonRecords().size());
        assertEquals(legacy, model.getAddressBook().getLessonRecords().getFirst());
        assertNull(model.getAddressBook().getLessonRecords().getFirst().sourceSessionId());
        assertTrue(feedback("stats").contains("Completed lessons: 1"));
        assertTrue(feedback("list-lessons").contains("2. Amy Bee"));
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    @Test
    public void removedLessonCommands_preserveDataAndRedo() throws Exception {
        mark(1, "2026-10-19", "completed");
        mark(1, "2026-10-19", "absent");
        logic.execute("undo");
        AddressBook before = new AddressBook(model.getAddressBook());
        String saved = Files.readString(storage.getAddressBookFilePath());
        for (String command : List.of("record-lesson 1 date/2026-10-19 from/10:00 to/11:30 status/completed",
                "edit-lesson 1 date/2026-10-19 from/09:00 to/10:00 status/absent subject/Science",
                "delete-lesson 1")) {
            assertThrows(ParseException.class, () -> logic.execute(command));
            assertEquals(before, model.getAddressBook());
            assertEquals(saved, Files.readString(storage.getAddressBookFilePath()));
            assertTrue(SnapshotManager.canRedo());
        }
        logic.execute("redo");
        assertEquals(Outcome.ABSENT, model.getAddressBook().getLessonRecords().getFirst().outcome());
    }

    @Test
    public void parser_rejectsInvalidAndUnexpectedArguments() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        String valid = "mark-attendance 1 date/2026-10-19 status/completed";
        parser.parseCommand(valid);
        for (String invalid : List.of(valid.replace("attendance 1", "attendance " + sessionId),
                valid.replace("2026-10-19", "2026-02-30"),
                valid.replace("2026-10-19", "2026-1-19"), valid.replace("completed", "present"),
                valid.replace("date/2026-10-19", ""), valid.replace("status/completed", ""),
                valid + " date/2026-10-12", valid + " status/absent", valid + " student/1", "list-schedule 1")) {
            assertThrows(ParseException.class, () -> parser.parseCommand(invalid), invalid);
        }
        assertTrue(parser.parseCommand("list-schedule").isReadOnly());
        assertFalse(parser.parseCommand(valid).isReadOnly());
    }

    @Test
    public void sessionNumbers_renumberAfterUnscheduling_andUndoRedoKeepAttendanceLinked() throws Exception {
        logic.execute("schedule-student 1 day/monday from/10-00 to/11-30");
        String secondId = model.getScheduleList().getLast().getSessionId();
        String listing = feedback("list-schedule");
        assertTrue(listing.contains("1. Amy Bee"));
        assertTrue(listing.contains("2. Amy Bee"));
        assertFalse(listing.contains(secondId));
        mark(2, "2026-10-19", "completed");
        String recordId = model.getAddressBook().getLessonRecords().getFirst().id();
        assertEquals(secondId, model.getAddressBook().getLessonRecords().getFirst().sourceSessionId());
        logic.execute("unschedule-student 1 day/monday from/10-00 to/11-30");
        assertFalse(feedback("list-schedule").contains("2. Amy Bee"));
        mark(1, "2026-10-19", "absent");
        assertEquals(1, model.getAddressBook().getLessonRecords().size());
        assertEquals(recordId, model.getAddressBook().getLessonRecords().getFirst().id());
        logic.execute("undo");
        logic.execute("undo");
        assertTrue(feedback("list-schedule").contains("2. Amy Bee"));
        assertTrue(SnapshotManager.canRedo());
        logic.execute("redo");
        assertFalse(feedback("list-schedule").contains("2. Amy Bee"));
        logic.execute("redo");
        assertEquals(Outcome.ABSENT, model.getAddressBook().getLessonRecords().getFirst().outcome());
        assertEquals(secondId, model.getAddressBook().getLessonRecords().getFirst().sourceSessionId());
    }

    @Test
    public void groupSession_marksEveryStudent_andStudentsCanLeaveOrBeDeleted() throws Exception {
        model.addPerson(new PersonBuilder().withName("Bob Choo").withPhone("22222222").build());
        logic.execute("schedule-student 1 2 day/wednesday from/14-00 to/15-00");
        assertEquals(2, model.getScheduleList().size());
        assertEquals(2, model.getScheduleList().getLast().getStudents().size());
        assertTrue(feedback("list-schedule").contains("2. Amy Bee, Bob Choo |"));

        String result = mark(2, "2026-10-14", "completed");
        assertTrue(result.contains("Amy Bee, Bob Choo on 2026-10-14 14:00-15:00: completed (lessons #1, #2)"),
                result);
        mark(2, "2026-10-14", "absent");
        List<LessonRecord> lessons = model.getAddressBook().getLessonRecords();
        assertEquals(2, lessons.size());
        assertTrue(lessons.stream().allMatch(r -> r.outcome() == Outcome.ABSENT));
        assertNotEquals(lessons.getFirst().studentId(), lessons.getLast().studentId());

        logic.execute("unschedule-student 2 day/wednesday from/14-00 to/15-00");
        assertEquals(1, model.getScheduleList().getLast().getStudents().size());
        assertTrue(mark(2, "2026-10-21", "completed").contains("Amy Bee on 2026-10-21"));
        assertEquals(3, model.getAddressBook().getLessonRecords().size());

        logic.execute("undo");
        logic.execute("undo");
        assertEquals(2, model.getScheduleList().getLast().getStudents().size());
        logic.execute("delete-student 2");
        assertEquals(2, model.getScheduleList().size());
        assertEquals("Amy Bee", model.getScheduleList().getLast().getStudents().getFirst().getName().fullName);
        assertEquals(1, model.getScheduleList().getLast().getStudents().size());
        assertEquals(2, model.getAddressBook().getLessonRecords().size());
        assertEquals(model.getAddressBook(), storage.readAddressBook().orElseThrow());
    }

    private String mark(int number, String date, String status) throws Exception {
        return feedback("mark-attendance " + number + " date/" + date + " status/" + status);
    }

    private String feedback(String command) throws Exception {
        return logic.execute(command).getFeedbackToUser();
    }
}
