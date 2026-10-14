package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for {@code MatchRange}.
 */
public class MatchRangeTest {

    @Test
    public void constructor_validIndexes_success() {
        MatchRange matchRange = new MatchRange(2, 7);

        assertEquals(2, matchRange.getStart());
        assertEquals(7, matchRange.getEnd());
    }

    @Test
    public void constructor_zeroIndexes_success() {
        MatchRange matchRange = new MatchRange(0, 0);

        assertEquals(0, matchRange.getStart());
        assertEquals(0, matchRange.getEnd());
    }
}
