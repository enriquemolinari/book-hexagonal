package hexagon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class FormattedDate {

    private static final String FORMAT = "MM-dd-yyyy";
    private final LocalDate dateTime;

    public FormattedDate(LocalDate dateTime) {
        this.dateTime = dateTime;
    }

    public String toString() {
        return this.dateTime.format(DateTimeFormatter.ofPattern(FORMAT));
    }
}
