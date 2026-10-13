package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.PersonContainsKeywordsPredicate;

/**
 * Searches for and lists all students and parents whose attributes contains any of the argument keywords.
 * Keyword matching is case insensitive and searches for exact phrases.
 */
public class SearchCommand extends Command {

    public static final String COMMAND_WORD = "search";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Searches for all persons whose attributes contain "
            + "any of the specified keywords (case-insensitive) and displays them as a list with index numbers"
            + "and highlights the keywords.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " alice bob charlie";

    private final PersonContainsKeywordsPredicate predicate;
    private final String keyword;

    /**
     * Creates a SearchCommand
     *
     * @param predicate the predicate used to filter the resulting list of Persons.
     * @param keyword the keyword to search with.
     */
    public SearchCommand(PersonContainsKeywordsPredicate predicate, String keyword) {
        this.predicate = predicate;
        this.keyword = keyword;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        return new CommandResult(
                String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, model.getFilteredPersonList().size()),
                keyword);
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

        return predicate.equals(otherSearchCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}

