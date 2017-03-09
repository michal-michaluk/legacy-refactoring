package forecast;

import api.AdjustDemandDto;
import entities.ShortageEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import lombok.AllArgsConstructor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tools.DailyDemand;
import tools.DateRange;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class Forecast {

    private final String productRefNo;
    private final CurrentStock stock;
    private final Map<LocalDate, Long> outputs;
    private final Map<LocalDate, DailyDemand> demands;

    public long getLocked() {
        return stock.getLocked();
    }

    public List<ShortageEntity> findShortages(DateRange range) {
        // TODO ASK including locked or only proper parts
        // TODO ASK current stock or on day start? what if we are in the middle of production a day?
        long level = stock.getLevel();

        List<ShortageEntity> gap = new LinkedList<>();
        for (LocalDate day : range.getDays()) {
            DailyDemand demand = demands.getOrDefault(day, DailyDemand.zero(day));
            long produced = outputs.getOrDefault(day, 0L);

            long levelOnDelivery;
            if (demand.getSchema() == DeliverySchema.atDayStart) {
                levelOnDelivery = level - demand.getLevel();
            } else if (demand.getSchema() == DeliverySchema.tillEndOfDay) {
                levelOnDelivery = level - demand.getLevel() + produced;
            } else if (demand.getSchema() == DeliverySchema.every3hours) {
                // TODO WTF ?? we need to rewrite that app :/
                throw new NotImplementedException();
            } else {
                // TODO implement other variants
                throw new NotImplementedException();
            }

            if (!(levelOnDelivery >= 0)) {
                gap.add(createShortage(day));
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            // TODO: ASK accumulated shortages or reset when under zero?
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap;
    }

    private ShortageEntity createShortage(LocalDate day) {
        ShortageEntity entity = new ShortageEntity();
        entity.setRefNo(productRefNo);
        entity.setFound(LocalDate.now());
        entity.setAtDay(day);
        return entity;
    }

    public List<ShortageEntity> tryShortages(DateRange range, AdjustDemandDto withAdjustement) {
        // TODO do we need that ?
        return null;
    }
}
