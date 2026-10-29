package seedu.address.logic.commands;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.IntStream;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.statistics.AssessmentRecord;
import seedu.address.model.statistics.LessonRecord;

/** Uses displayed record numbers while retaining stable identities inside historical entries. */
public class HistoryCommand extends Command {
    private final String operation;
    private final Index target;
    private final AssessmentDetails assessment;

    /** Creates an operation with a displayed student or assessment record number and its details. */
    public HistoryCommand(String operation, Index target, AssessmentDetails assessment) {
        this.operation = operation;
        this.target = target;
        this.assessment = assessment;
    }

    /** Formats command-specific syntax and an example for parser and execution errors. */
    public static String usage(String operation) {
        String example = "\nExample: " + operation;
        String assessmentFields = " date/YYYY-MM-DD subject/SUBJECT category/CATEGORY title/TITLE "
                + "score/SCORE max/MAX";
        String assessmentExample = " 1 date/2026-10-01 subject/Mathematics category/Quiz title/Fractions "
                + "score/18 max/25";
        return switch (operation) {
            case "list-lessons" -> operation + ": Lists all recorded lessons." + example;
            case "list-assessments" -> operation + ": Lists all recorded assessments." + example;
            case "delete-lesson" -> operation + ": Deletes a recorded lesson. "
                    + "Parameters: RECORD_NUMBER (Lesson #N from list-lessons)" + example + " 1";
            case "delete-assessment" -> operation + ": Deletes an assessment record. "
                    + "Parameters: RECORD_NUMBER (from list-assessments)" + example + " 1";
            case "record-assessment" -> operation + ": Records an assessment result for a student. "
                    + "Parameters: INDEX" + assessmentFields + example + assessmentExample;
            default -> {
                assert operation.equals("edit-assessment") : "Unknown history operation: " + operation;
                yield operation + ": Replaces an assessment record. "
                        + "Parameters: RECORD_NUMBER (from list-assessments)" + assessmentFields + example
                        + assessmentExample;
            }
        };
    }

    @Override
    public boolean isReadOnly() {
        return operation.startsWith("list-");
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        List<LessonRecord> lessons = new ArrayList<>(model.getAddressBook().getLessonRecords());
        List<AssessmentRecord> assessments = new ArrayList<>(model.getAddressBook().getAssessmentRecords());
        if (isReadOnly()) {
            return new CommandResult(list(model, lessons, assessments));
        }
        try {
            LocalDate date = assessment != null ? assessment.date() : null;
            if (date != null && date.isAfter(LocalDate.now(model.getClock()))) {
                throw new IllegalArgumentException("Historical record dates cannot be in the future.");
            }
            String feedback;
            if (operation.equals("delete-lesson")) {
                LessonRecord old = resolveRecord(lessons, target, "lesson", "list-lessons");
                lessons.remove(old);
                feedback = "Deleted lesson #" + target.getOneBased() + ": "
                        + studentLabel(model, old.studentId(), old.studentName()) + " | " + old.date()
                        + " " + old.start() + "-" + old.end();
            } else if (operation.equals("record-assessment")) {
                Person student = resolveStudent(model, target);
                String changedId = UUID.randomUUID().toString();
                assessments.add(assessment.record(changedId, student.getStudentId(), student.getName().fullName));
                feedback = "Recorded assessment #" + assessments.size() + ": " + student.getName() + " | " + date
                        + " | " + assessment.title();
            } else {
                AssessmentRecord old = resolveRecord(assessments, target, "assessment", "list-assessments");
                if (operation.equals("delete-assessment")) {
                    assessments.remove(old);
                } else {
                    assessments.set(assessments.indexOf(old),
                            assessment.record(old.id(), old.studentId(), old.studentName()));
                }
                feedback = (operation.equals("delete-assessment") ? "Deleted" : "Updated") + " assessment #"
                        + target.getOneBased() + ": " + studentLabel(model, old.studentId(), old.studentName())
                        + " | " + (assessment == null ? old.date() : assessment.date()) + " | "
                        + (assessment == null ? old.title() : assessment.title());
            }
            model.setHistory(lessons, assessments);
            return new CommandResult(feedback);
        } catch (IllegalArgumentException | CommandException e) {
            throw new CommandException(e.getMessage() + "\n" + usage(operation), e);
        }
    }

    private static <T> T resolveRecord(List<T> records, Index index, String type, String listCommand) {
        if (index.getZeroBased() >= records.size()) {
            throw new IllegalArgumentException("Invalid " + type + " number. Run " + listCommand
                    + " and choose a number from that list.");
        }
        return records.get(index.getZeroBased());
    }

    /** Resolves against the currently displayed student list, as other student commands do. */
    public static Person resolveStudent(Model model, Index index) throws CommandException {
        if (index.getZeroBased() >= model.getFilteredPersonList().size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        return model.getFilteredPersonList().get(index.getZeroBased());
    }

    private String list(Model model, List<LessonRecord> lessons, List<AssessmentRecord> assessments) {
        String heading = operation.equals("list-lessons") ? "Recorded lessons" : "Assessments";
        StringBuilder result = new StringBuilder(heading + " (all dates):");
        if (operation.equals("list-lessons")) {
            List<Integer> indices = IntStream.range(0, lessons.size()).boxed()
                    .sorted(Comparator.comparing((Integer i) -> lessons.get(i).date())
                            .thenComparing(i -> lessons.get(i).start())).toList();
            int rowNumber = 1;
            for (int index : indices) {
                LessonRecord r = lessons.get(index);
                result.append("\n").append(rowNumber++).append(". ")
                        .append(studentLabel(model, r.studentId(), r.studentName())).append(" | ")
                        .append(r.date()).append(" ").append(r.start()).append("-").append(r.end())
                        .append(" | ").append(r.subject()).append(" | ")
                        .append(r.outcome().name().toLowerCase(Locale.ROOT))
                        .append(" | Lesson #").append(index + 1);
            }
            if (lessons.isEmpty()) {
                result.append(" none");
            }
        } else {
            for (int i = 0; i < assessments.size(); i++) {
                AssessmentRecord r = assessments.get(i);
                result.append("\n").append(i + 1).append(". ")
                        .append(studentLabel(model, r.studentId(), r.studentName())).append(" | ")
                        .append(r.date()).append(" | ").append(r.subject()).append(" | ").append(r.category())
                        .append(" | ").append(r.title()).append(" | ").append(r.score().toPlainString())
                        .append("/").append(r.maximum().toPlainString());
            }
            if (assessments.isEmpty()) {
                result.append(" none");
            }
        }
        return result.toString();
    }

    private static String studentLabel(Model model, String id, String recordedName) {
        return model.getAddressBook().getPersonList().stream().filter(p -> p.getStudentId().equals(id))
                .map(p -> p.getName().fullName).findFirst()
                .orElse(recordedName + " (deleted)");
    }

    /** Validated assessment arguments shared by recording and correction. */
    public record AssessmentDetails(LocalDate date, String subject, String category, String title,
            BigDecimal score, BigDecimal maximum) {
        /** Creates a source record using the current or retained identity. */
        public AssessmentRecord record(String id, String studentId, String name) {
            return new AssessmentRecord(id, studentId, name, date, subject, category, title, score, maximum);
        }
    }
}
