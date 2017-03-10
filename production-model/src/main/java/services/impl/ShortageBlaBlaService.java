package services.impl;

import dao.ShortageDao;
import entities.ShortageEntity;
import external.JiraService;
import external.NotificationsService;
import forecast.Forecast;
import forecast.ForecastFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

import static forecast.DateRange.range;

/**
 * Created by michal on 10.03.2017.
 */
public class ShortageBlaBlaService {

    //Inject all
    private ShortageDao shortageDao;
    private ForecastFactory factory;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;


    protected void notification1(List<ShortageEntity> shortages) {
        notificationService.markOnPlan(shortages);
    }

    protected void notification2(List<ShortageEntity> shortages) {
        notificationService.softNotifyPlanner(shortages);
    }

    protected void notification3(List<ShortageEntity> shortages) {
        notificationService.alertPlanner(shortages);
    }

    public void processShortages_Planner(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        Forecast forecast = factory.create(productRefNo, today);
        List<ShortageEntity> shortages = forecast.findShortages(
                range(today, confShortagePredictionDaysAhead)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notification1(shortages);
            if (forecast.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    /**
     *  If locking parts can lead to insufficient stock for next deliveries,
     *    parts recovery should have high priority.
     *  If there is a potential shortage in particular days,
     *    we need to rise an soft notification to planner.
     *
     * or:
     *  If demand is not fulfilled by current product stock and production forecast
     *    there is a shortage in particular days and we need to rise an alert.
     */
    public void processShortages_Quality(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        Forecast forecast = factory.create(productRefNo, today);
        List<ShortageEntity> shortages = forecast.findShortages(
                range(today, confShortagePredictionDaysAhead)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notification2(shortages);
            if (forecast.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    /**
     *  If new demand is not fulfilled by  current product stock and production forecast
     *    there is a shortage in particular days and we need to rise an alert.
     *    planner should be notified,
     *    if there are locked parts on stock,
     *      QA task for recovering them should have high priority.
     *
     *
     *   If new demand is not fulfilled by product stock and production forecast
     *     there is a shortage in particular days and we need to rise an alert.
     *     planner should be notified in that case,
     *     if there are locked parts on stock,
     *       QA task for recovering them should have high priority.
     */
    public void processShortages_Logistic(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        Forecast forecast = factory.create(productRefNo, today);
        List<ShortageEntity> shortages = forecast.findShortages(
                range(today, confShortagePredictionDaysAhead)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notification3(shortages);
            if (forecast.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }

    public void processShortages_Warehouse(String productRefNo) {
        LocalDate today = LocalDate.now(clock);
        Forecast forecast = factory.create(productRefNo, today);
        List<ShortageEntity> shortages = forecast.findShortages(
                range(today, confShortagePredictionDaysAhead)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notification3(shortages);
            if (forecast.getLocked() > 0 &&
                    shortages.get(0).getAtDay()
                            .isBefore(today.plusDays(confIncreaseQATaskPriorityInDays))) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageDao.save(shortages);
        }
        if (shortages.isEmpty() && !previous.isEmpty()) {
            shortageDao.delete(productRefNo);
        }
    }
}
