package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.SearchCommand;
import seedu.address.model.person.ParentContainsKeywordsPredicate;
import seedu.address.model.person.PersonContainsKeywordsPredicate;

public class SearchCommandParserTest {

    private SearchCommandParser parser = new SearchCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, SearchCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsSearchCommand() {
        // no leading and trailing whitespaces
        SearchCommand expectedSearchCommand =
                new SearchCommand(new PersonContainsKeywordsPredicate(List.of("Alice Bob")),
                        new ParentContainsKeywordsPredicate(List.of("Alice Bob")), "Alice Bob");
        assertParseSuccess(parser, "Alice Bob", expectedSearchCommand);

        // Multiple whitespaces between words are preserved internally,
        // but leading and trailing whitespace is removed
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t",
                new SearchCommand(
                        new PersonContainsKeywordsPredicate(List.of("Alice \n \t Bob")),
                        new ParentContainsKeywordsPredicate(List.of("Alice \n \t Bob")),
                        "Alice \n \t Bob"));
    }

}
