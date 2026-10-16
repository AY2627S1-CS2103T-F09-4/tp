package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_PARENTS_LISTED_OVERVIEW;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandKeywordSuccess;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.CARL;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.GEORGE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.ParentContainsKeywordsPredicate;
import seedu.address.model.person.PersonContainsKeywordsPredicate;

/**
 * Contains integration tests (interaction with the Model) for {@code SearchCommand}.
 */
public class SearchCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void equals() {
        PersonContainsKeywordsPredicate firstStudentPredicate =
                new PersonContainsKeywordsPredicate(List.of("first"));
        PersonContainsKeywordsPredicate secondStudentPredicate =
                new PersonContainsKeywordsPredicate(List.of("second"));
        ParentContainsKeywordsPredicate firstParentPredicate =
                new ParentContainsKeywordsPredicate(List.of("first"));
        ParentContainsKeywordsPredicate secondParentPredicate =
                new ParentContainsKeywordsPredicate(List.of("second"));

        SearchCommand searchFirstCommand = new SearchCommand(firstStudentPredicate, firstParentPredicate, "first");
        SearchCommand searchSecondCommand = new SearchCommand(secondStudentPredicate, secondParentPredicate, "second");

        // same object -> returns true
        assertTrue(searchFirstCommand.equals(searchFirstCommand));

        // same values -> returns true
        SearchCommand searchFirstCommandCopy = new SearchCommand(firstStudentPredicate, firstParentPredicate, "first");
        assertTrue(searchFirstCommand.equals(searchFirstCommandCopy));

        // different types -> returns false
        assertFalse(searchFirstCommand.equals(1));

        // null -> returns false
        assertFalse(searchFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(searchFirstCommand.equals(searchSecondCommand));
    }

    @Test
    public void execute_zeroKeywords_noPersonFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 7) + " "
                + String.format(MESSAGE_PARENTS_LISTED_OVERVIEW, 0);
        PersonContainsKeywordsPredicate studentPredicate = prepareStudentPredicate(" ");
        ParentContainsKeywordsPredicate parentPredicate = prepareParentPredicate(" ");
        SearchCommand command = new SearchCommand(studentPredicate, parentPredicate, " ");
        expectedModel.updateFilteredPersonList(studentPredicate);
        expectedModel.updateParentList(parentPredicate);
        assertCommandKeywordSuccess(command, model, expectedMessage, expectedModel, " ");
        assertEquals(List.of(ALICE, BENSON, CARL, DANIEL, ELLE, FIONA, GEORGE), model.getFilteredPersonList());
    }

    @Test
    public void execute_multipleKeywords_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3) + " "
                + String.format(MESSAGE_PARENTS_LISTED_OVERVIEW, 0);
        PersonContainsKeywordsPredicate studentPredicate = prepareStudentPredicate("Street");
        ParentContainsKeywordsPredicate parentPredicate = prepareParentPredicate("Street");
        SearchCommand command = new SearchCommand(studentPredicate, parentPredicate, "Street");
        expectedModel.updateFilteredPersonList(studentPredicate);
        expectedModel.updateParentList(parentPredicate);
        assertCommandKeywordSuccess(command, model, expectedMessage, expectedModel, "Street");
        assertEquals(List.of(CARL, DANIEL, GEORGE), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        PersonContainsKeywordsPredicate studentPredicate = new PersonContainsKeywordsPredicate(List.of("keyword"));
        ParentContainsKeywordsPredicate parentPredicate = new ParentContainsKeywordsPredicate(List.of("keyword"));
        SearchCommand searchCommand = new SearchCommand(studentPredicate, parentPredicate, "keyword");
        String expected = SearchCommand.class.getCanonicalName() + "{studentPredicate=" + studentPredicate
                + ", " + "parentPredicate=" + parentPredicate + "}";
        assertEquals(expected, searchCommand.toString());
    }

    /**
     * Parses {@code userInput} into a {@code NameContainsKeywordsPredicate}.
     */
    private PersonContainsKeywordsPredicate prepareStudentPredicate(String userInput) {
        return new PersonContainsKeywordsPredicate(List.of(userInput));
    }
    private ParentContainsKeywordsPredicate prepareParentPredicate(String userInput) {
        return new ParentContainsKeywordsPredicate(List.of(userInput));
    }
}
