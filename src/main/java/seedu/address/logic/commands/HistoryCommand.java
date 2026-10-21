package seedu.address.logic.commands;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.statistics.AssessmentRecord;
import seedu.address.model.statistics.LessonRecord;
import seedu.address.model.statistics.LessonRecord.Outcome;

/** Uses displayed record numbers while retaining stable identities inside historical entries. */
public class HistoryCommand extends Command {
    private final String operation;
    private final Index target;
    private final LessonDetails lesson;
    private final AssessmentDetails assessment;

    /** Creates an operation with a displayed student or historical record number and its details. */
    public HistoryCommand(String operation, Index target, LessonDetails lesson, AssessmentDetails assessment) {
        this.operation = operation;
        this.target = target;
        this.lesson = lesson;
        this.assessment = assessment;
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
            LocalDate date = lesson != null ? lesson.date() : assessment != null ? assessment.date() : null;
            if (date != null && date.isAfter(LocalDate.now(model.getClock()))) {
                throw new IllegalArgumentException("Historical record dates cannot be in the future.");
            }
            String feedback;
            if (operation.equals("record-lesson") || operation.equals("record-assessment")) {
                Person student = resolveStudent(model, target);
                String changedId = UUID.randomUUID().toString();
                if (lesson != null) {
                    lessons.add(lesson.record(changedId, student.getStudentId(), student.getName().fullName,
                            student.getSubject().value));
                    feedback = "Recorded lesson #" + lessons.size() + ": " + student.getName() + " | " + date
                            + " | " + lesson.outcome().name().toLowerCase(Locale.ROOT);
                } else {
                    assessments.add(assessment.record(changedId, student.getStudentId(), student.getName().fullName));
                    feedback = "Recorded assessment #" + assessments.size() + ": " + student.getName() + " | " + date
                            + " | " + assessment.title();
                }
            } else if (operation.endsWith("lesson")) {
                LessonRecord old = resolveRecord(lessons, target, "lesson", "list-lessons");
                if (operation.equals("delete-lesson")) {
                    lessons.remove(old);
                } else {
                    if (old.sourceSessionId() != null && !old.date().equals(lesson.date())) {
                        throw new IllegalArgumentException("A scheduled lesson's date cannot be changed. "
                                + "Delete it and record the correct date instead.");
                    }
                    lessons.set(lessons.indexOf(old), new LessonRecord(old.id(), old.studentId(), old.studentName(),
                            lesson.subject(), old.sourceSessionId() == null ? lesson.date() : old.date(),
                            lesson.start(), lesson.end(), lesson.outcome(), old.sourceSessionId()));
                }
                feedback = (operation.equals("delete-lesson") ? "Deleted" : "Updated") + " lesson #"
                        + target.getOneBased() + ": " + studentLabel(model, old.studentId(), old.studentName())
                        + " | " + (lesson == null ? old.date() : lesson.date()) + " | "
                        + (lesson == null ? old.outcome() : lesson.outcome()).name().toLowerCase(Locale.ROOT);
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
        } catch (IllegalArgumentException e) {
            throw new CommandException(e.getMessage(), e);
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
        StringBuilder result = new StringBuilder(operation + " (all dates):");
        if (operation.equals("list-lessons")) {
            for (int i = 0; i < lessons.size(); i++) {
                LessonRecord r = lessons.get(i);
                result.append("\n").append(i + 1).append(". ")
                        .append(studentLabel(model, r.studentId(), r.studentName())).append(" | ")
                        .append(r.date()).append(" ").append(r.start()).append("-").append(r.end())
                        .append(" | ").append(r.subject()).append(" | ")
                        .append(r.outcome().name().toLowerCase(Locale.ROOT));
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

    /** Validated lesson arguments; new lessons capture the student's current subject. */
    public record LessonDetails(LocalDate date, LocalTime start, LocalTime end, Outcome outcome, String subject) {
        /** Creates a source record using the current or retained identity. */
        public LessonRecord record(String id, String studentId, String name, String fallbackSubject) {
            return new LessonRecord(id, studentId, name, subject == null ? fallbackSubject : subject,
                    date, start, end, outcome);
        }
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
