package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import seedu.address.model.schedule.ScheduledSession;
import seedu.address.testutil.ScheduledSessionBuilder;

public class ScheduleSessionCardTest {

    @BeforeAll
    static void initialiseJavaFx() throws Exception {
        FxTestSupport.initialiseJavaFx();
    }

    @Test
    public void constructor_showsTitleDetailsAndTimeInOrder() throws Exception {
        ScheduledSession session = new ScheduledSessionBuilder().withStudent(ALICE)
                .withTimeSlot("10-00", "12-30").build();

        ScheduleSessionCard card = FxTestSupport.runOnFxThread(() ->
                new ScheduleSessionCard(session, "#f6a05a", "1. Alice Pauline"));

        assertEquals("1. Alice Pauline", labelAt(card, 0).getText());
        assertEquals(ALICE.getSubject().value + " | " + ALICE.getGradeLevel().value, labelAt(card, 1).getText());
        assertEquals(ALICE.getAddress().value, labelAt(card, 2).getText());
        assertEquals("10:00 - 12:30", labelAt(card, 4).getText());
        assertTrue(card.getStyle().contains("#f6a05a"));
    }

    private static Label labelAt(ScheduleSessionCard card, int index) {
        return (Label) card.getChildren().get(index);
    }
}
