package tools;

import org.junit.Test;

import java.time.LocalDate;

/**
 * Created by michal on 09.03.2017.
 */
public class ShortageFinderTest {
    @Test
    public void findShortages() throws Exception {

        //ShortageFinder.findShortages(PredictionRange.currentWeek(), null);
        new ShortageFinder(null).findShortages(PredictionRange.range(LocalDate.now(), 7));
        //ShortageFinder.findShortages(PredictionRange.range(LocalDate.now(), LocalDate.now().plusDays(7)), null);
    }

}