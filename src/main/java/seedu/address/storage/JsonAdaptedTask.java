package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskStatus;

/**
 * Jackson-friendly version of {@link Task}.
 */
class JsonAdaptedTask {

    private final String description;
    private final String status;

    /**
     * Constructs a {@code JsonAdaptedTask} from a legacy plain-string task, which has no status.
     */
    @JsonCreator
    public JsonAdaptedTask(String description) {
        this(description, null);
    }

    /**
     * Constructs a {@code JsonAdaptedTask} with the given description and status.
     */
    @JsonCreator
    public JsonAdaptedTask(@JsonProperty("description") String description,
            @JsonProperty("status") String status) {
        this.description = description;
        this.status = status;
    }

    /**
     * Converts a given {@code Task} into this class for Jackson use.
     */
    public JsonAdaptedTask(Task source) {
        description = source.description;
        status = source.status.toString();
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    /**
     * Converts this adapted task into a model {@code Task}.
     *
     * @throws IllegalValueException if the description or status is invalid.
     */
    public Task toModelType() throws IllegalValueException {
        if (description == null || !Task.isValidDescription(description)) {
            throw new IllegalValueException(Task.MESSAGE_CONSTRAINTS);
        }
        if (status == null || status.isEmpty()) {
            return new Task(description);
        }
        if (!TaskStatus.isValidStatus(status)) {
            throw new IllegalValueException(TaskStatus.MESSAGE_CONSTRAINTS);
        }
        return new Task(description, TaskStatus.fromString(status));
    }
}
