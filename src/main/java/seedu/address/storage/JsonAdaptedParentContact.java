package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Phone;
import seedu.address.model.person.PhotoPath;

class JsonAdaptedParentContact {
    private final String id;
    private final String name;
    private final String phone;
    private final String email;
    private final String linkedStudentId;
    private final List<String> requirements;
    private final String photoPath;

    @JsonCreator
    JsonAdaptedParentContact(@JsonProperty("id") String id, @JsonProperty("name") String name,
            @JsonProperty("phone") String phone, @JsonProperty("email") String email,
            @JsonProperty("linkedStudentId") String linkedStudentId,
            @JsonProperty("requirements") List<String> requirements,
            @JsonProperty("photoPath") String photoPath) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.linkedStudentId = linkedStudentId;
        this.requirements = requirements == null ? new ArrayList<>() : requirements;
        this.photoPath = photoPath;
    }

    JsonAdaptedParentContact(ParentContact source) {
        id = source.getId();
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail() == null ? null : source.getEmail().value;
        linkedStudentId = source.getLinkedStudentId();
        requirements = source.getRequirements();
        photoPath = source.getPhotoPath().value;
    }

    ParentContact toModelType() throws IllegalValueException {
        if (id == null || name == null || phone == null) {
            throw new IllegalValueException("Parent contact is missing a required field.");
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        if (email != null && !email.isEmpty() && !Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        return new ParentContact(
                id,
                new Name(name),
                new Phone(phone),
                email == null || email.isEmpty() ? null : new Email(email),
                linkedStudentId,
                requirements,
                photoPath == null || photoPath.isEmpty() ? PhotoPath.NONE : new PhotoPath(photoPath));
    }
}
