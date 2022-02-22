package ee.ut.math.tvt.salessystem.logic;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {

    private static final Logger log = LogManager.getLogger(ShoppingCart.class);

    private final SalesSystemDAO dao;
    private final List<SoldItem> items = new ArrayList<>();

    public ShoppingCart(SalesSystemDAO dao) { this.dao = dao; }

    /**
     * Add new SoldItem to table.
     */
    public void addItem(SoldItem item) {
        SoldItem cartItem = getSoldItemByStock(item.getStockItem());
        int amountLeft = (cartItem != null) ? item.getStockItem().getQuantity() - cartItem.getQuantity() : item.getStockItem().getQuantity();

        if (amountLeft - item.getQuantity() < 0) {
            throw new SalesSystemException(String.format("Cannot add %d pcs of %s to cart because there are only %d left in warehouse.", item.getQuantity(), item.getName(), amountLeft));
        }

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
        } else {
            items.add(item);
        }
        log.debug(() -> String.format("Added %s quantity of %d", item.getName(), item.getQuantity()));
    }

    public boolean removeItem(long itemId) {
        return items.stream().filter(item -> item.getStockItem().getId() == itemId).findFirst().filter(items::remove).isPresent();
    }

    public boolean removeItem(SoldItem item) {
        return items.remove(item);
    }


    public double getSum() {
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    public List<SoldItem> getAll() {
        return items;
    }

    public SoldItem getSoldItemByStock(StockItem stockItem) {
        return items.stream().filter(item -> item.getStockItem() == stockItem).findFirst().orElse(null);
    }

    public void cancelCurrentPurchase() {
        items.clear();
    }

    public void submitCurrentPurchase() {
        // note the use of transactions. InMemorySalesSystemDAO ignores transactions
        // but when you start using hibernate in lab5, then it will become relevant.
        // what is a transaction? https://stackoverflow.com/q/974596

        if (items.isEmpty()) {
            log.info("Cannot confirm empty cart");
            return;
        }

        dao.beginTransaction();
        try {
            for (SoldItem item : items) {
                dao.saveSoldItem(item);
                item.getStockItem().setQuantity(item.getStockItem().getQuantity() - item.getQuantity());
            }
            dao.createReceipt(items);
            items.clear();
            dao.commitTransaction();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            dao.rollbackTransaction();
            throw e;
        }
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
