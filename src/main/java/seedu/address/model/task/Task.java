package seedu.address.model.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/**
 * Represents a task assigned to a person.
 * Guarantees: immutable; description is not blank.
 */
public class Task {

    public static final String MESSAGE_CONSTRAINTS = "Task description should not be blank.";
    public static final String VALIDATION_REGEX = "[^\\s].*";

    public final String description;

    /**
     * Constructs a {@code Task} with the given description.
     *
     * @param description A non-blank task description.
     */
    public Task(String description) {
        requireNonNull(description);
        checkArgument(isValidDescription(description), MESSAGE_CONSTRAINTS);
        this.description = description;
    }

    /**
     * Returns true if the given description is non-blank.
     */
    public static boolean isValidDescription(String description) {
        return description.matches(VALIDATION_REGEX);
    }

    @Override
    public String toString() {
        return description;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Task otherTask)) {
            return false;
        }
        return description.equals(otherTask.description);
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}
