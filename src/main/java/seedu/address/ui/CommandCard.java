package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.CommandSummary;

/**
 * A UI component that displays the format and function of a command.
 */
public class CommandCard extends UiPart<Region> {

    private static final String FXML = "CommandListCard.fxml";

    @FXML
    private Label commandWord;
    @FXML
    private Label format;
    @FXML
    private Label description;

    /**
     * Creates a {@code CommandCard} with the given {@code CommandSummary} to display.
     */
    public CommandCard(CommandSummary commandSummary) {
        super(FXML);
        requireNonNull(commandSummary);
        assert commandWord != null && format != null && description != null : "FXML labels were not injected";

        commandWord.setText(commandSummary.commandWord());
        format.setText("Format: " + (commandSummary.commandWord() + " " + commandSummary.format()).trim());
        description.setText(commandSummary.description());
    }
}
