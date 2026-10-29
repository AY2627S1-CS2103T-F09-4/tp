package seedu.address.logic.commands;

import java.util.stream.Collectors;

import seedu.address.model.Model;
import seedu.address.model.schedule.ScheduledSession;

/** Lists all recurring sessions with the displayed numbers used to mark attendance. */
public class ListScheduleCommand extends Command {
    public static final String COMMAND_WORD = "list-schedule";

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public CommandResult execute(Model model) {
        StringBuilder result = new StringBuilder("Weekly schedule:");
        int number = 1;
        for (ScheduledSession session : model.getAddressBook().getScheduleList()) {
            String subjects = session.getStudents().stream()
                    .map(student -> student.getSubject().toString())
                    .distinct()
                    .collect(Collectors.joining(", "));
            result.append("\n").append(number++).append(". ")
                    .append(ScheduleStudentCommand.formatNames(session.getStudents())).append(" | ")
                    .append(subjects).append(" | ").append(session.getDay())
                    .append(" | ").append(session.getTimeSlot());
        }
        if (model.getAddressBook().getScheduleList().isEmpty()) {
            result.append(" none");
        }
        return new CommandResult(result.toString());
    }
}
