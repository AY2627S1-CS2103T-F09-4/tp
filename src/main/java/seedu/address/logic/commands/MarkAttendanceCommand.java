package seedu.address.logic.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.statistics.LessonRecord;
import seedu.address.model.statistics.LessonRecord.Outcome;

/**
 * Records an explicit outcome for one dated occurrence of a recurring session.
 * For a group session, the same outcome is recorded for every student in it.
 */
public class MarkAttendanceCommand extends Command {
    public static final String COMMAND_WORD = "mark-attendance";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + " SESSION_NUMBER date/YYYY-MM-DD status/(completed|absent|cancelled|unrecorded)";

    private final Index sessionIndex;
    private final LocalDate date;
    private final Outcome outcome;

    /** Creates an attendance update using the current complete schedule's displayed session number. */
    public MarkAttendanceCommand(Index sessionIndex, LocalDate date, Outcome outcome) {
        this.sessionIndex = Objects.requireNonNull(sessionIndex);
        this.date = Objects.requireNonNull(date);
        this.outcome = Objects.requireNonNull(outcome);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        Objects.requireNonNull(model);
        if (sessionIndex.getZeroBased() >= model.getAddressBook().getScheduleList().size()) {
            throw new CommandException("Invalid session number. Run list-schedule and choose a number from that list.");
        }
        ScheduledSession session = model.getAddressBook().getScheduleList().get(sessionIndex.getZeroBased());
        if (date.isAfter(LocalDate.now(model.getClock()))) {
            throw new CommandException("Attendance cannot be recorded for a future date.");
        }
        if (date.getDayOfWeek() != session.getDay()) {
            throw new CommandException("Date must fall on " + session.getDay() + " for this scheduled session.");
        }
        ReadOnlyAddressBook book = model.getAddressBook();
        ArrayList<LessonRecord> lessons = new ArrayList<>(book.getLessonRecords());
        Set<String> scheduledSessionIds = book.getScheduleList().stream()
                .map(ScheduledSession::getSessionId)
                .collect(Collectors.toSet());
        // Every student in a group session gets the same outcome, recorded as their own lesson.
        List<LessonRecord> updatedLessons = new ArrayList<>();
        for (Person student : session.getStudents()) {
            updatedLessons.add(recordFor(student, session, lessons, scheduledSessionIds));
        }
        model.setHistory(lessons, book.getAssessmentRecords());

        LessonRecord first = updatedLessons.getFirst();
        String lessonNumbers = updatedLessons.stream()
                .map(lesson -> "#" + (lessons.indexOf(lesson) + 1))
                .collect(Collectors.joining(", "));
        return new CommandResult("Attendance recorded for " + ScheduleStudentCommand.formatNames(session.getStudents())
                + " on " + date + " " + first.start() + "-" + first.end() + ": "
                + outcome.name().toLowerCase(Locale.ROOT)
                + " (" + (updatedLessons.size() == 1 ? "lesson " : "lessons ") + lessonNumbers + ").");
    }

    /**
     * Creates or updates, in {@code lessons}, the lesson of {@code student} for this command's date in
     * {@code session}, and returns that lesson.
     *
     * @param student The student whose lesson is recorded.
     * @param session The session being marked; it must contain {@code student}.
     * @param lessons All lessons, changed in place.
     * @param scheduledSessionIds IDs of every session currently in the schedule.
     */
    private LessonRecord recordFor(Person student, ScheduledSession session, List<LessonRecord> lessons,
            Set<String> scheduledSessionIds) {
        assert session.hasStudent(student) : "attendance is only recorded for students in the session";
        String sessionId = session.getSessionId();
        LessonRecord old = lessons.stream()
                .filter(r -> sessionId.equals(r.sourceSessionId()) && student.getStudentId().equals(r.studentId())
                        && date.equals(r.date()))
                .findFirst()
                .or(() -> findLessonOfRemovedSession(student, session, lessons, scheduledSessionIds))
                .orElse(null);
        if (old == null) {
            LessonRecord created = new LessonRecord(UUID.randomUUID().toString(), student.getStudentId(),
                    student.getName().fullName, student.getSubject().value, date, session.getTimeSlot().start,
                    session.getTimeSlot().end, outcome, sessionId);
            lessons.add(created);
            return created;
        }
        // Linking to the current session means later markings of this date find the lesson directly.
        LessonRecord updated = new LessonRecord(old.id(), old.studentId(), old.studentName(), old.subject(),
                old.date(), old.start(), old.end(), outcome, sessionId);
        lessons.set(lessons.indexOf(old), updated);
        return updated;
    }

    /**
     * Returns the lesson of {@code student} on this command's date, at the same times as {@code session}, that was
     * recorded from a session which is no longer scheduled. This is the same lesson marked again after the session
     * was unscheduled and rescheduled at the same time, so it is updated rather than duplicated. Lessons that were
     * never linked to a session are not matched.
     */
    private Optional<LessonRecord> findLessonOfRemovedSession(Person student, ScheduledSession session,
            List<LessonRecord> lessons, Set<String> scheduledSessionIds) {
        return lessons.stream()
                .filter(r -> r.sourceSessionId() != null && !scheduledSessionIds.contains(r.sourceSessionId()))
                .filter(r -> student.getStudentId().equals(r.studentId()) && date.equals(r.date()))
                .filter(r -> session.getTimeSlot().start.equals(r.start())
                        && session.getTimeSlot().end.equals(r.end()))
                .findFirst();
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof MarkAttendanceCommand command
                && sessionIndex.equals(command.sessionIndex) && date.equals(command.date) && outcome == command.outcome;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionIndex, date, outcome);
    }
}
