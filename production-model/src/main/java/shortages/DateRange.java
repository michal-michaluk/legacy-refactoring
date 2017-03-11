package shortages;

import lombok.Getter;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class DateRange {

    @Getter
    private final List<LocalDate> days;

    public static DateRange range(LocalDate start, int daysAhead) {
        return new DateRange(
                Collections.unmodifiableList(
                        Stream.iterate(start, date -> date.plusDays(1))
                                .limit(daysAhead)
                                .collect(toList())
                )
        );
    }

    private DateRange(List<LocalDate> days) {
        this.days = days;
    }
}
