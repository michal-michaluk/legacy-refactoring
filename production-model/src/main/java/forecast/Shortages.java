package forecast;

import entities.ShortageEntity;
import lombok.Value;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michal on 10.03.2017.
 */
@Value
public class Shortages {

    private final String productRefNo;
    private final long locked;
    private final List<ShortageEntity> entities;

    public static ShortagesBuilder builder(String productRefNo, long initLevel, long locked) {
        return new ShortagesBuilder(productRefNo, initLevel, locked);
    }

    public boolean shouldIncreasePriority(LocalDate date) {
        return locked > 0 && isBefore(date);
    }

    public boolean isDifferent(Shortages other) {
        return !entities.isEmpty() && !entities.equals(other.entities);
    }

    public boolean isBefore(LocalDate date) {
        return entities.get(0).getAtDay().isBefore(date);
    }

    public static class ShortagesBuilder {
        private final String productRefNo;
        private final long locked;
        private List<ShortageEntity> entities = new LinkedList<>();

        private ShortagesBuilder(String productRefNo, long initLevel, long locked) {
            this.productRefNo = productRefNo;
            this.locked = locked;
        }

        public void add(String productRefNo, LocalDate day) {
            ShortageEntity entity = new ShortageEntity();
            entity.setRefNo(productRefNo);
            entity.setFound(LocalDate.now());
            entity.setAtDay(day);
            entities.add(entity);
        }

        public Shortages build() {
            return new Shortages(productRefNo, locked, entities);
        }
    }
}
