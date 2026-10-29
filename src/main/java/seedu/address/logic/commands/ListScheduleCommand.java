package seedu.address.logic.commands;

import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import seedu.address.model.Model;
import seedu.address.model.schedule.ScheduledSession;

/** Lists chronological rows with separate session numbers used to mark attendance. */
public class ListScheduleCommand extends Command {
    public static final String COMMAND_WORD = "list-schedule";

    @Override
    public boolean isReadOnly() {
        return true;
    }

    @Override
    public CommandResult execute(Model model) {
        StringBuilder result = new StringBuilder("Weekly schedule:");
        List<ScheduledSession> sessions = model.getAddressBook().getScheduleList();
        List<Integer> indices = IntStream.range(0, sessions.size()).boxed()
                .sorted(Comparator.comparing((Integer i) -> sessions.get(i).getDay())
                        .thenComparing(i -> sessions.get(i).getTimeSlot().start)).toList();
        int rowNumber = 1;
        for (int index : indices) {
            ScheduledSession session = sessions.get(index);
            result.append("\n").append(rowNumber++).append(". ")
                    .append(session.getStudent().getName()).append(" | ")
                    .append(session.getStudent().getSubject()).append(" | ").append(session.getDay())
                    .append(" | ").append(session.getTimeSlot())
                    .append(" | Session #").append(index + 1);
        }
        if (model.getAddressBook().getScheduleList().isEmpty()) {
            result.append(" none");
        }
        return new CommandResult(result.toString());
    }
}
