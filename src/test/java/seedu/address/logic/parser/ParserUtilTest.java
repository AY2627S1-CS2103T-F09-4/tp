package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.schedule.TimeSlot;
import seedu.address.model.tag.Tag;
import seedu.address.model.task.Task;
import seedu.address.model.task.TaskStatus;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "12";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndexes_validInput_returnsIndexesInOrder() throws Exception {
        assertEquals(List.of(INDEX_SECOND_PERSON, INDEX_FIRST_PERSON), ParserUtil.parseIndexes(" 2 \t 1 "));
        assertEquals(List.of(INDEX_FIRST_PERSON), ParserUtil.parseIndexes("1"));
    }

    @Test
    public void parseIndexes_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () -> ParserUtil.parseIndexes(""));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () -> ParserUtil.parseIndexes("   "));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () -> ParserUtil.parseIndexes("1 a"));
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, () -> ParserUtil.parseIndexes("1 0"));
    }

    @Test
    public void verifyNoDuplicateIndexes() throws Exception {
        ParserUtil.verifyNoDuplicateIndexes(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_DUPLICATE_INDEX, () ->
                ParserUtil.verifyNoDuplicateIndexes(List.of(INDEX_FIRST_PERSON, INDEX_SECOND_PERSON,
                        INDEX_FIRST_PERSON)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseTask_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTask(null));
    }

    @Test
    public void parseTask_blankValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTask(WHITESPACE));
    }

    @Test
    public void parseTask_validValueWithWhitespace_returnsTrimmedTask() throws Exception {
        assertEquals(new Task("do homework"), ParserUtil.parseTask("  do homework  "));
    }

    @Test
    public void parseTaskStatus_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTaskStatus(null));
    }

    @Test
    public void parseTaskStatus_invalidValue_throwsParseException() {
        String message = TaskStatus.MESSAGE_CONSTRAINTS;

        assertThrows(ParseException.class, message, () -> ParserUtil.parseTaskStatus("finished"));
        assertThrows(ParseException.class, message, () -> ParserUtil.parseTaskStatus(""));
    }

    @Test
    public void parseTaskStatus_validValue_returnsStatus() throws Exception {
        assertEquals(TaskStatus.DONE, ParserUtil.parseTaskStatus("done"));
        assertEquals(TaskStatus.NOT_DONE, ParserUtil.parseTaskStatus("not done"));
        assertEquals(TaskStatus.DONE, ParserUtil.parseTaskStatus(WHITESPACE + "DONE" + WHITESPACE));
    }

    @Test
    public void parseDay_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseDay(null));
    }

    @Test
    public void parseDay_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_DAY, () -> ParserUtil.parseDay(""));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_DAY, () -> ParserUtil.parseDay("someday"));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_DAY, () -> ParserUtil.parseDay("mo"));
        assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_DAY, () -> ParserUtil.parseDay("1"));
    }

    @Test
    public void parseDay_validValue_returnsDay() throws Exception {
        assertEquals(DayOfWeek.MONDAY, ParserUtil.parseDay("monday"));
        assertEquals(DayOfWeek.MONDAY, ParserUtil.parseDay("MON"));
        assertEquals(DayOfWeek.THURSDAY, ParserUtil.parseDay(WHITESPACE + "Thu" + WHITESPACE));
        assertEquals(DayOfWeek.SUNDAY, ParserUtil.parseDay("Sunday"));
    }

    @Test
    public void parseTimeSlot_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTimeSlot(null, "12-00"));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTimeSlot("10-00", null));
    }

    @Test
    public void parseTimeSlot_invalidValue_throwsParseException() {
        String message = TimeSlot.MESSAGE_CONSTRAINTS;

        assertThrows(ParseException.class, message, () -> ParserUtil.parseTimeSlot("", "12-00"));
        assertThrows(ParseException.class, message, () -> ParserUtil.parseTimeSlot("10:00", "12:00"));
        assertThrows(ParseException.class, message, () -> ParserUtil.parseTimeSlot("12-00", "10-00"));
        assertThrows(ParseException.class, message, () -> ParserUtil.parseTimeSlot("10-00", "10-00"));
    }

    @Test
    public void parseTimeSlot_validValue_returnsTimeSlot() throws Exception {
        assertEquals(TimeSlot.of("10-00", "12-00"), ParserUtil.parseTimeSlot("10-00", "12-00"));
        assertEquals(TimeSlot.of("10-00", "12-00"),
                ParserUtil.parseTimeSlot(WHITESPACE + "10-00" + WHITESPACE, WHITESPACE + "12-00"));
    }
}
