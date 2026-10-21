package seedu.address.model.statistics;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Shared strict parsing for commands and persisted historical records. */
public final class HistoryValidation {
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    private HistoryValidation() {
    }

    /** Requires a nonblank single-line value. */
    public static String text(String value) {
        if (value == null || value.isBlank() || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException("Historical text fields must be nonblank, single-line text.");
        }
        return value.trim();
    }

    /** Parses an exact ISO calendar date without silently adjusting invalid days. */
    public static LocalDate date(String value) {
        if (value == null || !value.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("Use a valid date in YYYY-MM-DD format.");
        }
        return LocalDate.parse(value);
    }

    /** Parses a 24-hour, minute-precision time. */
    public static LocalTime time(String value) {
        if (value == null || !value.matches("\\d{2}:\\d{2}")) {
            throw new IllegalArgumentException("Use a valid time in HH:mm format.");
        }
        return LocalTime.parse(value, TIME);
    }
}
