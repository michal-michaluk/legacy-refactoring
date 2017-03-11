package shortages;

import lombok.Value;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michal on 10.03.2017.
 */
@Value
public class Shortages {

    private final String productRefNo;
    private final long locked;
    private final LocalDate created = LocalDate.now();
    private final List<LocalDate> dates;

    public static ShortagesBuilder builder(String productRefNo, long initLevel, long locked) {
        return new ShortagesBuilder(productRefNo, initLevel, locked);
    }

    public boolean shouldIncreasePriority(LocalDate date) {
        return locked > 0 && isBefore(date);
    }

    public boolean isDifferent(Shortages other) {
        return !dates.isEmpty() && !dates.equals(other.dates);
    }

    public boolean isBefore(LocalDate date) {
        return dates.get(0).isBefore(date);
    }

    public static class ShortagesBuilder {
        private final String productRefNo;
        private final long locked;
        private List<LocalDate> dates = new LinkedList<>();

        private ShortagesBuilder(String productRefNo, long initLevel, long locked) {
            this.productRefNo = productRefNo;
            this.locked = locked;
        }

        public void add(LocalDate day) {
            dates.add(day);
        }

        public Shortages build() {
            return new Shortages(productRefNo, locked, Collections.unmodifiableList(dates));
        }
    }
}
