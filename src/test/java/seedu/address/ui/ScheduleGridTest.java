package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.DayOfWeek;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.testutil.ScheduledSessionBuilder;

public class ScheduleGridTest {

    private static final double AXIS_WIDTH = 60;
    private static final double HEADER_HEIGHT = 30;

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
    }

    @Test
    public void layout_cardIsPositionedBySlotAndSizedByDuration() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00")));
        Region card = cards(grid).get(0);

        assertEquals(AXIS_WIDTH, card.getLayoutX(), 0.001);
        assertEquals(HEADER_HEIGHT, card.getLayoutY(), 0.001);
        assertEquals(hourWidth(grid), card.getWidth(), 0.001);
        assertTrue(card.getHeight() > 0);
    }

    @Test
    public void layout_cardWidthScalesWithDuration() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00"),
                session(ALICE, DayOfWeek.TUESDAY, "10-00", "12-00")));
        List<Region> cards = cards(grid);

        assertEquals(2, cards.get(1).getWidth() / cards.get(0).getWidth(), 0.001);
    }

    @Test
    public void layout_timesAreRoundedOutwardsToThirtyMinutes() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00"),
                session(ALICE, DayOfWeek.TUESDAY, "10-10", "10-40"),
                session(ALICE, DayOfWeek.WEDNESDAY, "10-10", "10-20")));
        List<Region> cards = cards(grid);
        double hourWidth = hourWidth(grid);

        // 10:10-10:40 is covered by 10:00-11:00
        assertEquals(AXIS_WIDTH, cards.get(1).getLayoutX(), 0.001);
        assertEquals(hourWidth, cards.get(1).getWidth(), 0.001);
        // 10:10-10:20 is covered by 10:00-10:30
        assertEquals(AXIS_WIDTH, cards.get(2).getLayoutX(), 0.001);
        assertEquals(hourWidth / 2, cards.get(2).getWidth(), 0.001);
    }

    @Test
    public void layout_overlappingSessionsStackAndTheDayRowGrows() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "12-00"),
                session(BENSON, DayOfWeek.MONDAY, "11-00", "13-00"),
                session(ALICE, DayOfWeek.TUESDAY, "10-00", "11-00")));
        List<Region> cards = cards(grid);
        double rowHeight = cards.get(0).getHeight();

        assertEquals(rowHeight, cards.get(1).getHeight(), 0.001);
        assertEquals(cards.get(0).getLayoutY() + rowHeight, cards.get(1).getLayoutY(), 0.001);
        // Tuesday starts below both Monday lanes
        assertEquals(HEADER_HEIGHT + 2 * rowHeight, cards.get(2).getLayoutY(), 0.001);
    }

    @Test
    public void layout_touchingSessionsShareTheFirstLane() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00"),
                session(BENSON, DayOfWeek.MONDAY, "11-00", "12-00")));
        List<Region> cards = cards(grid);

        assertEquals(cards.get(0).getLayoutY(), cards.get(1).getLayoutY(), 0.001);
    }

    @Test
    public void layout_sessionBeforeDefaultRange_extendsTimelineEarlier() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(
                session(ALICE, DayOfWeek.MONDAY, "08-00", "09-00"),
                session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00")));
        List<Region> cards = cards(grid);
        double hourWidth = cards.get(0).getWidth();

        assertEquals(AXIS_WIDTH, cards.get(0).getLayoutX(), 0.001);
        assertEquals(AXIS_WIDTH + 2 * hourWidth, cards.get(1).getLayoutX(), 0.001);
    }

    @Test
    public void prefSize_defaultRangeHasEightHoursAndSevenDays() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00")));
        Region card = cards(grid).get(0);

        double hourWidth = hourWidth(grid);
        assertEquals(AXIS_WIDTH + 8 * hourWidth, grid.prefWidth(-1), 0.001);
        assertEquals(HEADER_HEIGHT + 7 * card.getHeight(), grid.prefHeight(-1), 0.001);
    }

    @Test
    public void prefSize_sessionAfterDefaultRange_widensTimeline() throws Exception {
        ScheduleGrid defaultGrid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "10-00", "11-00")));
        ScheduleGrid widerGrid = layoutGrid(List.of(session(ALICE, DayOfWeek.MONDAY, "19-00", "20-30")));

        assertTrue(widerGrid.prefWidth(-1) > defaultGrid.prefWidth(-1));
    }

    @Test
    public void layout_labelsFollowScrollOffsets() throws Exception {
        ScheduleGrid grid = layoutGrid(List.of());

        FxTestSupport.runOnFxThread(() -> {
            grid.setScrollOffsets(120, 45);
            grid.layout();
        });

        assertEquals(120, label(grid, "MON").getLayoutX(), 0.001);
        assertEquals(45, label(grid, "1000").getLayoutY(), 0.001);
        assertEquals(7, labels(grid).stream().filter(l -> l.getText().length() == 3).count());
    }

    private static ScheduledSession session(Person student, DayOfWeek day, String from, String to) {
        return new ScheduledSessionBuilder().withStudent(student).withDay(day).withTimeSlot(from, to).build();
    }

    private static ScheduleGrid layoutGrid(List<ScheduledSession> sessions) throws Exception {
        return FxTestSupport.runOnFxThread(() -> {
            ScheduleGrid grid = new ScheduleGrid();
            grid.setViewport(1500, 600);
            grid.setSessions(sessions, student -> "#f6a05a", student -> student.getName().fullName);
            grid.layout();
            return grid;
        });
    }

    private static List<Region> cards(ScheduleGrid grid) {
        return grid.getChildren().stream()
                .filter(node -> node instanceof ScheduleSessionCard)
                .map(node -> (Region) node)
                .toList();
    }

    private static double hourWidth(ScheduleGrid grid) {
        return (grid.prefWidth(-1) - AXIS_WIDTH) / 8;
    }

    private static List<Label> labels(ScheduleGrid grid) {
        return grid.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> (Label) node)
                .toList();
    }

    private static Node label(ScheduleGrid grid, String text) {
        return labels(grid).stream().filter(l -> l.getText().equals(text)).findFirst().orElseThrow();
    }
}
