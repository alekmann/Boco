package no.boco.backend.rental;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Objects;

/**
 * Describes a range of dates with a start and end date
 */
public class Range {
    private LocalDate start;
    private LocalDate end;
    private Integer amount;

    public Range(LocalDate start, LocalDate end, Integer amount) {
        this.start = start;
        this.end = end;
        this.amount = amount;
    }

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Range range = (Range) o;
        return Objects.equals(start, range.start) && Objects.equals(end, range.end) && Objects.equals(amount, range.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, amount);
    }

    @Override
    public String toString() {
        return "\nRange" +
                "\nstart=" + start.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)) +
                "\nend=" + end.format(DateTimeFormatter
                .ofLocalizedDate(FormatStyle.MEDIUM)) +
                "\namount=" + amount +
                '\n';
    }
}
