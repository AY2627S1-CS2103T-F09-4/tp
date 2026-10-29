package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.statistics.LessonRecord;
import seedu.address.model.statistics.LessonRecord.Outcome;
import seedu.address.testutil.PersonBuilder;

public class ListLessonsCommandTest {
    @Test
    public void execute_chronologicalRows_retainOriginalLessonNumbersAndDeletionTargets() throws Exception {
        ModelManager model = new ModelManager();
        Person student = new PersonBuilder().build();
        model.addPerson(student);
        List<LessonRecord> original = List.of(lesson(student, "2026-10-19", "10:00"),
                lesson(student, "2026-10-18", "09:00"), lesson(student, "2026-10-19", "09:00"),
                lesson(student, "2026-10-19", "09:00"));
        model.setHistory(original, List.of());
        model.updateFilteredPersonList(person -> false);
        AddressBookParser parser = new AddressBookParser();
        Command listing = parser.parseCommand("list-lessons");
        assertTrue(listing.isReadOnly());

        String output = listing.execute(model).getFeedbackToUser();
        List<Integer> rows = output.lines().skip(1)
                .map(line -> Integer.parseInt(line.substring(0, line.indexOf('.')))).toList();
        List<Integer> lessonNumbers = output.lines().skip(1)
                .map(line -> Integer.parseInt(line.substring(line.lastIndexOf('#') + 1))).toList();
        assertEquals(List.of(1, 2, 3, 4), rows);
        assertEquals(List.of(2, 3, 4, 1), lessonNumbers);
        assertEquals(original, model.getAddressBook().getLessonRecords());

        String result = parser.parseCommand("delete-lesson " + lessonNumbers.getFirst())
                .execute(model).getFeedbackToUser();
        assertTrue(result.contains("Deleted lesson #2: Amy Bee | 2026-10-18 09:00-10:00"));
        assertEquals(List.of(original.get(0), original.get(2), original.get(3)),
                model.getAddressBook().getLessonRecords());
        List<Integer> renumbered = listing.execute(model).getFeedbackToUser().lines().skip(1)
                .map(line -> Integer.parseInt(line.substring(line.lastIndexOf('#') + 1))).toList();
        assertEquals(List.of(2, 3, 1), renumbered);
    }

    private LessonRecord lesson(Person student, String date, String start) {
        LocalTime time = LocalTime.parse(start);
        return new LessonRecord(UUID.randomUUID().toString(), student.getStudentId(), student.getName().fullName,
                student.getSubject().value, LocalDate.parse(date), time, time.plusHours(1), Outcome.COMPLETED);
    }
}
