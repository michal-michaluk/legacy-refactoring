package tools;

import enums.DeliverySchema;
import forecast.Calc;
import lombok.Value;

import java.time.LocalDate;

/**
 * Created by michal on 09.03.2017.
 */
@Value
public class DailyDemand {
    private final LocalDate date;
    private final long level;
    private final Calc calc;

    public static DailyDemand zero(LocalDate day) {
        return new DailyDemand(day, 0L,
                (level1, demand, produced) -> level1 + produced);
    }

    public long calculate(long level, long produced) {
        return calc.calculate(level, this, produced);
    }
}
