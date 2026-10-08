package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EditParentCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/** Parses input arguments for editing a parent contact. */
public class EditParentCommandParser implements Parser<EditParentCommand> {
    @Override
    public EditParentCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL);
        Index index = ParserUtil.parseIndex(map.getPreamble());
        Name name = map.getValue(PREFIX_NAME).isPresent()
                ? ParserUtil.parseName(map.getValue(PREFIX_NAME).get()) : null;
        Phone phone = map.getValue(PREFIX_PHONE).isPresent()
                ? ParserUtil.parsePhone(map.getValue(PREFIX_PHONE).get()) : null;
        Email email = map.getValue(PREFIX_EMAIL).isPresent()
                ? ParserUtil.parseEmail(map.getValue(PREFIX_EMAIL).get()) : null;
        if (name == null && phone == null && email == null) {
            throw new ParseException("At least one field to edit must be provided.");
        }
        return new EditParentCommand(index, name, phone, email);
    }
}
