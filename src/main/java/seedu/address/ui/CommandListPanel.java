package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.CommandSummary;

/**
 * Panel containing the list of commands, shown by the {@code help} command.
 */
public class CommandListPanel extends UiPart<Region> {
    static final String LINK_COPIED_TEXT = "user guide (link copied to clipboard)";

    private static final String FXML = "CommandListPanel.fxml";
    private final Logger logger = LogsCenter.getLogger(CommandListPanel.class);

    private final Optional<Browser> browser;
    private final Consumer<String> clipboard;
    private final Executor browserLauncher;

    @FXML
    private Hyperlink userGuideLink;

    @FXML
    private ListView<CommandSummary> commandListView;

    /**
     * Opens a web page.
     */
    @FunctionalInterface
    interface Browser {
        /**
         * Opens the page at {@code uri}.
         *
         * @throws IOException if the page could not be opened.
         */
        void browse(URI uri) throws IOException;
    }

    /**
     * Creates a {@code CommandListPanel} with the given list of {@code CommandSummary}.
     */
    public CommandListPanel(List<CommandSummary> commandSummaries) {
        this(commandSummaries, getSystemBrowser(), CommandListPanel::copyToSystemClipboard,
                CommandListPanel::launchInBackground);
    }

    /**
     * Creates a {@code CommandListPanel} that opens the user guide with {@code browser}, or copies its URL with
     * {@code clipboard} if there is no browser or it fails. {@code browserLauncher} decides which thread the
     * browser is opened on.
     */
    CommandListPanel(List<CommandSummary> commandSummaries, Optional<Browser> browser,
            Consumer<String> clipboard, Executor browserLauncher) {
        super(FXML);
        requireNonNull(commandSummaries);
        requireNonNull(browser);
        requireNonNull(clipboard);
        requireNonNull(browserLauncher);
        assert !commandSummaries.isEmpty() : "Command list should not be empty";

        this.browser = browser;
        this.clipboard = clipboard;
        this.browserLauncher = browserLauncher;

        // Unmodifiable so the list shown to the user cannot be changed through the ListView
        commandListView.setItems(FXCollections.unmodifiableObservableList(
                FXCollections.observableArrayList(commandSummaries)));
        commandListView.setCellFactory(listView -> new CommandListViewCell());
        logger.fine("Created command list with " + commandSummaries.size() + " commands.");
    }

    /**
     * Opens the user guide in the browser.
     * If no browser can be opened, copies the URL to the user guide to the clipboard instead.
     */
    @FXML
    void openUserGuide() {
        userGuideLink.setVisited(false);
        if (browser.isEmpty()) {
            logger.info("Opening a browser is not supported on this system.");
            copyUserGuideUrl();
            return;
        }

        logger.info("Opening the user guide: " + HelpWindow.USERGUIDE_URL);
        browserLauncher.execute(() -> {
            try {
                browser.get().browse(URI.create(HelpWindow.USERGUIDE_URL));
            } catch (IOException | SecurityException | UnsupportedOperationException e) {
                logger.warning("Unable to open the user guide: " + e.getMessage());
                Platform.runLater(this::copyUserGuideUrl);
            }
        });
    }

    private void copyUserGuideUrl() {
        assert Platform.isFxApplicationThread() : "The clipboard can only be used on the JavaFX Application Thread";
        clipboard.accept(HelpWindow.USERGUIDE_URL);
        userGuideLink.setText(LINK_COPIED_TEXT);
        logger.info("Copied the user guide URL to the clipboard instead.");
    }

    private static Optional<Browser> getSystemBrowser() {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            return Optional.empty();
        }
        return Optional.of(Desktop.getDesktop()::browse);
    }

    static void copyToSystemClipboard(String text) {
        final ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

    /**
     * Runs {@code task} on a new thread, as {@code Desktop#browse} can block the JavaFX Application Thread.
     */
    static void launchInBackground(Runnable task) {
        Thread browserThread = new Thread(task);
        // Prevents a slow browser launch from keeping the app open after it exits
        browserThread.setDaemon(true);
        browserThread.start();
    }

    /**
     * Custom {@code ListCell} that displays a {@code CommandSummary} using a {@code CommandCard}.
     */
    class CommandListViewCell extends ListCell<CommandSummary> {
        @Override
        protected void updateItem(CommandSummary commandSummary, boolean empty) {
            super.updateItem(commandSummary, empty);

            if (empty || commandSummary == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(new CommandCard(commandSummary).getRoot());
            }
        }
    }

}
