package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for {@code TextIndexFinder}.
 */
public class TextIndexFinderTest {

    private final TextIndexFinder finder = new TextIndexFinder();

    @Test
    public void findMatchRanges_keywordPresent_returnsCorrectRange() {
        List<MatchRange> result = finder.findMatchRanges("Hello World", "World");

        assertEquals(1, result.size());
        assertEquals(6, result.get(0).getStart());
        assertEquals(11, result.get(0).getEnd());
    }

    @Test
    public void findMatchRanges_caseInsensitiveMatch_returnsCorrectRange() {
        List<MatchRange> result = finder.findMatchRanges("Hello WORLD", "world");

        assertEquals(1, result.size());
        assertEquals(6, result.get(0).getStart());
        assertEquals(11, result.get(0).getEnd());
    }

    @Test
    public void findMatchRanges_multipleOccurrences_returnsAllRanges() {
        List<MatchRange> result = finder.findMatchRanges("cat and cat", "cat");

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getStart());
        assertEquals(3, result.get(0).getEnd());

        assertEquals(8, result.get(1).getStart());
        assertEquals(11, result.get(1).getEnd());
    }

    @Test
    public void findMatchRanges_keywordAbsent_returnsEmptyList() {
        List<MatchRange> result = finder.findMatchRanges("Hello World", "Java");

        assertTrue(result.isEmpty());
    }

    @Test
    public void findMatchRanges_nullText_returnsEmptyList() {
        assertTrue(finder.findMatchRanges(null, "Hello").isEmpty());
    }

    @Test
    public void findMatchRanges_nullKeyword_returnsEmptyList() {
        assertTrue(finder.findMatchRanges("Hello", null).isEmpty());
    }

    @Test
    public void findMatchRanges_emptyKeyword_returnsEmptyList() {
        assertTrue(finder.findMatchRanges("Hello", "").isEmpty());
    }

    @Test
    public void findMatchRanges_overlappingOccurrences_returnsNonOverlappingRanges() {
        List<MatchRange> result = finder.findMatchRanges("aaaa", "aa");

        assertEquals(2, result.size());

        assertEquals(0, result.get(0).getStart());
        assertEquals(2, result.get(0).getEnd());

        assertEquals(2, result.get(1).getStart());
        assertEquals(4, result.get(1).getEnd());
    }
}
