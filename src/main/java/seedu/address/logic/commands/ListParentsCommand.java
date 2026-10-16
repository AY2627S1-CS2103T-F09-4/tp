package seedu.address.logic.commands;

import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PARENTS;

import seedu.address.model.Model;

/** Selects the parent-contact view. The UI can observe the parent list directly. */
public class ListParentsCommand extends Command {
    public static final String COMMAND_WORD = "list-parents";
    @Override public CommandResult execute(Model model) {
        model.updateParentList(PREDICATE_SHOW_ALL_PARENTS);
        return new CommandResult("Listed all parent contacts.");
    }
}
