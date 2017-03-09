package tools;

import entities.DemandEntity;
import entities.ProductionEntity;
import external.CurrentStock;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

/**
 * Created by michal on 09.03.2017.
 */
public class FinderParameter {
    private String productRefNo;
    private CurrentStock stock;
    private Map<LocalDate, ProductionEntity> outputs;
    private Map<LocalDate, DemandEntity> demandsPerDay;

    public FinderParameter(String productRefNo, CurrentStock stock,
                           List<ProductionEntity> productions,
                           List<DemandEntity> demands) {
        assert productions.stream()
                .map(p -> p.getForm().getRefNo())
                .allMatch(s -> s.equals(productRefNo));

        this.stock = stock;
        this.productRefNo = productRefNo;
        this.outputs = productions.stream()
                .collect(toMap(
                        p -> p.getStart().toLocalDate(),
                        Function.identity())
                );
        this.demandsPerDay = demands.stream()
                .collect(toMap(DemandEntity::getDay,
                        Function.identity())
                );
    }

    public String getProductRefNo() {
        return productRefNo;
    }

    public long getOutputs(LocalDate day) {
        if (outputs.containsKey(day)) {
            return outputs.get(day).getOutput();
        }
        return 0;

        // alternative notation:
        // return Optional.ofNullable(outputs.get(day))
        //         .map(ProductionEntity::getOutput)
        //         .orElse(0L);
    }

    public long getLevel() {
        return stock.getLevel();
    }

    public DailyDemand getDemand(LocalDate day) {
        if (demandsPerDay.containsKey(day)) {
            DemandEntity demandEntity = demandsPerDay.get(day);
            return new DailyDemand(
                    Util.getLevel(demandEntity),
                    Util.getDeliverySchema(demandEntity)
            );
        } else {
            return null;
        }
    }
}
