package crap;

import dao.DemandDao;
import dao.ProductionDao;
import entities.ProductionEntity;
import enums.DeliverySchema;
import external.CurrentStock;
import external.StockService;
import forecast.Calc;
import forecast.DailyDemand;
import forecast.Forecast;
import forecast.ForecastRepository;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static forecast.Util.getDeliverySchema;
import static forecast.Util.getLevel;
import static java.util.stream.Collectors.toMap;

/**
 * Created by michal on 09.03.2017.
 */
class EntityBasedForecastRepository implements ForecastRepository {

    // inject with spring
    private DemandDao demandDao;
    private StockService stockService;
    private ProductionDao productionDao;
    private Map<DeliverySchema, Calc> calculationVariants = init();

    @Override
    public Forecast create(String productRefNo, LocalDate today) {
        CurrentStock stock = stockService.getCurrentStock(productRefNo);
        Map<LocalDate, Long> outputs = loadProductions(productRefNo, today);
        Map<LocalDate, DailyDemand> demandsPerDay = loadDailyDemands(productRefNo, today);

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

    private Map<LocalDate, DailyDemand> loadDailyDemands(String productRefNo, LocalDate today) {
        return demandDao.findFrom(today.atStartOfDay(), productRefNo)
                .stream()
                .map(demandEntity -> new DailyDemand(
                        demandEntity.getDay(),
                        getLevel(demandEntity),
                        calculationVariants.getOrDefault(
                                getDeliverySchema(demandEntity),
                                Calc.NOT_IMPLEMENTED)
                ))
                .collect(toMap(DailyDemand::getDate,
                        Function.identity())
                );
    }

    private Map<DeliverySchema, Calc> init() {
        Map<DeliverySchema, Calc> map = new HashMap<>();
        map.put(DeliverySchema.atDayStart, Calc.atDayStart);
        map.put(DeliverySchema.tillEndOfDay, Calc.tillEndOfDay);
        return Collections.unmodifiableMap(map);
    }
}
