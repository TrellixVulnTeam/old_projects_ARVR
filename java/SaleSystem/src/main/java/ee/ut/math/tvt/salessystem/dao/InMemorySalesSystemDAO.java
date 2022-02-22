package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dataobjects.Receipt;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.ManyToOne;
import java.time.LocalDate;
import java.util.*;

public class InMemorySalesSystemDAO implements SalesSystemDAO {

    private static final Logger log = LogManager.getLogger(InMemorySalesSystemDAO.class);

    private final List<StockItem> stockItemList;
    private final List<SoldItem> soldItemList;
    private final List<Receipt> receipts;

    public InMemorySalesSystemDAO() {
        List<StockItem> items = new ArrayList<>();
        items.add(new StockItem(1L, "Lays chips", "Potato chips", 11.0, 5));
        items.add(new StockItem(2L, "Chupa-chups", "Sweets", 8.0, 8));
        items.add(new StockItem(3L, "Frankfurters", "Beer sauseges", 15.0, 12));
        items.add(new StockItem(4L, "Free Beer", "Student's delight", 0.0, 100));
        this.stockItemList = items;
        this.soldItemList = new ArrayList<>();
        this.receipts = new ArrayList<>();
        log.info("InMemorySalesSystemDAO started");
    }

    @Override
    public List<StockItem> findStockItems() {
        return stockItemList;
    }

    @Override
    public StockItem findStockItem(long id) {
        for (StockItem item : stockItemList) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        soldItemList.add(item);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
        stockItemList.add(stockItem);
    }

    @Override
    public void beginTransaction() {
        //There is no need to have a transaction in an in-memory solution
    }

    @Override
    public void rollbackTransaction() {
        //There is no need to have a transaction in an in-memory solution
    }

    @Override
    public void commitTransaction() {
        //There is no need to have a transaction in an in-memory solution
    }

    @Override
    public void close() {
        //No resources to close
    }

    @Override
    public void createReceipt(List<SoldItem> items) {
        receipts.add(new Receipt(items));
    }

    @Override
    public List<Receipt> getReceipts(LocalDate start, LocalDate end) {
        if (start.compareTo(end) > 0) {
            throw new SalesSystemException("Receipt query start " + start.toString() + " is later than end " + end.toString());
        }
        List<Receipt> result = new ArrayList<>();
        for (Receipt receipt : receipts) {
            LocalDate date = LocalDate.from(receipt.getTime());
            if (date.compareTo(start) >= 0 && date.compareTo(end) <= 0) result.add(receipt);
        }
        return result;
    }

    @Override
    public List<Receipt> getReceipts(int n) {
        List<Receipt> result = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            try {
                result.add(receipts.get(receipts.size() - i));
            } catch (IndexOutOfBoundsException ignored) {
                break;
            }
        }
        return result;
    }

    private long getNextSoldItemId() {
        if (!soldItemList.isEmpty())
            return Collections.max(soldItemList, Comparator.comparing(SoldItem::getId)).getId() + 1;
        return 1;
    }

    @Override
    public List<Receipt> getReceipts() {
        return receipts;
    }
}
