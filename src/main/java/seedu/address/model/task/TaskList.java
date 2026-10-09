package seedu.address.model.task;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * An immutable, ordered list of tasks.
 */
public class TaskList {

    private final List<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        this(new ArrayList<>());
    }

    /**
     * Creates a task list containing the given tasks in order.
     *
     * @param tasks The tasks to include.
     */
    public TaskList(List<Task> tasks) {
        requireNonNull(tasks);
        this.tasks = new ArrayList<>(tasks);
        this.tasks.forEach(task -> requireNonNull(task));
    }

    /**
     * Returns the tasks in insertion order as an unmodifiable list.
     */
    public List<Task> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    /**
     * Returns a new task list with the given task appended.
     */
    public TaskList withTask(Task task) {
        requireNonNull(task);
        List<Task> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.add(task);
        return new TaskList(updatedTasks);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof TaskList otherTaskList)) {
            return false;
        }
        return tasks.equals(otherTaskList.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tasks);
    }

    @Override
    public String toString() {
        return tasks.toString();
    }
}
