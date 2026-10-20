package seedu.address.testutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Phone;
import seedu.address.model.person.PhotoPath;

/**
 * A utility class to help with building ParentContact objects.
 */
public class ParentContactBuilder {

    public static final String DEFAULT_ID = "parent-test-id";
    public static final String DEFAULT_NAME = "Amy Bee";
    public static final String DEFAULT_PHONE = "85355255";
    public static final String DEFAULT_EMAIL = "amy@gmail.com";

    private String id;
    private Name name;
    private Phone phone;
    private Email email;
    private String linkedStudentId;
    private List<String> requirements;
    private PhotoPath photoPath;

    /**
     * Creates a ParentContactBuilder with default details.
     */
    public ParentContactBuilder() {
        id = DEFAULT_ID;
        name = new Name(DEFAULT_NAME);
        phone = new Phone(DEFAULT_PHONE);
        email = new Email(DEFAULT_EMAIL);
        linkedStudentId = null;
        requirements = new ArrayList<>();
        photoPath = PhotoPath.NONE;
    }

    /**
     * Initializes the builder with the data of the given ParentContact.
     */
    public ParentContactBuilder(ParentContact contactToCopy) {
        id = contactToCopy.getId();
        name = contactToCopy.getName();
        phone = contactToCopy.getPhone();
        email = contactToCopy.getEmail();
        linkedStudentId = contactToCopy.getLinkedStudentId();
        requirements = new ArrayList<>(contactToCopy.getRequirements());
        photoPath = contactToCopy.getPhotoPath();
    }

    /**
     * Sets the ID of the parent contact.
     */
    public ParentContactBuilder withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * Sets the name of the parent contact.
     */
    public ParentContactBuilder withName(String name) {
        this.name = new Name(name);
        return this;
    }

    /**
     * Sets the phone number of the parent contact.
     */
    public ParentContactBuilder withPhone(String phone) {
        this.phone = new Phone(phone);
        return this;
    }

    /**
     * Sets the email of the parent contact.
     */
    public ParentContactBuilder withEmail(String email) {
        this.email = email == null ? null : new Email(email);
        return this;
    }

    /**
     * Sets the linked student's identifier.
     */
    public ParentContactBuilder withLinkedStudentId(String linkedStudentId) {
        this.linkedStudentId = linkedStudentId;
        return this;
    }

    /**
     * Sets the requirements of the parent contact.
     */
    public ParentContactBuilder withRequirements(String... requirements) {
        this.requirements = new ArrayList<>(Arrays.asList(requirements));
        return this;
    }

    /**
     * Sets the photo path of the parent contact.
     */
    public ParentContactBuilder withPhotoPath(String photoPath) {
        this.photoPath = new PhotoPath(photoPath);
        return this;
    }

    /**
     * Builds and returns the ParentContact.
     */
    public ParentContact build() {
        return new ParentContact(
                id,
                name,
                phone,
                email,
                linkedStudentId,
                requirements,
                photoPath);
    }
}
