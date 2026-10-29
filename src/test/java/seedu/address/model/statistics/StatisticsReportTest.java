package seedu.address.model.statistics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.statistics.LessonRecord.Outcome;
import seedu.address.testutil.PersonBuilder;

public class StatisticsReportTest {
    private final Person alice = new PersonBuilder().build();
    private final LocalDate start = LocalDate.of(2026, 1, 1);
    private final LocalDate end = LocalDate.of(2026, 3, 31);

    @Test
    public void business_onlyCompletedRecords_inclusiveDatesAndMonthlyWorkload() {
        AddressBook book = new AddressBook();
        Person bob = new PersonBuilder().withName("Bob").build();
        book.setHistory(List.of(lesson(alice, start, Outcome.COMPLETED),
                lesson(alice, end, Outcome.COMPLETED), lesson(bob, end, Outcome.COMPLETED),
                lesson(alice, start.minusDays(1), Outcome.COMPLETED),
                lesson(alice, end.plusDays(1), Outcome.COMPLETED), lesson(bob, start, Outcome.ABSENT),
                lesson(bob, start, Outcome.CANCELLED), lesson(bob, start, Outcome.UNRECORDED)), List.of());

        String report = StatisticsReport.business(book, start, end);

        assertTrue(report.contains("Completed lessons: 3; teaching hours: 4.50; distinct students taught: 2"));
        assertTrue(report.contains("2026-02: Completed lessons: 0; teaching hours: 0.00"));
        assertTrue(report.contains("2026-03: Completed lessons: 2; teaching hours: 3.00"));
    }

    @Test
    public void student_attendanceUsesOnlyCompletedAndAbsent_andGroupsComparableMarks() {
        AddressBook book = new AddressBook();
        book.setHistory(List.of(lesson(alice, start, Outcome.COMPLETED), lesson(alice, end, Outcome.ABSENT),
                lesson(alice, start, Outcome.CANCELLED), lesson(alice, start, Outcome.UNRECORDED)),
                List.of(assessment(end, "Math", "Quiz", "Last", "8", "10"),
                    assessment(start, "math", "quiz", "First", "30", "50"),
                    assessment(end, "Math", "Exam", "Exam", "70", "100"),
                    assessment(end, "English", "Quiz", "Essay", "5", "10"),
                    assessment(end.plusDays(1), "Math", "Quiz", "Outside", "0", "10")));

        String report = StatisticsReport.student(book, alice, start, end);

        assertTrue(report.contains("completed: 1\nabsent: 1\ncancelled: 1\nunrecorded: 1"));
        assertTrue(report.contains("Attendance rate: 50.00%"));
        assertTrue(report.contains("2026-01-01 | math | quiz | First | 60.00%"));
        assertTrue(report.indexOf("First") < report.indexOf("Last"));
        assertTrue(report.contains("Change math / quiz: 20.00 percentage points"));
        assertTrue(report.contains("Change math / exam: N/A"));
        assertTrue(report.contains("Change english / quiz: N/A"));
        assertFalse(report.contains("Outside"));
    }

    @Test
    public void student_zeroDenominatorAndNoAssessments_showNotApplicable() {
        AddressBook book = new AddressBook();
        book.setHistory(List.of(lesson(alice, start, Outcome.CANCELLED),
                lesson(alice, start, Outcome.UNRECORDED)), List.of());
        String report = StatisticsReport.student(book, alice, start, end);
        assertTrue(report.contains("Attendance rate: N/A"));
        assertTrue(report.contains("Percentage-point change: N/A"));
    }

    @Test
    public void assessment_changeRoundsOnlyAfterSubtraction_andAllowsDecline() {
        AddressBook book = new AddressBook();
        book.setHistory(List.of(), List.of(assessment(start, "Math", "Quiz", "First", "2", "3"),
                assessment(end, "Math", "Quiz", "Last", "1", "3")));
        assertTrue(StatisticsReport.student(book, alice, start, end)
                .contains("Change math / quiz: -33.33 percentage points"));
    }

    @Test
    public void sameDateAssessments_keepRecordedOrder() {
        // IDs are random UUIDs, so give the first-recorded mark the larger ID to prove ID order is not used.
        AssessmentRecord first = new AssessmentRecord("00000000-0000-0000-0000-000000000002",
                alice.getStudentId(), "Alice", start, "Math", "Quiz", "First", new BigDecimal("50"),
                new BigDecimal("100"));
        AssessmentRecord last = new AssessmentRecord("00000000-0000-0000-0000-000000000001",
                alice.getStudentId(), "Alice", start, "Math", "Quiz", "Last", new BigDecimal("80"),
                new BigDecimal("100"));
        AddressBook book = new AddressBook();
        book.setHistory(List.of(), List.of(first, last));
        String report = StatisticsReport.student(book, alice, start, start);
        assertTrue(report.indexOf("First") < report.indexOf("Last"));
        assertTrue(report.contains("Change math / quiz: 30.00 percentage points"));
    }

    @Test
    public void records_validateDurationsAndMarks_andHistoryIsImmutable() {
        assertThrows(IllegalArgumentException.class, () -> new LessonRecord(UUID.randomUUID().toString(),
                alice.getStudentId(), "Alice", "Math", start, LocalTime.NOON, LocalTime.NOON, Outcome.COMPLETED));
        assertThrows(IllegalArgumentException.class, () -> assessment(start, "Math", "Quiz", "Bad", "11", "10"));
        assertThrows(IllegalArgumentException.class, () -> assessment(start, "Math", "Quiz", "Bad", "-1", "10"));
        assertThrows(IllegalArgumentException.class, () -> assessment(start, "Math", "Quiz", "Bad", "0", "0"));
        AddressBook book = new AddressBook();
        book.setHistory(List.of(lesson(alice, start, Outcome.COMPLETED)), List.of());
        assertEquals(90, book.getLessonRecords().getFirst().minutes());
        assertThrows(UnsupportedOperationException.class, () -> book.getLessonRecords().clear());
    }

    private LessonRecord lesson(Person student, LocalDate date, Outcome outcome) {
        return new LessonRecord(UUID.randomUUID().toString(), student.getStudentId(), student.getName().fullName,
                "Math", date, LocalTime.of(10, 0), LocalTime.of(11, 30), outcome);
    }

    private AssessmentRecord assessment(LocalDate date, String subject, String category, String title,
            String score, String maximum) {
        return new AssessmentRecord(UUID.randomUUID().toString(), alice.getStudentId(), alice.getName().fullName,
                date, subject, category, title, new BigDecimal(score), new BigDecimal(maximum));
    }
}
