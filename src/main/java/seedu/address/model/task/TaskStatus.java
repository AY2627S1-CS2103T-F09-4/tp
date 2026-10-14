package seedu.address.model.task;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;
import java.util.Optional;

/**
 * Represents whether a task has been completed.
 */
public enum TaskStatus {
    NOT_DONE("not done"),
    DONE("done");

    public static final String MESSAGE_CONSTRAINTS = "Task status must be 'done' or 'not done'.";

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns true if the given string names a status, ignoring case and surrounding whitespace.
     */
    public static boolean isValidStatus(String test) {
        requireNonNull(test);
        return find(test).isPresent();
    }

    /**
     * Returns the status named by the given string, ignoring case and surrounding whitespace.
     */
    public static TaskStatus fromString(String status) {
        requireNonNull(status);
        checkArgument(isValidStatus(status), MESSAGE_CONSTRAINTS);
        return find(status).get();
    }

    private static Optional<TaskStatus> find(String status) {
        String trimmed = status.trim();
        return Arrays.stream(values())
                .filter(value -> value.displayName.equalsIgnoreCase(trimmed))
                .findFirst();
    }

    @Override
    public String toString() {
        return displayName;
    }
}
