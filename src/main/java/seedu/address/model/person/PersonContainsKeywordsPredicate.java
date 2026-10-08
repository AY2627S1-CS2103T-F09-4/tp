package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} matches any of the keywords given.
 */
public class PersonContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;

    public PersonContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> matchesPerson(person, keyword));
    }

    private boolean matchesPerson(Person person, String keyword) {
        return containsIgnoreCase(person.getName().fullName, keyword)
                || containsIgnoreCase(person.getPhone().value, keyword)
                || containsIgnoreCase(person.getEmail().value, keyword)
                || containsIgnoreCase(person.getAddress().value, keyword)
                || person.getTags().stream()
                .anyMatch(tag -> containsIgnoreCase(tag.tagName, keyword));
    }

    private boolean containsIgnoreCase(String field, String keyword) {
        return field.toLowerCase().contains(keyword.toLowerCase());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonContainsKeywordsPredicate otherPersonContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherPersonContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}


