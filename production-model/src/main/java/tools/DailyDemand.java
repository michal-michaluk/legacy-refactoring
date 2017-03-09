package tools;

import enums.DeliverySchema;
import lombok.Value;

import java.time.LocalDate;

/**
 * Created by michal on 09.03.2017.
 */
@Value
public class DailyDemand {
    private final LocalDate date;
    private final long level;
    private final DeliverySchema schema;
}
