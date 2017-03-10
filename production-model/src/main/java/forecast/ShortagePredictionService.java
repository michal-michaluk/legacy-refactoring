package forecast;

import dao.ShortageDao;
import entities.ShortageEntity;
import external.JiraService;
import external.NotificationsService;
import forecast.Forecast;
import forecast.ForecastFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;

import static forecast.DateRange.range;

/**
 * Created by michal on 10.03.2017.
 */
public class ShortagePredictionService {

    //Inject all
    private ShortageDao shortageDao;
    private ForecastFactory factory;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    public void processShortages_Planner(String productRefNo) {
        processShortages(productRefNo, shortages -> notificationService.markOnPlan(shortages));
    }

    public void processShortages_Quality(String productRefNo) {
        processShortages(productRefNo, shortages -> notificationService.softNotifyPlanner(shortages));
    }

    public void processShortages_Logistic(String productRefNo) {
        processShortages(productRefNo, shortages -> notificationService.alertPlanner(shortages));
    }

    public void processShortages_Warehouse(String productRefNo) {
        processShortages(productRefNo, shortages -> notificationService.alertPlanner(shortages));
    }

    private void processShortages(String productRefNo,
                                  Consumer<List<ShortageEntity>> notification) {
        LocalDate today = LocalDate.now(clock);
        Forecast forecast = factory.create(productRefNo, today);
        List<ShortageEntity> shortages = forecast.findShortages(
                range(today, confShortagePredictionDaysAhead)
        );

        List<ShortageEntity> previous = shortageDao.getForProduct(productRefNo);
        if (!shortages.isEmpty() && !shortages.equals(previous)) {
            notification.accept(shortages);
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
