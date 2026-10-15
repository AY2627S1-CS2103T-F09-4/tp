package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddContactCommand;
import seedu.address.logic.Messages;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class AddContactCommandParserTest {
    private final AddContactCommandParser parser = new AddContactCommandParser();

    @Test
    public void parse_validInput_success() throws Exception {
        assertTrue(parser.parse(" n/Mary Tan p/98765432 e/mary@example.com link/1"
                + " r/Prefers email r/Call before lessons") instanceof AddContactCommand);
    }

    @Test
    public void parse_missingRequiredField_failure() {
        String expected = AddContactCommand.MESSAGE_USAGE;
        assertParseFailure(parser, "p/98765432", expected);
        assertParseFailure(parser, "n/Mary Tan", expected);
        assertParseFailure(parser, "junk n/Mary Tan p/98765432", expected);
    }

    @Test
    public void parse_repeatedNonRequirementPrefix_failure() {
        String valid = " n/Mary Tan p/98765432";
        assertParseFailure(parser, valid + " n/Peter Tan", 
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NAME));
        assertParseFailure(parser, valid + " p/91234567", 
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE));
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, " n/Mary#Tan p/98765432", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Mary Tan p/12", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Mary Tan p/98765432 e/not-an-email", Email.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " n/Mary Tan p/98765432 link/not-index", ParserUtil.MESSAGE_INVALID_INDEX);
    }
}
