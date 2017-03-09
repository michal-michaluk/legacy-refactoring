package tools;

import entities.ProductionEntity;
import external.CurrentStock;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created by michal on 09.03.2017.
 */
public class FinderParameter {
    private String productRefNo;
    private CurrentStock stock;
    private Map<LocalDate, Long> outputs;
    private Map<LocalDate, DailyDemand> demandsPerDay;

    public FinderParameter(String productRefNo, CurrentStock stock,
                           Map<LocalDate, Long> outputs,
                           Map<LocalDate, DailyDemand> demandsPerDay) {
        this.stock = stock;
        this.productRefNo = productRefNo;
        this.outputs = outputs;
        this.demandsPerDay = demandsPerDay;
    }

    public String getProductRefNo() {
        return productRefNo;
    }

    public long getOutputs(LocalDate day) {
        return outputs.getOrDefault(day, 0L);
    }

    public long getLevel() {
        return stock.getLevel();
    }

    public long getLocked() {
        return stock.getLocked();
    }

    public DailyDemand getDemand(LocalDate day) {
        return demandsPerDay.get(day);
    }
}
