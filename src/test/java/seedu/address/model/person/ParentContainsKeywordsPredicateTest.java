package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.ParentContactBuilder;

public class ParentContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        ParentContainsKeywordsPredicate firstPredicate =
                new ParentContainsKeywordsPredicate(firstPredicateKeywordList);
        ParentContainsKeywordsPredicate secondPredicate =
                new ParentContainsKeywordsPredicate(secondPredicateKeywordList);

        assertTrue(firstPredicate.equals(firstPredicate));

        ParentContainsKeywordsPredicate firstPredicateCopy =
                new ParentContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        assertFalse(firstPredicate.equals(1));

        assertFalse(firstPredicate.equals(null));

        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        ParentContainsKeywordsPredicate predicate =
                new ParentContainsKeywordsPredicate(List.of("Alice"));

        assertTrue(predicate.test(new ParentContactBuilder()
                .withName("Alice Tan")
                .build()));

        predicate = new ParentContainsKeywordsPredicate(List.of("Bob", "Alice"));

        assertTrue(predicate.test(new ParentContactBuilder()
                .withName("Alice Tan")
                .build()));

        predicate = new ParentContainsKeywordsPredicate(List.of("aLIce"));

        assertTrue(predicate.test(new ParentContactBuilder()
                .withName("Alice Tan")
                .build()));
    }

    @Test
    public void test_phoneContainsKeywords_returnsTrue() {
        ParentContainsKeywordsPredicate predicate =
                new ParentContainsKeywordsPredicate(List.of("12345"));

        assertTrue(predicate.test(new ParentContactBuilder()
                .withPhone("12345")
                .build()));
    }

    @Test
    public void test_emailContainsKeywords_returnsTrue() {
        ParentContainsKeywordsPredicate predicate =
                new ParentContainsKeywordsPredicate(List.of("alice@email.com"));

        assertTrue(predicate.test(new ParentContactBuilder()
                .withEmail("alice@email.com")
                .build()));
    }

    @Test
    public void test_linkedStudentContainsKeywords_returnsTrue() {
        ParentContainsKeywordsPredicate predicate =
                new ParentContainsKeywordsPredicate(List.of("Alice Tan"));

        assertTrue(predicate.test(new ParentContactBuilder()
                .withLinkedStudentName("Alice Tan")
                .build()));
    }

    @Test
    public void test_requirementsContainKeywords_returnsTrue() {
        ParentContainsKeywordsPredicate predicate =
                new ParentContainsKeywordsPredicate(List.of("vegetarian"));

        assertTrue(predicate.test(new ParentContactBuilder()
                .withRequirements("Vegetarian", "No peanuts")
                .build()));
    }

    @Test
    public void test_containsCaseInsensitiveKeywords_returnsTrue() {
        ParentContainsKeywordsPredicate predicate =
                new ParentContainsKeywordsPredicate(List.of("VEGETARIAN"));

        assertTrue(predicate.test(new ParentContactBuilder()
                .withRequirements("Vegetarian")
                .build()));
    }

    @Test
    public void test_doesNotContainKeywords_returnsFalse() {
        // Zero keywords
        ParentContainsKeywordsPredicate predicate =
                new ParentContainsKeywordsPredicate(List.of());

        assertFalse(predicate.test(new ParentContactBuilder()
                .withName("Alice Tan")
                .build()));

        predicate = new ParentContainsKeywordsPredicate(List.of("Carol"));

        assertFalse(predicate.test(new ParentContactBuilder()
                .withName("Alice Tan")
                .withPhone("91234567")
                .withEmail("alice@email.com")
                .withLinkedStudentName("Alice Tan")
                .withRequirements("Vegetarian", "No peanuts")
                .build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        ParentContainsKeywordsPredicate predicate =
                new ParentContainsKeywordsPredicate(keywords);

        String expected = ParentContainsKeywordsPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + "}";

        assertEquals(expected, predicate.toString());
    }
}
