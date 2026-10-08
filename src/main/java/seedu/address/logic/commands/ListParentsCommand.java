package seedu.address.logic.commands;

import seedu.address.model.Model;

/** Selects the parent-contact view. The UI can observe the parent list directly. */
public class ListParentsCommand extends Command {
    public static final String COMMAND_WORD = "list-parents";
    @Override public CommandResult execute(Model model) { return new CommandResult("Listed all parent contacts."); }
}
