package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Warehouse {
    private static final Logger log = LogManager.getLogger(Warehouse.class);
    private final SalesSystemDAO dao;

    public Warehouse(SalesSystemDAO dao) {
        this.dao = dao;
    }

    public void processStockItem(StockItem newItem) {
        dao.beginTransaction();
        try {
            StockItem currentItem = dao.findStockItem(newItem.getId());
            if (currentItem == null) {
                dao.saveStockItem(newItem);
                log.info("new product added");
            } else {
                currentItem.setName(newItem.getName());
                currentItem.setDescription(newItem.getDescription());
                currentItem.setPrice(newItem.getPrice());
                currentItem.setQuantity(newItem.getQuantity());
                dao.saveStockItem(currentItem);
                log.info("product modified");
            }
            dao.commitTransaction();
        } catch (Exception e) {
            dao.rollbackTransaction();
            log.error(String.format("Unable to save StockItem:%n%s", e.getMessage()));
        }
    }
}
