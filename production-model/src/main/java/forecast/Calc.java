package forecast;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import tools.DailyDemand;

/**
 * Created by michal on 09.03.2017.
 */
public interface Calc {

    Calc atDayStart = (level, demand, produced) -> level - demand.getLevel();
    Calc tillEndOfDay = (level, demand, produced) -> level - demand.getLevel() + produced;
    Calc NOT_IMPLEMENTED = (level, demand, produced) -> {
        throw new NotImplementedException();
    };

    long calculate(long level, DailyDemand demand, long produced);
}
