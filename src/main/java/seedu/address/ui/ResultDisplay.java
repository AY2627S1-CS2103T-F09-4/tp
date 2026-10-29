package seedu.address.ui;

import static java.util.Objects.requireNonNull;

import java.util.logging.Logger;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import seedu.address.commons.core.LogsCenter;

/**
 * A UI component that displays the result of a command execution.
 */
public class ResultDisplay extends UiPart<Region> {

    private static final String FXML = "ResultDisplay.fxml";

    private final Logger logger = LogsCenter.getLogger(ResultDisplay.class);

    @FXML
    private TextArea resultDisplay;

    /**
     * Creates a result display whose text can be scrolled from the keyboard.
     */
    public ResultDisplay() {
        super(FXML);
        resultDisplay.addEventFilter(KeyEvent.KEY_PRESSED, this::handleScrollKey);
    }

    public void setFeedbackToUser(String feedbackToUser) {
        requireNonNull(feedbackToUser);
        resultDisplay.setText(feedbackToUser);
    }

    /**
     * Scrolls the result text from the keyboard: up and down arrows move by one line, Page Up and Page Down by
     * one screen, and Home and End jump to the start and end. The text cannot be edited, so without this these
     * keys would move an invisible caret and only scroll once it left the visible lines.
     * Keys held with Shift, Ctrl or Alt are left alone so text can still be selected and copied.
     */
    void handleScrollKey(KeyEvent event) {
        requireNonNull(event);
        if (event.isShiftDown() || event.isShortcutDown() || event.isAltDown()) {
            return;
        }
        ScrollPane textScroll = (ScrollPane) resultDisplay.lookup(".scroll-pane");
        if (textScroll == null) {
            logger.warning("The result display has no scroll pane yet, so it cannot be scrolled");
            return;
        }
        double lineHeight = lineHeight();
        double screenHeight = Math.max(lineHeight, textScroll.getViewportBounds().getHeight() - lineHeight);
        double bottom = Math.max(0, textScroll.getContent().getLayoutBounds().getHeight()
                - textScroll.getViewportBounds().getHeight());
        switch (event.getCode()) {
            case UP -> scrollTo(resultDisplay.getScrollTop() - lineHeight, bottom);
            case DOWN -> scrollTo(resultDisplay.getScrollTop() + lineHeight, bottom);
            case PAGE_UP -> scrollTo(resultDisplay.getScrollTop() - screenHeight, bottom);
            case PAGE_DOWN -> scrollTo(resultDisplay.getScrollTop() + screenHeight, bottom);
            case HOME -> scrollTo(0, bottom);
            case END -> scrollTo(bottom, bottom);
            default -> {
                return;
            }
        }
        logger.fine("Scrolled the result display with " + event.getCode());
        event.consume();
    }

    /** Scrolls the text so that {@code top} pixels are above the view, stopping at its start and {@code bottom}. */
    private void scrollTo(double top, double bottom) {
        assert bottom >= 0 : "The bottom of the text cannot be above its start";
        resultDisplay.setScrollTop(Math.max(0, Math.min(bottom, top)));
        assert resultDisplay.getScrollTop() >= 0 && resultDisplay.getScrollTop() <= bottom
                : "Scrolling left the result text";
    }

    private double lineHeight() {
        Text line = new Text("X");
        line.setFont(resultDisplay.getFont());
        return line.getLayoutBounds().getHeight();
    }
}
