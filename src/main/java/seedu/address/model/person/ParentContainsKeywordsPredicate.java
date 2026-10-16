package seedu.address.model.person;

import java.util.List;
import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code attributes} matches any of the keywords given.
 */
public class ParentContainsKeywordsPredicate implements Predicate<ParentContact> {
    private final List<String> keywords;

    public ParentContainsKeywordsPredicate(List<String> keywords) {
        this.keywords = keywords;
    }

    @Override
    public boolean test(ParentContact parentContact) {
        return keywords.stream()
                .anyMatch(keyword -> matchesParent(parentContact, keyword));
    }

    private boolean matchesParent(ParentContact parentContact, String keyword) {
        return containsIgnoreCase(parentContact.getName().fullName, keyword)
                || containsIgnoreCase(parentContact.getPhone().value, keyword)
                || containsIgnoreCase(parentContact.getEmail().value, keyword)
                || containsIgnoreCase(parentContact.getLinkedStudentId(), keyword)
                || checkIfRequirementsContainKeyword(parentContact.getRequirements(), keyword);
    }

    private boolean checkIfRequirementsContainKeyword(List<String> requirements, String keyword) {
        boolean result = false;
        for (String requirement : requirements) {
            result = result || containsIgnoreCase(requirement, keyword);
        }
        return result;
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
        if (!(other instanceof ParentContainsKeywordsPredicate otherParentContainsKeywordsPredicate)) {
            return false;
        }

        return keywords.equals(otherParentContainsKeywordsPredicate.keywords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("keywords", keywords).toString();
    }
}


