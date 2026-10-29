package seedu.address.model.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Objects;

/**
 * Represents a task assigned to a person.
 * Guarantees: immutable; description is not blank; status is present.
 */
public class Task {

    public static final String MESSAGE_CONSTRAINTS = "Task description should not be blank.";
    public static final String VALIDATION_REGEX = "[^\\s].*";

    /**
     * The longest description a new task may have, so that a task takes at most two lines in a student card.
     * Only new input is checked, so tasks saved before this limit existed still load.
     */
    public static final int MAX_DESCRIPTION_LENGTH = 30;
    public static final String MESSAGE_LENGTH_CONSTRAINTS = "Task description should be at most "
            + MAX_DESCRIPTION_LENGTH + " characters long.";

    public final String description;
    public final TaskStatus status;

    /**
     * Constructs a not-done {@code Task} with the given description.
     *
     * @param description A non-blank task description.
     */
    public Task(String description) {
        this(description, TaskStatus.NOT_DONE);
    }

    /**
     * Constructs a {@code Task} with the given description and status.
     *
     * @param description A non-blank task description.
     */
    public Task(String description, TaskStatus status) {
        requireNonNull(description);
        requireNonNull(status);
        checkArgument(isValidDescription(description), MESSAGE_CONSTRAINTS);
        this.description = description;
        this.status = status;
    }

    /**
     * Returns true if the given description is non-blank.
     */
    public static boolean isValidDescription(String description) {
        return description.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if the given description is short enough to be added as a new task.
     */
    public static boolean isWithinLengthLimit(String description) {
        requireNonNull(description);
        return description.length() <= MAX_DESCRIPTION_LENGTH;
    }

    /**
     * Returns a copy of this task with the given status.
     */
    public Task withStatus(TaskStatus newStatus) {
        return new Task(description, newStatus);
    }

    /** Returns a copy of this Task instance */
    public Task copy() {
        return new Task(description, status);
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
        return description.equals(otherTask.description) && status == otherTask.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, status);
    }
}
