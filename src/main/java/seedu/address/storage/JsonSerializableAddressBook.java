package seedu.address.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.AddressBook;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.person.ParentContact;
import seedu.address.model.person.Person;
import seedu.address.model.reminder.Reminder;
import seedu.address.model.schedule.ScheduledSession;

/**
 * An Immutable AddressBook that is serializable to JSON format.
 */
@JsonRootName(value = "addressbook")
class JsonSerializableAddressBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();
    private final List<JsonAdaptedParentContact> parentContacts = new ArrayList<>();
    private final List<JsonAdaptedScheduledSession> schedule = new ArrayList<>();
    private final List<JsonAdaptedReminder> reminders = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableAddressBook} with the given persons.
     */
    @JsonCreator
    public JsonSerializableAddressBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons,
            @JsonProperty("parentContacts") List<JsonAdaptedParentContact> parentContacts,
            @JsonProperty("schedule") List<JsonAdaptedScheduledSession> schedule,
            @JsonProperty("reminders") List<JsonAdaptedReminder> reminders) {
        if (persons != null) {
            this.persons.addAll(persons);
        }
        if (parentContacts != null) {
            this.parentContacts.addAll(parentContacts);
        }
        if (schedule != null) {
            this.schedule.addAll(schedule);
        }
        if (reminders != null) {
            this.reminders.addAll(reminders);
        }
    }

    /** Compatibility constructor for existing callers that predate reminders. */
    public JsonSerializableAddressBook(List<JsonAdaptedPerson> persons,
            List<JsonAdaptedParentContact> parentContacts, List<JsonAdaptedScheduledSession> schedule) {
        this(persons, parentContacts, schedule, null);
    }

    /**
     * Converts a given {@code ReadOnlyAddressBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableAddressBook}.
     */
    public JsonSerializableAddressBook(ReadOnlyAddressBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
        parentContacts.addAll(source.getParentContactList().stream()
                .map(JsonAdaptedParentContact::new).collect(Collectors.toList()));
        for (ScheduledSession session : source.getScheduleList()) {
            schedule.add(new JsonAdaptedScheduledSession(session, source.getPersonList()));
        }
        reminders.addAll(source.getReminderList().stream().map(JsonAdaptedReminder::new).toList());
    }

    /**
     * Converts this address book into the model's {@code AddressBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public AddressBook toModelType() throws IllegalValueException {
        AddressBook addressBook = new AddressBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (addressBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            addressBook.addPerson(person);
        }
        for (JsonAdaptedParentContact adapted : parentContacts) {
            ParentContact contact = adapted.toModelType();
            if (addressBook.hasParentContact(contact)) {
                throw new IllegalValueException("Parent contacts list contains duplicate contact(s).");
            }
            addressBook.addParentContact(contact);
        }
        for (JsonAdaptedScheduledSession adapted : schedule) {
            addressBook.addScheduledSession(adapted.toModelType(addressBook.getPersonList()));
        }
        for (JsonAdaptedReminder adapted : reminders) {
            Reminder reminder = adapted.toModelType();
            addressBook.addReminder(reminder);
        }
        return addressBook;
    }

}
