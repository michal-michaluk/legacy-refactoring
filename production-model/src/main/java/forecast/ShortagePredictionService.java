package forecast;

import external.JiraService;
import external.NotificationsService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.function.Consumer;

import static forecast.DateRange.range;

/**
 * Created by michal on 10.03.2017.
 */
public class ShortagePredictionService {

    //Inject all
    private ShortageRepository shortageRepository;
    private ForecastFactory factory;

    private NotificationsService notificationService;
    private JiraService jiraService;
    private Clock clock;

    private int confShortagePredictionDaysAhead;
    private long confIncreaseQATaskPriorityInDays;

    public void processShortages_Planner(String productRefNo) {
        processShortages(productRefNo,
                shortages -> notificationService.markOnPlan(shortages.getEntities()));
    }

    public void processShortages_Quality(String productRefNo) {
        processShortages(productRefNo,
                shortages -> notificationService.softNotifyPlanner(shortages.getEntities()));
    }

    public void processShortages_Logistic(String productRefNo) {
        processShortages(productRefNo,
                shortages -> notificationService.alertPlanner(shortages.getEntities()));
    }

    public void processShortages_Warehouse(String productRefNo) {
        processShortages(productRefNo,
                shortages -> notificationService.alertPlanner(shortages.getEntities()));
    }

    private void processShortages(String productRefNo,
                                  Consumer<Shortages> notification) {
        LocalDate today = LocalDate.now(clock);
        Forecast forecast = factory.create(productRefNo, today);
        Shortages shortages = forecast.findShortages(
                range(today, confShortagePredictionDaysAhead)
        );

        Shortages previous = shortageRepository.getForProduct(productRefNo);
        if (shortages.isDifferent(previous)) {
            notification.accept(shortages);
            if (shortages.shouldIncreasePriority(today)) {
                jiraService.increasePriorityFor(productRefNo);
            }
            shortageRepository.save(shortages);
        }
    }
}
