package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Represents a parent contact associated with at most one student. */
public class ParentContact {
    private final String id;
    private final Name name;
    private final Phone phone;
    private final Email email;
    private final String linkedStudentId;
    private final List<String> requirements;
    private final PhotoPath photoPath;

    public ParentContact(Name name, Phone phone, Email email, String linkedStudentId,
            List<String> requirements) {
        this(UUID.randomUUID().toString(), name, phone, email, linkedStudentId, requirements);
    }

    public ParentContact(String id, Name name, Phone phone, Email email, String linkedStudentId,
            List<String> requirements) {
        this.id = requireNonNull(id);
        this.name = requireNonNull(name);
        this.phone = requireNonNull(phone);
        this.email = email;
        this.linkedStudentId = linkedStudentId;
        this.requirements = List.copyOf(requireNonNull(requirements));
        this.photoPath = PhotoPath.NONE;
    }

    public ParentContact(String id, Name name, Phone phone, Email email, String linkedStudentId,
            List<String> requirements, PhotoPath photoPath) {
        this.id = requireNonNull(id); this.name = requireNonNull(name); this.phone = requireNonNull(phone);
        this.email = email; this.linkedStudentId = linkedStudentId;
        this.requirements = List.copyOf(requireNonNull(requirements));
        this.photoPath = requireNonNull(photoPath);
    }

    public String getId() { return id; }
    public Name getName() { return name; }
    public Phone getPhone() { return phone; }
    public Email getEmail() { return email; }
    public String getLinkedStudentId() { return linkedStudentId; }
    public List<String> getRequirements() { return Collections.unmodifiableList(requirements); }
    public PhotoPath getPhotoPath() { return photoPath; }

    public boolean isSameContact(ParentContact other) {
        return other != null && name.fullName.equalsIgnoreCase(other.name.fullName)
                && phone.equals(other.phone);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof ParentContact contact)) return false;
        return id.equals(contact.id) && name.equals(contact.name) && phone.equals(contact.phone)
                && Objects.equals(email, contact.email)
                && Objects.equals(linkedStudentId, contact.linkedStudentId)
                && requirements.equals(contact.requirements) && photoPath.equals(contact.photoPath);
    }

    @Override
    public int hashCode() { return Objects.hash(id, name, phone, email, linkedStudentId, requirements, photoPath); }
}
