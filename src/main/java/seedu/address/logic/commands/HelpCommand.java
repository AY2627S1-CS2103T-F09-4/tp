package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.model.Model;

/**
 * Formats full help instructions for every command for display.
 */
public class HelpCommand extends Command {

    public static final String COMMAND_WORD = "help";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Shows program usage instructions.\n"
            + "Example: " + COMMAND_WORD;

    public static final String SHOWING_HELP_MESSAGE = "Showing all commands in the Commands tab.";

    /** The format and function of every command, in the order they are shown to the user. */
    public static final List<CommandSummary> COMMAND_SUMMARIES = List.of(
            new CommandSummary(AddCommand.COMMAND_WORD,
                    "n/NAME p/PHONE e/EMAIL a/ADDRESS s/SUBJECT [g/GRADE_LEVEL] [t/TAG]...",
                    "Adds a student."),
            new CommandSummary(AddContactCommand.COMMAND_WORD,
                    "n/NAME p/PHONE [e/EMAIL] [link/STUDENT_INDEX] [r/REQUIREMENT]...",
                    "Adds a parent, optionally linked to a student."),
            new CommandSummary(ListCommand.COMMAND_WORD, "",
                    "Lists all students."),
            new CommandSummary(ListParentsCommand.COMMAND_WORD, "",
                    "Lists all parents."),
            new CommandSummary(EditStudentCommand.COMMAND_WORD,
                    "INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [s/SUBJECT] [g/GRADE_LEVEL] [t/TAG]...",
                    "Edits the details of a student."),
            new CommandSummary(EditParentCommand.COMMAND_WORD,
                    "INDEX [n/NAME] [p/PHONE] [e/EMAIL] [link/STUDENT_INDEX] [r/REQUIREMENT]...",
                    "Edits a parent. Empty link/ unlinks; r/ replaces requirements, or clears them if empty."),
            new CommandSummary(AddPhotoCommand.COMMAND_WORD, "INDEX f/FILE_PATH",
                    "Adds or replaces the photo of a student."),
            new CommandSummary(AddParentPhotoCommand.COMMAND_WORD, "INDEX f/FILE_PATH",
                    "Adds or replaces the photo of a parent."),
            new CommandSummary(DeletePhotoCommand.COMMAND_WORD, "INDEX",
                    "Deletes the photo of a student."),
            new CommandSummary(DeleteParentPhotoCommand.COMMAND_WORD, "INDEX",
                    "Deletes the photo of a parent."),
            new CommandSummary(AddTaskCommand.COMMAND_WORD, "INDEX task/DESCRIPTION",
                    "Adds a task to a student."),
            new CommandSummary(MarkTaskCommand.COMMAND_WORD, "INDEX tid/TASK_ID status/(done|not done)",
                    "Marks a task of a student as done or not done."),
            new CommandSummary(DeleteTaskCommand.COMMAND_WORD, "INDEX tid/TASK_ID",
                    "Deletes a task of a student."),
            new CommandSummary(ScheduleStudentCommand.COMMAND_WORD, "INDEX day/DAY from/HH-mm to/HH-mm",
                    "Schedules a weekly session for a student."),
            new CommandSummary(UnscheduleStudentCommand.COMMAND_WORD, "INDEX day/DAY from/HH-mm to/HH-mm",
                    "Removes a scheduled session from a student."),
            new CommandSummary(ListScheduleCommand.COMMAND_WORD, "",
                    "Lists all weekly sessions, numbered for use with mark-attendance."),
            new CommandSummary(MarkAttendanceCommand.COMMAND_WORD,
                    "SESSION_NUMBER date/YYYY-MM-DD status/(completed|absent|cancelled|unrecorded)",
                    "Records the outcome of a session on a given date."),
            new CommandSummary("list-lessons", "",
                    "Lists all recorded lessons."),
            new CommandSummary("record-assessment",
                    "INDEX date/YYYY-MM-DD subject/SUBJECT category/CATEGORY title/TITLE score/SCORE max/MAX",
                    "Records an assessment result for a student."),
            new CommandSummary("list-assessments", "",
                    "Lists all recorded assessments."),
            new CommandSummary("edit-assessment",
                    "RECORD_NUMBER date/YYYY-MM-DD subject/SUBJECT category/CATEGORY title/TITLE "
                            + "score/SCORE max/MAX",
                    "Replaces the details of an assessment record."),
            new CommandSummary("delete-assessment", "RECORD_NUMBER",
                    "Deletes an assessment record."),
            new CommandSummary("stats", "[from/YYYY-MM-DD to/YYYY-MM-DD]",
                    "Shows overall statistics, from the start of the year by default."),
            new CommandSummary("stats-student", "INDEX [from/YYYY-MM-DD to/YYYY-MM-DD]",
                    "Shows the progress of a student, from the start of the year by default."),
            new CommandSummary(AddReminderCommand.COMMAND_WORD, "r/REMINDER_TEXT [at/YYYY-MM-DD HH:mm]",
                    "Adds a reminder, optionally due at a given time."),
            new CommandSummary(ListRemindersCommand.COMMAND_WORD, "",
                    "Lists all reminders."),
            new CommandSummary(DeleteReminderCommand.COMMAND_WORD, "ID",
                    "Deletes a reminder."),
            new CommandSummary(SearchCommand.COMMAND_WORD, "KEYWORD [MORE_KEYWORDS]...",
                    "Searches all details of students and parents for the keywords."),
            new CommandSummary(DeleteCommand.COMMAND_WORD, "INDEX",
                    "Deletes a student."),
            new CommandSummary(DeleteParentCommand.COMMAND_WORD, "INDEX",
                    "Deletes a parent."),
            new CommandSummary(UndoCommand.COMMAND_WORD, "",
                    "Undoes the last command."),
            new CommandSummary(RedoCommand.COMMAND_WORD, "",
                    "Reverses the last undo."),
            new CommandSummary(ClearCommand.COMMAND_WORD, "",
                    "Deletes all entries."),
            new CommandSummary(COMMAND_WORD, "",
                    "Shows this list of commands."),
            new CommandSummary(ExitCommand.COMMAND_WORD, "",
                    "Exits the program."));

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        assert !COMMAND_SUMMARIES.isEmpty() : "There must be at least one command to show";
        return new CommandResult(SHOWING_HELP_MESSAGE, true, false, false, false);
    }
}
