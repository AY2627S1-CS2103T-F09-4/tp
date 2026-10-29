package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * A short description of a command, shown to the user by the {@code help} command.
 *
 * @param commandWord the word that invokes the command.
 * @param format the parameters of the command, or an empty string if it takes none.
 * @param description what the command does.
 */
public record CommandSummary(String commandWord, String format, String description) {

    public static final String MESSAGE_CONSTRAINTS =
            "Command word must be a single word, and description must not be blank.";

    /** A command word is one or more characters with no whitespace, e.g. "add-student". */
    public static final String COMMAND_WORD_REGEX = "\\S+";

    /**
     * Every field must be present and not null.
     *
     * @throws IllegalArgumentException if {@code commandWord} is not a single word or {@code description} is blank.
     */
    public CommandSummary {
        requireNonNull(commandWord);
        requireNonNull(format);
        requireNonNull(description);
        checkArgument(commandWord.matches(COMMAND_WORD_REGEX), MESSAGE_CONSTRAINTS);
        checkArgument(!description.isBlank(), MESSAGE_CONSTRAINTS);
    }
}
