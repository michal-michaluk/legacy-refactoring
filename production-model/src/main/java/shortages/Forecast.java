package shortages;

import lombok.Value;
import shortages.Shortages.ShortagesBuilder;
import lombok.AllArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
public class Forecast {

    private final String productRefNo;
    private final long stock;
    private final long locked;
    private final Map<LocalDate, Long> outputs;
    private final Map<LocalDate, DailyDemand> demands;

    Shortages findShortages(DateRange range) {
        // TODO ASK including locked or only proper parts
        // TODO ASK current stock or on day start? what if we are in the middle of production a day?
        long level = stock;

        ShortagesBuilder shortages = Shortages.builder(productRefNo, stock, locked);
        for (LocalDate day : range.getDays()) {
            DailyDemand demand = demands.getOrDefault(day, DailyDemand.zero(day));
            long produced = outputs.getOrDefault(day, 0L);

            long levelOnDelivery = demand.calculate(level, produced);

            if (levelOnDelivery < 0) {
                shortages.add(day);
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            // TODO: ASK accumulated shortages or reset when under zero?
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return shortages.build();
    }

//    Shortages tryShortages(DateRange range, AdjustDemandDto withAdjustement) {
//        // TODO do we need that ?
//        return null;
//    }

    @Value
    public static class DailyDemand {
        private final LocalDate date;
        private final long level;
        private final Calc calc;

        static DailyDemand zero(LocalDate day) {
            return new DailyDemand(day, 0L,
                    (level1, demand, produced) -> level1 + produced);
        }

        long calculate(long level, long produced) {
            return calc.calculate(level, this, produced);
        }
    }

    /**
     * Production at day of expected delivery is quite complex:
     * We are able to produce and deliver just in time at same day
     * but depending on delivery time or scheme of multiple deliveries,
     * we need to plan properly to have right amount of parts ready before delivery time.
     * <p/>
     * Typical schemas are:
     * <li>Delivery at prod day start</li>
     * <li>Delivery till prod day end</li>
     * <li>Delivery during specified shift</li>
     * <li>Multiple deliveries at specified times</li>
     * Schema changes the way how we calculate shortages.
     * Pick of schema depends on customer demand on daily basis and for each product differently.
     * Some customers includes that information in callof document,
     * other stick to single schema per product. By manual adjustments of demand,
     * customer always specifies desired delivery schema
     * (increase amount in scheduled transport or organize extra transport at given time)
     */
    public interface Calc {

        Calc atDayStart = (level, demand, produced) -> level - demand.getLevel();
        Calc tillEndOfDay = (level, demand, produced) -> level - demand.getLevel() + produced;
        Calc NOT_IMPLEMENTED = (level, demand, produced) -> {
            throw new NotImplementedException();
        };

        long calculate(long level, DailyDemand demand, long produced);
    }
}
