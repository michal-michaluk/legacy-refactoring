package services.impl;

import dao.DemandDao;
import dao.ProductionDao;
import entities.ProductionEntity;
import external.CurrentStock;
import external.StockService;
import tools.DailyDemand;
import tools.FinderParameter;
import tools.ShortageFinder;
import tools.Util;

import java.time.LocalDate;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Created by michal on 09.03.2017.
 */
public class ShortageFinderFactory {

    // inject with spring
    private DemandDao demandDao;
    private StockService stockService;
    private ProductionDao productionDao;

    public ShortageFinder create(String productRefNo, LocalDate today) {
        CurrentStock stock = stockService.getCurrentStock(productRefNo);

        Map<LocalDate, Long> outputs = loadProductions(productRefNo, today);
        Map<LocalDate, DailyDemand> demandsPerDay = loadDailyDemands(productRefNo, today);

        return new ShortageFinder(new FinderParameter(
                productRefNo, stock, outputs, demandsPerDay
        ));
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
                        Util.getDeliverySchema(demandEntity)
                ))
                .collect(toMap(DailyDemand::getDate,
                        Function.identity())
                );
    }
}
