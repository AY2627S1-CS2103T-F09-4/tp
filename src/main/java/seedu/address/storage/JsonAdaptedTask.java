package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.task.Task;

/**
 * Jackson-friendly version of {@link Task}.
 */
class JsonAdaptedTask {

    private final String description;

    /**
     * Constructs a {@code JsonAdaptedTask} with the given description.
     */
    @JsonCreator
    public JsonAdaptedTask(String description) {
        this.description = description;
    }

    /**
     * Converts a given {@code Task} into this class for Jackson use.
     */
    public JsonAdaptedTask(Task source) {
        description = source.description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }

    /**
     * Converts this adapted task into a model {@code Task}.
     *
     * @throws IllegalValueException if the description is invalid.
     */
    public Task toModelType() throws IllegalValueException {
        if (description == null || !Task.isValidDescription(description)) {
            throw new IllegalValueException(Task.MESSAGE_CONSTRAINTS);
        }
        return new Task(description);
    }
}
