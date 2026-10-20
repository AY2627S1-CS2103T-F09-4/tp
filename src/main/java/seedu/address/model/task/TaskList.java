package seedu.address.model.task;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;

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

    public int size() {
        return tasks.size();
    }

    /**
     * Returns the task at the given index.
     *
     * @throws IndexOutOfBoundsException if the index is beyond the end of the list.
     */
    public Task getTask(Index index) {
        requireNonNull(index);
        return tasks.get(index.getZeroBased());
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

    /**
     * Returns a new task list with the status of the task at {@code index} replaced.
     *
     * @throws IndexOutOfBoundsException if the index is beyond the end of the list.
     */
    public TaskList withTaskStatus(Index index, TaskStatus status) {
        requireNonNull(status);
        List<Task> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.set(index.getZeroBased(), getTask(index).withStatus(status));
        return new TaskList(updatedTasks);
    }

    /**
     * Returns a new task list without the task at {@code index}; later tasks shift up by one.
     *
     * @throws IndexOutOfBoundsException if the index is beyond the end of the list.
     */
    public TaskList withoutTask(Index index) {
        requireNonNull(index);
        List<Task> updatedTasks = new ArrayList<>(tasks);
        updatedTasks.remove(index.getZeroBased());
        return new TaskList(updatedTasks);
    }

    /** Returns a copy of this TaskList instance */
    public TaskList copy() {
        List<Task> taskCopy = new ArrayList<>();
        for (Task task : tasks) {
            taskCopy.add(task.copy());
        }

        return new TaskList(taskCopy);
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
