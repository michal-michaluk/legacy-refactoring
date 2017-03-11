package shortages;

import quality.QualityTaskService;

import java.time.Clock;
import java.time.LocalDate;
import java.util.function.Consumer;

import static shortages.DateRange.range;

/**
 * Created by michal on 10.03.2017.
 */
public class ShortagePredictionService {

    //Inject all
    private ShortageRepository shortageRepository;
    private ForecastRepository forecasts;

    private NotificationsService notificationService;
    private QualityTaskService qualityTaskService;
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
        Forecast forecast = forecasts.create(productRefNo, today);
        Shortages shortages = forecast.findShortages(
                range(today, confShortagePredictionDaysAhead)
        );

        Shortages previous = shortageRepository.getForProduct(productRefNo);
        if (shortages.isDifferent(previous)) {
            notification.accept(shortages);
            if (shortages.shouldIncreasePriority(today)) {
                qualityTaskService.increasePriorityFor(productRefNo);
            }
            shortageRepository.save(shortages);
        }
    }
}
