package seedu.address.logic.commands;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.statistics.LessonRecord;
import seedu.address.model.statistics.LessonRecord.Outcome;

/** Records an explicit outcome for one dated occurrence of a recurring session. */
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

    /** Adds consistent guidance to a specific attendance validation failure. */
    public static String withUsage(String reason) {
        return "Invalid mark-attendance: " + reason + "\nUsage: " + MESSAGE_USAGE
                + "\nUse Session #N from list-schedule, not the row number."
                + "\nExample: mark-attendance 1 date/2026-10-19 status/completed";
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        if (sessionIndex.getZeroBased() >= model.getAddressBook().getScheduleList().size()) {
            throw new CommandException(withUsage("Invalid session number. "
                    + "Run list-schedule and choose an existing Session #N."));
        }
        ScheduledSession session = model.getAddressBook().getScheduleList().get(sessionIndex.getZeroBased());
        String sessionId = session.getSessionId();
        if (date.isAfter(LocalDate.now(model.getClock()))) {
            throw new CommandException(withUsage("Attendance cannot be recorded for a future date."));
        }
        if (date.getDayOfWeek() != session.getDay()) {
            throw new CommandException(withUsage("Date must fall on " + session.getDay()
                    + " for this scheduled session."));
        }
        ReadOnlyAddressBook book = model.getAddressBook();
        ArrayList<LessonRecord> lessons = new ArrayList<>(book.getLessonRecords());
        LessonRecord old = lessons.stream().filter(r -> sessionId.equals(r.sourceSessionId()) && date.equals(r.date()))
                .findFirst().orElse(null);
        LessonRecord updated;
        if (old == null) {
            Person student = session.getStudent();
            updated = new LessonRecord(UUID.randomUUID().toString(), student.getStudentId(), student.getName().fullName,
                    student.getSubject().value, date, session.getTimeSlot().start, session.getTimeSlot().end,
                    outcome, sessionId);
        } else {
            updated = new LessonRecord(old.id(), old.studentId(), old.studentName(), old.subject(), old.date(),
                    old.start(), old.end(), outcome, old.sourceSessionId());
        }
        for (int i = 0; i < lessons.size(); i++) {
            LessonRecord other = lessons.get(i);
            if (old == null && other.studentId().equals(updated.studentId()) && other.date().equals(date)
                    && updated.start().equals(other.start()) && updated.end().equals(other.end())) {
                throw new CommandException(withUsage("Duplicate lesson #" + (i + 1) + " already exists on " + date
                        + " " + other.start() + "-" + other.end() + " for this student. "
                        + "Use list-lessons to review the existing record."));
            }
        }
        if (old == null) {
            lessons.add(updated);
        } else {
            lessons.set(lessons.indexOf(old), updated);
        }
        model.setHistory(lessons, book.getAssessmentRecords());
        return new CommandResult("Attendance recorded for " + session.getStudent().getName() + " on " + date
                + " " + updated.start() + "-" + updated.end() + ": " + outcome.name().toLowerCase(Locale.ROOT)
                + " (lesson #" + (lessons.indexOf(updated) + 1) + ").");
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
