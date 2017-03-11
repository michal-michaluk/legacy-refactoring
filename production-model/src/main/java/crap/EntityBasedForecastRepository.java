package crap;

import demnd.DemandDao;
import production.plan.ProductionDao;
import production.plan.ProductionEntity;
import demnd.DeliverySchema;
import warehouse.CurrentStock;
import warehouse.StockService;
import shortages.Forecast;
import shortages.ForecastRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static shortages.Util.getDeliverySchema;
import static shortages.Util.getLevel;
import static java.util.stream.Collectors.toMap;

/**
 * Created by michal on 09.03.2017.
 */
class EntityBasedForecastRepository implements ForecastRepository {

    // inject with spring
    private DemandDao demandDao;
    private StockService stockService;
    private ProductionDao productionDao;
    private Map<DeliverySchema, Forecast.Calc> calculationVariants = init();

    @Override
    public Forecast create(String productRefNo, LocalDate today) {
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        Map<LocalDate, Long> outputs = loadProductions(productRefNo, today);
        Map<LocalDate, Forecast.DailyDemand> demandsPerDay = loadDailyDemands(productRefNo, today);

        return new Forecast(
                productRefNo, stock.getLevel(),
                stock.getLocked(), outputs, demandsPerDay
        );
    }

    private Map<LocalDate, Long> loadProductions(String productRefNo, LocalDate today) {
        return productionDao.findFromTime(productRefNo, today.atStartOfDay()).stream()
                .collect(toMap(
                        p -> p.getStart().toLocalDate(),
                        ProductionEntity::getOutput)
                );
    }

    private Map<LocalDate, Forecast.DailyDemand> loadDailyDemands(String productRefNo, LocalDate today) {
        return demandDao.findFrom(today.atStartOfDay(), productRefNo)
                .stream()
                .map(demandEntity -> new Forecast.DailyDemand(
                        demandEntity.getDay(),
                        getLevel(demandEntity),
                        calculationVariants.getOrDefault(
                                getDeliverySchema(demandEntity),
                                Forecast.Calc.NOT_IMPLEMENTED)
                ))
                .collect(toMap(Forecast.DailyDemand::getDate,
                        Function.identity())
                );
    }

    private Map<DeliverySchema, Forecast.Calc> init() {
        Map<DeliverySchema, Forecast.Calc> map = new HashMap<>();
        map.put(DeliverySchema.atDayStart, Forecast.Calc.atDayStart);
        map.put(DeliverySchema.tillEndOfDay, Forecast.Calc.tillEndOfDay);
        return Collections.unmodifiableMap(map);
    }
}
