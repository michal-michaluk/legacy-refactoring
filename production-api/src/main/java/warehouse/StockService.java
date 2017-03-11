package warehouse;

/**
 * It is external system API.
 * implemented with Warehouse System API.
 */
public interface StockService {
    CurrentStock getCurrentStock(String productRefNo);
}
