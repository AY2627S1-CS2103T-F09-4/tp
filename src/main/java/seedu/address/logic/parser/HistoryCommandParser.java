package seedu.address.logic.parser;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.HistoryCommand;
import seedu.address.logic.commands.HistoryCommand.AssessmentDetails;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Subject;
import seedu.address.model.statistics.HistoryValidation;

/** Parses the historical-record command family using the existing argument tokenizer. */
public class HistoryCommandParser implements Parser<HistoryCommand> {
    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?:^|\\s)([A-Za-z][A-Za-z-]*)/");
    private final String operation;

    public HistoryCommandParser(String operation) {
        this.operation = operation;
    }

    @Override
    public HistoryCommand parse(String args) throws ParseException {
        try {
            if (operation.startsWith("list-")) {
                if (!args.isBlank()) {
                    throw new IllegalArgumentException("This history list command takes no arguments.");
                }
                return new HistoryCommand(operation, null, null);
            }
            boolean isDelete = operation.startsWith("delete-");
            String[] fields = isDelete ? new String[0]
                    : new String[]{"date", "subject", "category", "title", "score", "max"};
            ArgumentMultimap values = arguments(args, fields);
            Index target = ParserUtil.parseIndex(values.getPreamble());
            if (isDelete) {
                return new HistoryCommand(operation, target, null);
            }
            LocalDate date = HistoryValidation.date(required(values, "date"));
            BigDecimal score = new BigDecimal(required(values, "score"));
            BigDecimal maximum = new BigDecimal(required(values, "max"));
            if (maximum.signum() <= 0 || score.signum() < 0 || score.compareTo(maximum) > 0) {
                throw new IllegalArgumentException("Marks must satisfy 0 <= score <= max and max > 0.");
            }
            AssessmentDetails assessment = new AssessmentDetails(date, parseSubject(required(values, "subject")),
                    required(values, "category"), required(values, "title"), score, maximum);
            return new HistoryCommand(operation, target, assessment);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new ParseException("Invalid " + operation + ": " + e.getMessage() + "\n" + usage(), e);
        }
    }

    private String usage() {
        if (operation.startsWith("delete-")) {
            return "Usage: " + operation + " RECORD_NUMBER (from list-assessments)";
        }
        if (operation.startsWith("list-")) {
            return "Usage: " + operation;
        }
        String target = operation.startsWith("record-") ? "INDEX" : "RECORD_NUMBER";
        return "Usage: " + operation + " " + target + " date/YYYY-MM-DD "
                + "subject/SUBJECT category/CATEGORY title/TITLE score/SCORE max/MAX";
    }

    private static String parseSubject(String value) {
        if (!Subject.isValidSubject(value)) {
            throw new IllegalArgumentException(Subject.MESSAGE_CONSTRAINTS);
        }
        return value;
    }

    /** Tokenizes allowed prefixes and rejects unknown or repeated prefixes. */
    static ArgumentMultimap arguments(String args, String... fields) throws ParseException {
        List<String> allowed = Arrays.asList(fields);
        Matcher matcher = PREFIX_PATTERN.matcher(args);
        while (matcher.find()) {
            if (!allowed.contains(matcher.group(1))) {
                throw new ParseException("Unknown argument prefix: " + matcher.group(1) + "/");
            }
        }
        Prefix[] prefixes = allowed.stream().map(f -> new Prefix(f + "/")).toArray(Prefix[]::new);
        ArgumentMultimap values = ArgumentTokenizer.tokenize(args, prefixes);
        values.verifyNoDuplicatePrefixesFor(prefixes);
        return values;
    }

    private static String required(ArgumentMultimap values, String field) {
        return HistoryValidation.text(values.getValue(new Prefix(field + "/"))
                .orElseThrow(() -> new IllegalArgumentException("Missing " + field + "/ argument.")));
    }
}
