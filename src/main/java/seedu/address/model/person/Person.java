package seedu.address.model.person;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;
import seedu.address.model.task.TaskList;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Subject subject;
    private final GradeLevel gradeLevel;
    private final PhotoPath photoPath;
    private final TaskList taskList;
    private final Set<Tag> tags = new HashSet<>();

    /**
     * Creates a person without a grade level.
     * Every field must be present and not null.
     */
    public Person(Name name, Phone phone, Email email, Address address, Subject subject, Set<Tag> tags) {
        this(name, phone, email, address, subject, GradeLevel.NONE, PhotoPath.NONE, new TaskList(), tags);
    }

    /**
     * Every field must be present and not null. Use {@link GradeLevel#NONE} if there is no grade level.
     */
    public Person(Name name, Phone phone, Email email, Address address, Subject subject, GradeLevel gradeLevel,
            Set<Tag> tags) {
        this(name, phone, email, address, subject, gradeLevel, PhotoPath.NONE, new TaskList(), tags);
    }

    /**
     * Every field must be present and not null. Use {@link GradeLevel#NONE} if there is no grade level.
     * Use {@link PhotoPath#NONE} if there is no photo path.
     */
    public Person(Name name, Phone phone, Email email, Address address, Subject subject, GradeLevel gradeLevel,
            PhotoPath photoPath, Set<Tag> tags) {
        this(name, phone, email, address, subject, gradeLevel, photoPath, new TaskList(), tags);
    }

    /**
     * Every field must be present and not null. Use {@link GradeLevel#NONE} and {@link PhotoPath#NONE}
     * when those values are not specified.
     */
    public Person(Name name, Phone phone, Email email, Address address, Subject subject, GradeLevel gradeLevel,
            PhotoPath photoPath, TaskList taskList, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, subject, gradeLevel, photoPath, taskList, tags);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.subject = subject;
        this.gradeLevel = gradeLevel;
        this.photoPath = photoPath;
        this.taskList = taskList;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public Subject getSubject() {
        return subject;
    }

    public GradeLevel getGradeLevel() {
        return gradeLevel;
    }

    public PhotoPath getPhotoPath() {
        return photoPath;
    }

    public TaskList getTaskList() {
        return taskList;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if both persons have the same name (case-insensitive) and phone number.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().fullName.equalsIgnoreCase(getName().fullName)
                && otherPerson.getPhone().equals(getPhone());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && subject.equals(otherPerson.subject)
                && gradeLevel.equals(otherPerson.gradeLevel)
                && photoPath.equals(otherPerson.photoPath)
                && taskList.equals(otherPerson.taskList)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, subject, gradeLevel, photoPath, taskList, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("subject", subject)
                .add("gradeLevel", gradeLevel)
                .add("photoPath", photoPath)
                .add("taskList", taskList)
                .add("tags", tags)
                .toString();
    }

}
