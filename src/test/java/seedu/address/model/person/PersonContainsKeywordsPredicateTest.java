package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonContainsKeywordsPredicateTest {

    @Test
    public void equals() {
        List<String> firstPredicateKeywordList = List.of("first");
        List<String> secondPredicateKeywordList = List.of("first", "second");

        PersonContainsKeywordsPredicate firstPredicate =
                new PersonContainsKeywordsPredicate(firstPredicateKeywordList);
        PersonContainsKeywordsPredicate secondPredicate =
                new PersonContainsKeywordsPredicate(secondPredicateKeywordList);

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        PersonContainsKeywordsPredicate firstPredicateCopy =
                new PersonContainsKeywordsPredicate(firstPredicateKeywordList);
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different keywords -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameContainsKeywords_returnsTrue() {
        // One keyword
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("Alice"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Multiple keywords: one matches
        predicate = new PersonContainsKeywordsPredicate(List.of("Bob", "Carol"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Carol").build()));

        // Mixed-case keyword
        predicate = new PersonContainsKeywordsPredicate(List.of("aLIce"));
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Bob").build()));
    }

    @Test
    public void test_phoneContainsKeywords_returnsTrue() {
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("12345"));

        assertTrue(predicate.test(new PersonBuilder()
                .withName("Alice")
                .withPhone("12345")
                .build()));
    }

    @Test
    public void test_emailContainsKeywords_returnsTrue() {
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("alice@email.com"));

        assertTrue(predicate.test(new PersonBuilder()
                .withName("Alice")
                .withEmail("alice@email.com")
                .build()));
    }

    @Test
    public void test_addressContainsKeywords_returnsTrue() {
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("Main Street"));

        assertTrue(predicate.test(new PersonBuilder()
                .withName("Alice")
                .withAddress("Main Street")
                .build()));
    }

    @Test
    public void test_gradeLevelContainsKeywords_returnsTrue() {
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("Secondary"));

        assertTrue(predicate.test(new PersonBuilder()
                .withName("Alice")
                .withGradeLevel("Secondary")
                .build()));
    }

    @Test
    public void test_subjectContainsKeywords_returnsTrue() {
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("Mathematics"));

        assertTrue(predicate.test(new PersonBuilder()
                .withName("Alice")
                .withSubject("Mathematics")
                .build()));
    }

    @Test
    public void test_taskContainsKeywords_returnsTrue() {
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("homework"));

        assertTrue(predicate.test(new PersonBuilder()
                .withName("Alice")
                .withTasks("Complete homework")
                .build()));
    }

    @Test
    public void test_tagContainsKeywords_returnsTrue() {
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("friends"));

        assertTrue(predicate.test(new PersonBuilder()
                .withName("Alice")
                .withTags("friends")
                .build()));
    }

    @Test
    public void test_containsKeywords_caseInsensitive_returnsTrue() {
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of("MATHEMATICS"));

        assertTrue(predicate.test(new PersonBuilder()
                .withName("Alice")
                .withSubject("Mathematics")
                .build()));
    }

    @Test
    public void test_doesNotContainKeywords_returnsFalse() {
        // Zero keywords
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(List.of());
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Non-matching keyword
        predicate = new PersonContainsKeywordsPredicate(List.of("Carol"));
        assertFalse(predicate.test(new PersonBuilder()
                .withName("Alice Bob")
                .withPhone("12345")
                .withEmail("alice@email.com")
                .withAddress("Main Street")
                .withGradeLevel("Primary")
                .withSubject("English")
                .build()));
    }

    @Test
    public void toStringMethod() {
        List<String> keywords = List.of("keyword1", "keyword2");
        PersonContainsKeywordsPredicate predicate =
                new PersonContainsKeywordsPredicate(keywords);

        String expected = PersonContainsKeywordsPredicate.class.getCanonicalName()
                + "{keywords=" + keywords + "}";
        assertEquals(expected, predicate.toString());
    }
}
