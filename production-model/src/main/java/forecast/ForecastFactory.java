package forecast;

import dao.DemandDao;
import dao.ProductionDao;
import entities.ProductionEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import external.StockService;
import tools.DailyDemand;
import tools.Util;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Created by michal on 09.03.2017.
 */
public class ForecastFactory {

    // inject with spring
    private DemandDao demandDao;
    private StockService stockService;
    private ProductionDao productionDao;


    public Forecast create(String productRefNo, LocalDate today) {
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        Map<LocalDate, Long> outputs = loadProductions(productRefNo, today);
        Map<LocalDate, DailyDemand> demandsPerDay = loadDailyDemands(productRefNo, today);

        return new Forecast(productRefNo, stock, outputs, demandsPerDay);
    }

    private Map<LocalDate, Long> loadProductions(String productRefNo, LocalDate today) {
        return productionDao.findFromTime(productRefNo, today.atStartOfDay()).stream()
                .collect(toMap(
                        p -> p.getStart().toLocalDate(),
                        ProductionEntity::getOutput)
                );
    }

    private Map<LocalDate, DailyDemand> loadDailyDemands(String productRefNo, LocalDate today) {
        return demandDao.findFrom(today.atStartOfDay(), productRefNo)
                .stream()
                .map(demandEntity -> new DailyDemand(
                        demandEntity.getDay(),
                        Util.getLevel(demandEntity),
                        pickStrategy(Util.getDeliverySchema(demandEntity))
                ))
                .collect(toMap(DailyDemand::getDate,
                        Function.identity())
                );
    }

    private Calc pickStrategy(DeliverySchema schema) {
        if (schema == DeliverySchema.atDayStart) {
            return Calc.atDayStart;
        } else if (schema == DeliverySchema.tillEndOfDay) {
            return Calc.tillEndOfDay;
        } else if (schema == DeliverySchema.every3hours) {
            // TODO WTF ?? we need to rewrite that app :/
            return Calc.NOT_IMPLEMENTED;
        } else {
            // TODO implement other variants
            return Calc.NOT_IMPLEMENTED;
        }
    }
}
