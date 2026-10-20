package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.model.schedule.TimeSlot;

/**
 * Removes a scheduled session of a student, identified by displayed index, matching an exact day and time slot.
 * If duplicates exist, only one is removed.
 */
public class UnscheduleStudentCommand extends Command {

    public static final String COMMAND_WORD = "unschedule-student";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes a scheduled session from the student identified by the index number used in the displayed "
            + "student list. The day and time slot must match the scheduled session exactly.\n"
            + "Parameters: INDEX day/DAY from/HH-mm to/HH-mm\n"
            + "Example: " + COMMAND_WORD + " 1 day/monday from/10-00 to/12-00";

    public static final String MESSAGE_UNSCHEDULE_STUDENT_SUCCESS = "Unscheduled %1$s on %2$s from %3$s to %4$s";
    public static final String MESSAGE_SESSION_NOT_FOUND = "%1$s is not scheduled on %2$s from %3$s to %4$s.";

    private final Index targetIndex;
    private final DayOfWeek day;
    private final TimeSlot timeSlot;

    /**
     * Creates an UnscheduleStudentCommand for the student at {@code targetIndex}.
     */
    public UnscheduleStudentCommand(Index targetIndex, DayOfWeek day, TimeSlot timeSlot) {
        requireAllNonNull(targetIndex, day, timeSlot);
        this.targetIndex = targetIndex;
        this.day = day;
        this.timeSlot = timeSlot;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person student = lastShownList.get(targetIndex.getZeroBased());
        ScheduledSession session = new ScheduledSession(student, day, timeSlot);
        String dayName = day.name().charAt(0) + day.name().substring(1).toLowerCase(Locale.ROOT);

        if (!model.hasScheduledSession(session)) {
            throw new CommandException(String.format(MESSAGE_SESSION_NOT_FOUND,
                    student.getName(), dayName, timeSlot.getStartString(), timeSlot.getEndString()));
        }

        model.deleteScheduledSession(session);
        return new CommandResult(String.format(MESSAGE_UNSCHEDULE_STUDENT_SUCCESS,
                student.getName(), dayName, timeSlot.getStartString(), timeSlot.getEndString()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UnscheduleStudentCommand otherCommand)) {
            return false;
        }
        return targetIndex.equals(otherCommand.targetIndex)
                && day == otherCommand.day
                && timeSlot.equals(otherCommand.timeSlot);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .add("day", day)
                .add("timeSlot", timeSlot)
                .toString();
    }
}
