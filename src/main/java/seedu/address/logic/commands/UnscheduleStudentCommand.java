package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

/**
 * Removes students, identified by displayed index, from their scheduled session at an exact day and time slot.
 * Other students in a group session stay scheduled; a session with no students left is removed.
 * If a student has duplicate sessions at that time, they are removed from only the first one.
 */
public class UnscheduleStudentCommand extends Command {

    public static final String COMMAND_WORD = "unschedule-student";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes the students identified by the index numbers used in the displayed student list from "
            + "their scheduled session. The day and time slot must match the scheduled session exactly. "
            + "Other students in a group session stay scheduled.\n"
            + "Parameters: INDEX [MORE_INDEXES]... day/DAY from/HH-mm to/HH-mm\n"
            + "Example: " + COMMAND_WORD + " 1 day/monday from/10-00 to/12-00";

    public static final String MESSAGE_UNSCHEDULE_STUDENT_SUCCESS = "Unscheduled %1$s on %2$s from %3$s to %4$s";
    public static final String MESSAGE_SESSION_NOT_FOUND = "%1$s is not scheduled on %2$s from %3$s to %4$s.";

    private static final Logger logger = LogsCenter.getLogger(UnscheduleStudentCommand.class);

    private final List<Index> targetIndexes;
    private final DayOfWeek day;
    private final TimeSlot timeSlot;

    /**
     * Creates an UnscheduleStudentCommand for the students at {@code targetIndexes}.
     *
     * @param targetIndexes Displayed indexes of the students; must be non-empty and contain no duplicates.
     * @param day Day of the week of the session.
     * @param timeSlot Time slot of the session.
     */
    public UnscheduleStudentCommand(List<Index> targetIndexes, DayOfWeek day, TimeSlot timeSlot) {
        requireAllNonNull(targetIndexes, day, timeSlot);
        requireAllNonNull(targetIndexes);
        if (targetIndexes.isEmpty()) {
            throw new IllegalArgumentException("At least one student index is required.");
        }
        assert targetIndexes.stream().distinct().count() == targetIndexes.size()
                : "duplicate indexes should be rejected by the parser";
        this.targetIndexes = List.copyOf(targetIndexes);
        this.day = day;
        this.timeSlot = timeSlot;
    }

    /**
     * Creates an UnscheduleStudentCommand for the single student at {@code targetIndex}.
     */
    public UnscheduleStudentCommand(Index targetIndex, DayOfWeek day, TimeSlot timeSlot) {
        this(List.of(requireNonNull(targetIndex)), day, timeSlot);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> students = ScheduleStudentCommand.resolveStudents(model, targetIndexes);
        String dayName = ScheduleStudentCommand.formatDay(day);

        // Work out every change first, so that nothing is removed if any student is not scheduled.
        Map<ScheduledSession, List<Person>> remainingStudentsBySession = new LinkedHashMap<>();
        for (Person student : students) {
            ScheduledSession session = model.getScheduleList().stream()
                    .filter(s -> s.isAt(day, timeSlot))
                    .filter(s -> remainingStudentsBySession.getOrDefault(s, s.getStudents()).contains(student))
                    .findFirst()
                    .orElseThrow(() -> new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND,
                            student.getName(), dayName, timeSlot.getStartString(), timeSlot.getEndString())));
            List<Person> remainingStudents = remainingStudentsBySession.getOrDefault(session, session.getStudents())
                    .stream()
                    .filter(other -> !other.equals(student))
                    .toList();
            remainingStudentsBySession.put(session, remainingStudents);
        }

        for (Map.Entry<ScheduledSession, List<Person>> entry : remainingStudentsBySession.entrySet()) {
            ScheduledSession session = entry.getKey();
            List<Person> remainingStudents = entry.getValue();
            if (remainingStudents.isEmpty()) {
                model.deleteScheduledSession(session);
            } else {
                model.setScheduledSession(session, session.withStudents(remainingStudents));
            }
        }
        logger.info("Unscheduled " + students.size() + " student(s) on " + day + " " + timeSlot);

        return new CommandResult(String.format(MESSAGE_UNSCHEDULE_STUDENT_SUCCESS,
                ScheduleStudentCommand.formatNames(students), dayName, timeSlot.getStartString(),
                timeSlot.getEndString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnscheduleStudentCommand otherCommand)) {
            return false;
        }
        return targetIndexes.equals(otherCommand.targetIndexes)
                && day == otherCommand.day
                && timeSlot.equals(otherCommand.timeSlot);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndexes", targetIndexes)
                .add("day", day)
                .add("timeSlot", timeSlot)
                .toString();
    }
}
