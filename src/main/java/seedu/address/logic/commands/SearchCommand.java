package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.ParentContainsKeywordsPredicate;
import seedu.address.model.person.PersonContainsKeywordsPredicate;

/**
 * Searches for and lists all students and parents whose attributes contains any of the argument keywords.
 * Keyword matching is case-insensitive and searches for exact phrases.
 */
public class SearchCommand extends Command {

    public static final String COMMAND_WORD = "search";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Searches for all persons whose attributes contain "
            + "any of the specified keywords (case-insensitive) and displays them as a list with index numbers"
            + "and highlights the keywords.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    private final PersonContainsKeywordsPredicate studentPredicate;
    private final ParentContainsKeywordsPredicate parentPredicate;
    private final String keyword;

    /**
     * Creates a SearchCommand
     *
     * @param studentPredicate the predicate used to filter the resulting list of Persons.
     * @param parentPredicate the predicate used to filter the resulting list of Parents.
     * @param keyword the keyword to search with.
     */
    public SearchCommand(PersonContainsKeywordsPredicate studentPredicate,
                         ParentContainsKeywordsPredicate parentPredicate, String keyword) {
        this.studentPredicate = studentPredicate;
        this.parentPredicate = parentPredicate;
        this.keyword = keyword;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(studentPredicate);
        model.updateParentList(parentPredicate);
        String message = String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size())
                + " " + String.format(Messages.MESSAGE_PARENTS_LISTED_OVERVIEW, model.getParentContactList().size());
        return new CommandResult(message, keyword);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof SearchCommand otherSearchCommand)) {
            return false;
        }

        return studentPredicate.equals(otherSearchCommand.studentPredicate)
                && parentPredicate.equals(otherSearchCommand.parentPredicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("studentPredicate", studentPredicate)
                .add("parentPredicate", parentPredicate)
                .toString();
    }
}

