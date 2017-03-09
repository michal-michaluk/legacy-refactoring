package tools;

import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

/**
 * Created by michal on 09.03.2017.
 */
public class ShortageFinderTest {
    @Test
    public void findShortages() throws Exception {

        //ShortageFinder.findShortages(PredictionRange.currentWeek(), null);
        ShortageFinder.findShortages(PredictionRange.range(LocalDate.now(), 7), null);
        //ShortageFinder.findShortages(PredictionRange.range(LocalDate.now(), LocalDate.now().plusDays(7)), null);
    }

}