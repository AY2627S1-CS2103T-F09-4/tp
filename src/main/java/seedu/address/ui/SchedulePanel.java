package seedu.address.ui;

import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;

/**
 * Displays the weekly schedule in the Schedule tab, with a legend mapping each student to a colour.
 */
public class SchedulePanel extends UiPart<Region> {
    private static final String FXML = "SchedulePanel.fxml";

    private static final String[] PALETTE = {
        "#f6a05a", "#6ccfcf", "#8fcf8f", "#f2cf63", "#c9a0d6",
        "#e8837a", "#7fb1e8", "#d6c08f", "#a3d9a5", "#e8a0c4"
    };

    private final Logger logger = LogsCenter.getLogger(SchedulePanel.class);
    private final ObservableList<ScheduledSession> sessions;
    private final ObservableList<Person> persons;
    private final ObservableList<Person> displayedPersons;
    private final ScheduleGrid grid = new ScheduleGrid();

    @FXML
    private ScrollPane scheduleScroll;

    @FXML
    private FlowPane legendPane;

    /**
     * Creates a panel that redraws whenever {@code sessions}, {@code persons} or {@code displayedPersons} change.
     * Card titles show each student's index in {@code displayedPersons}, the list that commands refer to.
     */
    public SchedulePanel(ObservableList<ScheduledSession> sessions, ObservableList<Person> persons,
            ObservableList<Person> displayedPersons) {
        super(FXML);
        this.sessions = sessions;
        this.persons = persons;
        this.displayedPersons = displayedPersons;
        scheduleScroll.setContent(grid);
        scheduleScroll.viewportBoundsProperty()
                .addListener((observable, oldBounds, newBounds) -> onViewportChanged());
        scheduleScroll.hvalueProperty().addListener((observable, oldValue, newValue) -> syncPinnedLabels());
        scheduleScroll.vvalueProperty().addListener((observable, oldValue, newValue) -> syncPinnedLabels());
        scheduleScroll.setFocusTraversable(true);
        scheduleScroll.addEventFilter(KeyEvent.KEY_PRESSED, this::handleScrollKey);

        sessions.addListener((ListChangeListener<ScheduledSession>) change -> refresh());
        persons.addListener((ListChangeListener<Person>) change -> refresh());
        displayedPersons.addListener((ListChangeListener<Person>) change -> refresh());
        refresh();
    }

    private void onViewportChanged() {
        Bounds bounds = scheduleScroll.getViewportBounds();
        grid.setViewport(bounds.getWidth(), bounds.getHeight());
        Platform.runLater(this::syncPinnedLabels);
    }

    private void syncPinnedLabels() {
        Bounds bounds = scheduleScroll.getViewportBounds();
        double scrollableWidth = Math.max(0, grid.prefWidth(-1) - bounds.getWidth());
        double scrollableHeight = Math.max(0, grid.prefHeight(-1) - bounds.getHeight());
        grid.setScrollOffsets(scheduleScroll.getHvalue() * scrollableWidth,
                scheduleScroll.getVvalue() * scrollableHeight);
    }

    /**
     * Scrolls the timetable from the keyboard: left and right arrows move by one hour, up and down arrows by
     * one row, Page Up and Page Down by one screen, and Home and End jump to the earliest and latest hours.
     * Other keys, such as Tab, are left alone so focus can still move on.
     */
    void handleScrollKey(KeyEvent event) {
        Bounds bounds = scheduleScroll.getViewportBounds();
        switch (event.getCode()) {
            case LEFT:
                scrollBy(-grid.hourWidth(), 0);
                break;
            case RIGHT:
                scrollBy(grid.hourWidth(), 0);
                break;
            case UP:
                scrollBy(0, -grid.rowHeight());
                break;
            case DOWN:
                scrollBy(0, grid.rowHeight());
                break;
            case PAGE_UP:
                scrollBy(0, -bounds.getHeight());
                break;
            case PAGE_DOWN:
                scrollBy(0, bounds.getHeight());
                break;
            case HOME:
                scheduleScroll.setHvalue(scheduleScroll.getHmin());
                break;
            case END:
                scheduleScroll.setHvalue(scheduleScroll.getHmax());
                break;
            default:
                return;
        }
        logger.fine("Scrolled the schedule with " + event.getCode());
        event.consume();
    }

    /** Scrolls the timetable by {@code dx} and {@code dy} pixels, stopping at its edges. */
    private void scrollBy(double dx, double dy) {
        Bounds bounds = scheduleScroll.getViewportBounds();
        double scrollableWidth = grid.prefWidth(-1) - bounds.getWidth();
        double scrollableHeight = grid.prefHeight(-1) - bounds.getHeight();
        if (scrollableWidth > 0) {
            scheduleScroll.setHvalue(clamp(scheduleScroll.getHvalue() + dx / scrollableWidth));
        }
        if (scrollableHeight > 0) {
            scheduleScroll.setVvalue(clamp(scheduleScroll.getVvalue() + dy / scrollableHeight));
        }
        assert scheduleScroll.getHvalue() >= 0 && scheduleScroll.getHvalue() <= 1;
        assert scheduleScroll.getVvalue() >= 0 && scheduleScroll.getVvalue() <= 1;
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(1, value));
    }

    private String colourOf(Person student) {
        int index = persons.indexOf(student);
        return PALETTE[Math.floorMod(index, PALETTE.length)];
    }

    private String titleOf(Person student) {
        int index = displayedPersons.indexOf(student);
        String name = student.getName().fullName;
        return index < 0 ? name : (index + 1) + ". " + name;
    }

    private void refresh() {
        Function<Person, String> colour = this::colourOf;
        grid.setSessions(List.copyOf(sessions), colour, this::titleOf);
        Platform.runLater(this::syncPinnedLabels);

        legendPane.getChildren().clear();
        persons.stream()
                .filter(p -> sessions.stream().anyMatch(s -> s.getStudent().equals(p)))
                .forEach(p -> legendPane.getChildren().add(createLegendEntry(p)));
    }

    private HBox createLegendEntry(Person student) {
        Region swatch = new Region();
        swatch.setMinSize(14, 14);
        swatch.setPrefSize(14, 14);
        swatch.setStyle("-fx-background-color: " + colourOf(student) + ";");

        Label name = new Label(student.getName().fullName);
        name.setStyle("-fx-text-fill: white;");

        HBox entry = new HBox(6, swatch, name);
        entry.setAlignment(Pos.CENTER_LEFT);
        return entry;
    }
}
