package forecast;

import api.AdjustDemandDto;
import entities.ShortageEntity;
import external.CurrentStock;
import forecast.Shortages.ShortagesBuilder;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
class Forecast {

    private final String productRefNo;
    private final CurrentStock stock;
    private final Map<LocalDate, Long> outputs;
    private final Map<LocalDate, DailyDemand> demands;

    Shortages findShortages(DateRange range) {
        // TODO ASK including locked or only proper parts
        // TODO ASK current stock or on day start? what if we are in the middle of production a day?
        long level = stock.getLevel();

        ShortagesBuilder shortages = Shortages.builder(stock.getLevel(), stock.getLocked());
        for (LocalDate day : range.getDays()) {
            DailyDemand demand = demands.getOrDefault(day, DailyDemand.zero(day));
            long produced = outputs.getOrDefault(day, 0L);

            long levelOnDelivery = demand.calculate(level, produced);

            if (levelOnDelivery < 0) {
                shortages.add(productRefNo, day);
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            // TODO: ASK accumulated shortages or reset when under zero?
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return shortages.build();
    }

    List<ShortageEntity> tryShortages(DateRange range, AdjustDemandDto withAdjustement) {
        // TODO do we need that ?
        return null;
    }
}
