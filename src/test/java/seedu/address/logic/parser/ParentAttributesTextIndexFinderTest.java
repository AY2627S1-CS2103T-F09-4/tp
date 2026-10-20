package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.person.ParentContact;
import seedu.address.testutil.ParentContactBuilder;

class ParentAttributesTextIndexFinderTest {

    @Test
    void getNameIndex_keywordMatchesName_returnsMatchRange() {
        ParentContact contact = new ParentContactBuilder()
                .withName("Alice Tan")
                .build();

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, "Alice");

        List<MatchRange> ranges = finder.getNameIndex();

        assertEquals(1, ranges.size());
        assertEquals(0, ranges.get(0).getStart());
        assertEquals(5, ranges.get(0).getEnd());
    }

    @Test
    void getPhoneIndex_keywordMatchesPhone_returnsMatchRange() {
        ParentContact contact = new ParentContactBuilder()
                .withPhone("91234567")
                .build();

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, "234");

        List<MatchRange> ranges = finder.getPhoneIndex();

        assertEquals(1, ranges.size());
        assertEquals(2, ranges.get(0).getStart());
        assertEquals(5, ranges.get(0).getEnd());
    }

    @Test
    void getEmailIndex_keywordMatchesEmail_returnsMatchRange() {
        ParentContact contact = new ParentContactBuilder()
                .withEmail("alice@example.com")
                .build();

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, "example");

        List<MatchRange> ranges = finder.getEmailIndex();

        assertEquals(1, ranges.size());
        assertEquals(6, ranges.get(0).getStart());
        assertEquals(13, ranges.get(0).getEnd());
    }

    @Test
    void getLinkedStudentIndex_keywordMatchesLinkedStudent_returnsPaddedRange() {
        ParentContact contact = new ParentContactBuilder()
                .withLinkedStudentId("Alice Tan|91234567")
                .build();

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, "Alice");

        List<MatchRange> ranges = finder.getLinkedStudentIndex(16);

        assertEquals(1, ranges.size());
        assertEquals(0 + 16, ranges.get(0).getStart());
        assertEquals(5 + 16, ranges.get(0).getEnd());
    }

    @Test
    void getRequirementsIndex_keywordMatchesRequirement_returnsPaddedRange() {
        ParentContact contact = new ParentContactBuilder()
                .withRequirements("Vegetarian", "No peanuts")
                .build();

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, "peanuts");

        List<MatchRange> ranges = finder.getRequirementsIndex(14);

        assertEquals(1, ranges.size());
        assertEquals(16 + 13, ranges.get(0).getStart());
        assertEquals(23 + 13, ranges.get(0).getEnd());
    }

    @Test
    void getNameIndex_keywordDoesNotMatch_returnsEmptyList() {
        ParentContact contact = new ParentContactBuilder()
                .withName("Alice Tan")
                .build();

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, "Bob");

        assertTrue(finder.getNameIndex().isEmpty());
    }

    @Test
    void getRequirementsIndex_noRequirements_returnsEmptyList() {
        ParentContact contact = new ParentContactBuilder()
                .withRequirements()
                .build();

        ParentAttributesTextIndexFinder finder =
                new ParentAttributesTextIndexFinder(contact, "food");

        assertTrue(finder.getRequirementsIndex(14).isEmpty());
    }
}
