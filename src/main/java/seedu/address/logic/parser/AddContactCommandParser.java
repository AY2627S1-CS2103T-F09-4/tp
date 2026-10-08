package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.address.logic.parser.CliSyntax.PREFIX_LINK;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REQUIREMENT;

import java.util.ArrayList;
import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.AddContactCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Phone;

/** Parses input arguments for adding a parent contact. */
public class AddContactCommandParser implements Parser<AddContactCommand> {
    @Override
    public AddContactCommand parse(String args) throws ParseException {
        ArgumentMultimap map = ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_LINK, PREFIX_REQUIREMENT);
        if (!map.getPreamble().isEmpty() || map.getValue(PREFIX_NAME).isEmpty()
                || map.getValue(PREFIX_PHONE).isEmpty()) {
            throw new ParseException(AddContactCommand.MESSAGE_USAGE);
        }
        map.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_LINK);
        Name name = ParserUtil.parseName(map.getValue(PREFIX_NAME).get());
        Phone phone = ParserUtil.parsePhone(map.getValue(PREFIX_PHONE).get());
        Email email = map.getValue(PREFIX_EMAIL).isPresent()
                ? ParserUtil.parseEmail(map.getValue(PREFIX_EMAIL).get()) : null;
        Index link = map.getValue(PREFIX_LINK).isPresent()
                ? ParserUtil.parseIndex(map.getValue(PREFIX_LINK).get()) : null;
        List<String> requirements = new ArrayList<>(map.getAllValues(PREFIX_REQUIREMENT));
        return new AddContactCommand(new ParentContact(name, phone, email, null, requirements), link);
    }
}
