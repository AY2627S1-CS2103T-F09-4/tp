package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.LogicManager;
import seedu.address.model.task.Task;

/**
 * Tests that a {@code Person}'s {@code attributes} matches any of the keywords given.
 */
public class PersonContainsKeywordsPredicate implements Predicate<Person> {
    private final List<String> keywords;
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);
    private Person person;

    /**
     * Creates a {@code PersonContainsKeywordsPredicate} with keywords.
     *
     * @param keywords the keywords used for testing.
     */
    public PersonContainsKeywordsPredicate(List<String> keywords) {
        requireNonNull(keywords);
        this.keywords = keywords;
    }

    @Override
    public boolean test(Person person) {
        return keywords.stream()
                .anyMatch(keyword -> matchesPerson(person, keyword));
    }

    private boolean matchesPerson(Person person, String keyword) {
        this.person = person;
        return containsIgnoreCase(person.getName().fullName, keyword)
                || containsIgnoreCase(person.getPhone().value, keyword)
                || containsIgnoreCase(person.getEmail().value, keyword)
                || containsIgnoreCase(person.getAddress().value, keyword)
                || containsIgnoreCase(person.getGradeLevel().value, keyword)
                || containsIgnoreCase(person.getSubject().value, keyword)
                || checkIfTasksContainKeyword(person.getTaskList().getTasks(), keyword)
                || checkIfTagsContainKeyword(person, keyword);
    }

    private boolean checkIfTagsContainKeyword(Person person, String keyword) {
        boolean result = person.getTags().stream()
                .anyMatch(tag -> containsIgnoreCase(tag.tagName, keyword));
        if (result) {
            logger.fine("Person " + person.getName().fullName + " matches " + keyword
                    + " in attribute: Tags");
        }
        return result;
    }

    private boolean checkIfTasksContainKeyword(List<Task> tasks, String keyword) {
        boolean result = false;
        for (Task task : tasks) {
            result = result || containsIgnoreCase(task.toString(), keyword);
        }
        if (result) {
            logger.fine("Person " + person.getName().fullName + " matches " + keyword
                    + " in attribute: Tasks");
        }
        return result;
    }

    private boolean containsIgnoreCase(String field, String keyword) {
        if (field == null) {
            return false;
        }
        boolean result = field.toLowerCase().contains(keyword.toLowerCase());
        if (result) {
            logger.fine("Person " + person.getName().fullName + " matches " + keyword
                    + " in attribute: " + field);
        }
        return result;
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


