package shortages;

/**
 * Created by michal on 10.03.2017.
 */
public interface ShortageRepository {
    Shortages getForProduct(String productRefNo);

    void save(Shortages shortages);
}
