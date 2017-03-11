package production.plan;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Created by michal on 07.03.2017.
 */
public class Util {

    private Util() {
    }

    public static boolean isFormAvailableAt(FormEntity form, LocalDateTime start, Duration duration) {
        // TODO How to store / calculate that ?
        return false;
    }

    public static boolean canProduceOn(LineEntity line, FormEntity form) {
        return line.getMaxWeight() > form.getWeight();
    }

}
