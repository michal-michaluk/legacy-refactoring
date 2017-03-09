package tools;

import api.AdjustDemandDto;
import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class ShortageFinder {

    private final FinderParameter parameter;

    public ShortageFinder(FinderParameter parameter) {
        this.parameter = parameter;
    }

    public long getLocked() {
        return parameter.getLocked();
    }

    public List<ShortageEntity> findShortages(PredictionRange range) {
        // TODO ASK including locked or only proper parts
        // TODO ASK current stock or on day start? what if we are in the middle of production a day?
        String productRefNo = parameter.getProductRefNo();
        long level = parameter.getLevel();

        List<ShortageEntity> gap = new LinkedList<>();
        for (LocalDate day : range.getDays()) {
            DailyDemand demand = parameter.getDemand(day);
            if (demand == null) {
                long production = parameter.getOutputs(day);
                level += production;
                continue;
            }
            long produced = parameter.getOutputs(day);

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
                ShortageEntity entity = new ShortageEntity();
                entity.setRefNo(productRefNo);
                entity.setFound(LocalDate.now());
                entity.setAtDay(day);
                gap.add(entity);
            }
            long endOfDayLevel = level + produced - demand.getLevel();
            // TODO: ASK accumulated shortages or reset when under zero?
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap;
    }

    public static List<ShortageEntity> tryShortages(PredictionRange range, CurrentStock stock,
                                                    List<ProductionEntity> productions, List<DemandEntity> demands,
                                                    AdjustDemandDto withAdjustement) {
        // TODO do we need that ?
        return null;
    }
}
