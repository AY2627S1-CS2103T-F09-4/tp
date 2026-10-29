package seedu.address.model.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.statistics.LessonRecord.Outcome;

/** Pure text reports derived from source history, never from the weekly timetable or UI filter. */
public final class StatisticsReport {
    private StatisticsReport() {
    }

    /** Formats business workload and monthly completed workload. */
    public static String business(ReadOnlyAddressBook book, LocalDate from, LocalDate to) {
        List<LessonRecord> completed = book.getLessonRecords().stream()
                .filter(r -> within(r.date(), from, to) && r.outcome() == Outcome.COMPLETED).toList();
        StringBuilder report = new StringBuilder("Business workload (not profitability) " + from + " to " + to);
        report.append("\n").append(workload(completed));
        report.append("\nMonthly breakdown:");
        for (YearMonth month = YearMonth.from(from); !month.isAfter(YearMonth.from(to)); month = month.plusMonths(1)) {
            YearMonth current = month;
            report.append("\n").append(month).append(": ").append(workload(completed.stream()
                    .filter(r -> YearMonth.from(r.date()).equals(current)).toList()));
        }
        return report.toString();
    }

    private static String workload(List<LessonRecord> lessons) {
        long minutes = lessons.stream().mapToLong(LessonRecord::minutes).sum();
        long students = lessons.stream().map(LessonRecord::studentId).distinct().count();
        return "Completed lessons: " + lessons.size() + "; teaching hours: "
                + BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP).toPlainString()
                + "; distinct students taught: " + students;
    }

    /** Formats attendance and chronologically ordered comparable assessments for one persistent identity. */
    public static String student(ReadOnlyAddressBook book, Person student, LocalDate from, LocalDate to) {
        List<LessonRecord> lessons = book.getLessonRecords().stream()
                .filter(r -> r.studentId().equals(student.getStudentId()) && within(r.date(), from, to)).toList();
        StringBuilder report = new StringBuilder("Student: " + student.getName() + " | " + from + " to " + to);
        for (Outcome outcome : Outcome.values()) {
            report.append("\n").append(outcome.name().toLowerCase(Locale.ROOT)).append(": ")
                    .append(count(lessons, outcome));
        }
        long completed = count(lessons, Outcome.COMPLETED);
        long denominator = completed + count(lessons, Outcome.ABSENT);
        report.append("\nAttendance rate: ").append(denominator == 0 ? "N/A"
                : BigDecimal.valueOf(completed * 100).divide(BigDecimal.valueOf(denominator), 2,
                        RoundingMode.HALF_UP).toPlainString() + "%");
        // Source history is kept in recorded order and sorted() is stable, so same-date marks keep that order.
        // IDs are random UUIDs and must not be used as a tie-break.
        List<AssessmentRecord> assessments = book.getAssessmentRecords().stream()
                .filter(r -> r.studentId().equals(student.getStudentId()) && within(r.date(), from, to))
                .sorted(Comparator.comparing(AssessmentRecord::date)).toList();
        report.append("\nAssessments:");
        if (assessments.isEmpty()) {
            report.append(" none\nPercentage-point change: N/A");
        }
        for (AssessmentRecord assessment : assessments) {
            report.append("\n").append(assessment.date()).append(" | ").append(assessment.subject())
                    .append(" | ").append(assessment.category()).append(" | ").append(assessment.title())
                    .append(" | ").append(decimal(assessment.percentage())).append("%");
        }
        Map<String, Map<String, List<AssessmentRecord>>> groups = assessments.stream().collect(Collectors.groupingBy(
                r -> r.subject().toLowerCase(Locale.ROOT), TreeMap::new, Collectors.groupingBy(
                    r -> r.category().toLowerCase(Locale.ROOT), TreeMap::new, Collectors.toList())));
        groups.forEach((subject, categories) -> categories.forEach((category, records) -> {
            String change = records.size() < 2 ? "N/A" : decimal(records.getLast().percentage()
                    .subtract(records.getFirst().percentage())) + " percentage points";
            AssessmentRecord first = records.getFirst();
            report.append("\nChange ").append(first.subject()).append(" / ").append(first.category())
                    .append(": ").append(change);
        }));
        return report.toString();
    }

    private static long count(List<LessonRecord> lessons, Outcome outcome) {
        return lessons.stream().filter(r -> r.outcome() == outcome).count();
    }

    private static boolean within(LocalDate date, LocalDate from, LocalDate to) {
        return !date.isBefore(from) && !date.isAfter(to);
    }

    private static String decimal(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
