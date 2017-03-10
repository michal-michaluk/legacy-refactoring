package forecast;

import entities.ShortageEntity;
import lombok.Builder;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by michal on 10.03.2017.
 */
public class Shortages {

    private final long locked;
    private final List<ShortageEntity> list;

    private Shortages(long locked, List<ShortageEntity> list) {
        this.locked = locked;
        this.list = list;
    }

    public static ShortagesBuilder builder(long initLevel, long locked) {
        return new ShortagesBuilder(initLevel, locked);
    }

    public boolean shouldIncreasePriority(LocalDate date) {
        return locked > 0 && isBefore(date);
    }

    public boolean isDifferent(Shortages other) {
        return !list.isEmpty() && !list.equals(other.list);
    }

    public boolean isBefore(LocalDate date) {
        return list.get(0).getAtDay().isBefore(date);
    }

    public List<ShortageEntity> getEntities() {
        return list;
    }

    public static class ShortagesBuilder {
        private final long locked;
        private List<ShortageEntity> list = new LinkedList<>();

        private ShortagesBuilder(long initLevel, long locked) {
            this.locked = locked;
        }

        public void add(String productRefNo, LocalDate day) {
            ShortageEntity entity = new ShortageEntity();
            entity.setRefNo(productRefNo);
            entity.setFound(LocalDate.now());
            entity.setAtDay(day);
            list.add(entity);
        }

        public Shortages build() {
            return new Shortages(locked, list);
        }
    }
}
