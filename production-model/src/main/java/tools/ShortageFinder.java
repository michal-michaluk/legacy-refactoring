package tools;

import api.AdjustDemandDto;
import entities.DemandEntity;
import entities.ProductionEntity;
import entities.ShortageEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ShortageFinder {

    public static List<ShortageEntity> tryShortages(PredictionRange range, CurrentStock stock,
                                                    List<ProductionEntity> productions, List<DemandEntity> demands,
                                                    AdjustDemandDto withAdjustement) {
        // TODO do we need that ?
        return null;
    }

    public static List<ShortageEntity> findShortages(PredictionRange range,
                                                     FinderParameter parameter) {
        // TODO ASK including locked or only proper parts
        // TODO ASK current stock or on day start? what if we are in the middle of production a day?
        String productRefNo = parameter.getProductRefNo();
        long level = parameter.getLevel();

        List<ShortageEntity> gap = new LinkedList<>();
        for (LocalDate day : range.getDays()) {
            DemandEntity demand = parameter.getDemand(day);
            if (demand == null) {
                long production = parameter.getOutputs(day);
                level += production;
                continue;
            }
            long produced = parameter.getOutputs(day);

            long levelOnDelivery;
            if (Util.getDeliverySchema(demand) == DeliverySchema.atDayStart) {
                levelOnDelivery = level - Util.getLevel(demand);
            } else if (Util.getDeliverySchema(demand) == DeliverySchema.tillEndOfDay) {
                levelOnDelivery = level - Util.getLevel(demand) + produced;
            } else if (Util.getDeliverySchema(demand) == DeliverySchema.every3hours) {
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
            long endOfDayLevel = level + produced - Util.getLevel(demand);
            // TODO: ASK accumulated shortages or reset when under zero?
            level = endOfDayLevel >= 0 ? endOfDayLevel : 0;
        }
        return gap;
    }

    private ShortageFinder() {
    }
}
