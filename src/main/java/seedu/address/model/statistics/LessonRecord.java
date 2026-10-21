package seedu.address.model.statistics;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.UUID;

/** An immutable dated lesson, independent of planned weekly sessions. */
public record LessonRecord(String id, String studentId, String studentName, String subject,
        LocalDate date, LocalTime start, LocalTime end, Outcome outcome, String sourceSessionId) {

    /** Creates an unlinked lesson recorded outside the recurring timetable. */
    public LessonRecord(String id, String studentId, String studentName, String subject,
            LocalDate date, LocalTime start, LocalTime end, Outcome outcome) {
        this(id, studentId, studentName, subject, date, start, end, outcome, null);
    }

    /** Validates structural constraints. Future dates are checked against the model clock by commands. */
    public LessonRecord {
        id = UUID.fromString(id).toString();
        studentId = UUID.fromString(studentId).toString();
        sourceSessionId = sourceSessionId == null ? null : UUID.fromString(sourceSessionId).toString();
        studentName = HistoryValidation.text(studentName);
        subject = HistoryValidation.text(subject);
        if (date == null || start == null || end == null || outcome == null || !start.isBefore(end)
                || start.getSecond() != 0 || end.getSecond() != 0 || start.getNano() != 0 || end.getNano() != 0) {
            throw new IllegalArgumentException("Lesson needs a date, status, and same-day start before end (HH:mm).");
        }
    }

    /** Returns the recorded duration in minutes without rounding. */
    public long minutes() {
        return Duration.between(start, end).toMinutes();
    }

    /** Explicit outcomes; planned sessions never imply an outcome. */
    public enum Outcome {
        COMPLETED, ABSENT, CANCELLED, UNRECORDED;

        /** Parses a case-insensitive outcome. */
        public static Outcome parse(String value) {
            try {
                return valueOf(value.toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status must be completed, absent, cancelled, or unrecorded.");
            }
        }
    }
}
