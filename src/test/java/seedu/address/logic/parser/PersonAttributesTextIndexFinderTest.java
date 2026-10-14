package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalPersons.CARL;

import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Contains unit tests for {@code PersonAttributesTextIndexFinder}.
 */
public class PersonAttributesTextIndexFinderTest {

    @Test
    public void getNameIndex_keywordPresent_returnsCorrectRange() {
        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(CARL, "Carl");

        assertRanges(finder.getNameIndex(), new int[][] {{0, 4}});
    }

    @Test
    public void getPhoneIndex_keywordPresent_returnsCorrectRange() {
        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(CARL, "352");

        assertRanges(finder.getPhoneIndex(), new int[][] {{2, 5}});
    }

    @Test
    public void getEmailIndex_keywordPresent_returnsCorrectRange() {
        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(CARL, "example");

        assertRanges(finder.getEmailIndex(), new int[][] {{6, 13}});
    }

    @Test
    public void getAddressIndex_keywordPresent_returnsCorrectRange() {
        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(CARL, "street");

        assertRanges(finder.getAddressIndex(), new int[][] {{5, 11}});
    }

    @Test
    public void getGradeIndex_keywordAbsent_returnsCorrectRange() {
        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(CARL, "2");

        assertTrue(finder.getGradeLevelIndex().isEmpty());
    }

    /**
     * while every Person by default has a Subject from PersonBuilder, for all intents and purposes
     * this test is functionally the same as if a Person had no class at all.
     */
    @Test
    public void getSubjectIndex_keywordAbsent_returnsEmptyList() {
        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(CARL, "Science");
        assertTrue(finder.getSubjectIndex().isEmpty());
    }

    @Test
    public void getTaskIndex_keywordPresent_returnsCorrectRange() {
        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(CARL, "homework");

        assertRanges(finder.getTaskIndex(0), new int[][] {{0, 8}});
    }

    @Test
    public void getTagIndex_keywordPresent_returnsCorrectRange() {
        PersonAttributesTextIndexFinder finder =
                new PersonAttributesTextIndexFinder(CARL, "street");

        seedu.address.model.tag.Tag tag =
                new seedu.address.model.tag.Tag("street");

        assertRanges(finder.getTagIndex(tag), new int[][] {{0, 6}});
    }

    /**
     * Asserts that the actual ranges match the expected start and end indexes.
     */
    private void assertRanges(List<MatchRange> actual, int[][] expected) {
        assertEquals(expected.length, actual.size());

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i][0], actual.get(i).getStart());
            assertEquals(expected[i][1], actual.get(i).getEnd());
        }
    }
}
