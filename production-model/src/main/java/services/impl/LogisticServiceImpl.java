package services.impl;

import api.AdjustDemandDto;
import api.LogisticService;
import api.StockForecastDto;
import dao.DemandDao;
import entities.DemandEntity;
import entities.ManualAdjustmentEntity;

import java.time.Clock;
import java.time.LocalDate;

public class LogisticServiceImpl implements LogisticService {

    //Inject all
    private DemandDao demandDao;
    private ShortageBlaBlaService service;
    private Clock clock;

    /**
     * <pre>
     * Adjust demand at day to amount, delivered.
     *  New demand is stored for further reference
     *   We can change only Demands for today and future.
     *  Data from callof document should be preserved in database (DON’T OVERRIDE THEM).
     *   Should be possible to adjust demand even
     *  if there was no callof document for that product.
     *    Logistician note should be kept along with adjustment.
     * </pre>
     *
     * @param adjustment
     */
    //Transactional
    @Override
    public void adjustDemand(AdjustDemandDto adjustment) {
        if (adjustment.getAtDay().isBefore(LocalDate.now(clock))) {
            return; // TODO it is UI issue or reproduced post
        }
        DemandEntity demand = demandDao.getCurrent(adjustment.getProductRefNo(), adjustment.getAtDay());

        ManualAdjustmentEntity manualAdjustment = new ManualAdjustmentEntity();
        manualAdjustment.setLevel(adjustment.getLevel());
        manualAdjustment.setNote(adjustment.getNote());
        manualAdjustment.setDeliverySchema(adjustment.getDeliverySchema());

        demand.getAdjustment().add(manualAdjustment);

        service.processShortages_Logistic(adjustment.getProductRefNo());
    }

    /**
     * <pre>
     * Daily processing of callof document:
     * for all products included in callof document
     *   New demand are stored for further reference
     * </pre>
     *
     * @param document
     */
    //Transactional
    @Override
    public void processCallof(Object document) {
        // TODO implement me later
        // processShortages_Warehouse()
    }

    //ReadOnly
    @Override
    public StockForecastDto getStockForecast(String productRefNo) {
        return new StockForecastDto();
    }

}
