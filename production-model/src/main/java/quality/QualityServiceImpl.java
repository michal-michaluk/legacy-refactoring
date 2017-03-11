package quality;

import warehouse.StorageUnit;
import shortages.ShortagePredictionService;

public class QualityServiceImpl implements QualityService {

    private ShortagePredictionService service;

    /**
     * <pre>
     *  Lock all parts from storage unit on stock.
     *  parts from storage unit are locked on stock.
     *  </pre>
     */
    //Transactional
    @Override
    public void lock(StorageUnit unit) {
        service.processShortages_Quality(unit.getProductRefNo());
    }

    /**
     * <pre>
     *  Unlock storage unit, recover X parts, Y parts was scrapped.
     *  stock.unlock(storageUnit, recovered, scrapped)
     *  Recovered parts are back on stock.
     *  Scrapped parts are removed from stock.
     * </pre>
     */
    //Transactional
    @Override
    public void unlock(StorageUnit unit, long recovered, long scrapped) {
        service.processShortages_Quality(unit.getProductRefNo());
    }

}
