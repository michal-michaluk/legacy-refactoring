package forecast;

import java.time.LocalDate;

/**
 * Created by michal on 10.03.2017.
 */
public interface ForecastRepository {
    Forecast create(String productRefNo, LocalDate today);
}
