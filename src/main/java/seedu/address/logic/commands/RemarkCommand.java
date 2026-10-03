package seedu.address.logic.commands;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CliSyntax.PREFIX_REMARK;
import java.util.List;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
public class RemarkCommand extends Command {
    public static final String COMMAND_WORD = "remark";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds or edits a remark. Parameters: INDEX " + PREFIX_REMARK + "REMARK";
    public static final String MESSAGE_SUCCESS = "Updated remark for Person: %1$s";
    private final Index index; private final Remark remark;
    public RemarkCommand(Index index, String remark) { requireAllNonNull(index, remark); this.index = index; this.remark = new Remark(remark); }
    @Override public CommandResult execute(Model model) throws CommandException {
        List<Person> persons = model.getFilteredPersonList();
        if (index.getZeroBased() >= persons.size()) throw new CommandException(MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        Person old = persons.get(index.getZeroBased());
        Person updated = new Person(old.getName(), old.getPhone(), old.getEmail(), old.getAddress(), remark, old.getTags());
        model.setPerson(old, updated);
        return new CommandResult(String.format(MESSAGE_SUCCESS, updated));
    }
    @Override public boolean equals(Object other) { return other == this || (other instanceof RemarkCommand && index.equals(((RemarkCommand) other).index) && remark.equals(((RemarkCommand) other).remark)); }
}
