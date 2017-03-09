package services.impl;

import api.DeliveryNote;
import api.StorageUnit;
import api.WarehouseService;
import dao.ShortageDao;
import entities.ShortageEntity;
import external.JiraService;
import external.NotificationsService;
import forecast.Forecast;
import forecast.ForecastFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static tools.DateRange.range;

public class WarehouseServiceImpl implements WarehouseService {

    //Inject all
    private ShortageDao shortageDao;
    private ForecastFactory factory;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    /**
     * <pre>
     * Register newly produced parts on stock.
     *  new parts are available on stock.
     *  If output from production is smaller than planned
     *  it may lead to shortage in next days.
     * </pre>
     */
    //Transactional
    @Override
    public void registerNew(StorageUnit unit) {
        processShortages(unit.getProductRefNo());
    }

    /**
     * <pre>
     * Remove delivered parts from stock.
     *  If parts delivered during day exceed registered customer demand,
     *  demand for next day will be probably corrected with upcoming callof document,
     *  but in rare cases it may be caused by not registered additional delivery
     *  (lack of manual adjustments of demand in system).
     * </pre>
     */
    //Transactional
    @Override
    public void deliver(DeliveryNote note) {
        for (String productRefNo : note.getProducts()) {
            processShortages(productRefNo);
        }
    }

    public void processShortages(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        Forecast finder = factory.create(productRefNo, today);
        List<ShortageEntity> shortages = finder.findShortages(
                range(today, confShortagePredictionDaysAhead)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (shortages != null && !shortages.equals(previous)) {
            notificationService.alertPlanner(shortages);
            if (finder.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }
}
