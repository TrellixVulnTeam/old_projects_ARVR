package ee.ut.math.tvt.salessystem.dataobjects;

import ee.ut.math.tvt.salessystem.SalesSystemException;

import javax.persistence.*;


/**
 * Already bought StockItem. SoldItem duplicates name and price for preserving history.
 */
@Entity
@Table(name = "soldItem")
public class SoldItem {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "soldItem_stockItem")
    private StockItem stockItem;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "price")
    private double price;

    public SoldItem(StockItem stockItem, int quantity) {
        this.stockItem = stockItem;
        this.name = stockItem.getName();
        this.price = stockItem.getPrice();
        this.quantity = quantity;
    }

    public SoldItem() {}

    public static SoldItem parseSoldItem(StockItem stockItem, String quantityStr) {
        try {
            int amount = Integer.parseInt(quantityStr);
            if (amount < 1) throw new SalesSystemException("Item quantity cannot be negative!");
            return new SoldItem(stockItem, amount);
        } catch (NumberFormatException e) {
            throw new SalesSystemException(String.format("Unexpected value \"%s\" in sold item %s quantity field!", quantityStr, stockItem.getName()));
        }
    }

    public long getId() {
        if (id == null) return stockItem.getId();
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public StockItem getStockItem() {
        return stockItem;
    }

    public long getStockId() { return stockItem.getId(); }

    @Override
    public String toString() {
        return String.format("%d,%s,%.2f,%d",
                getId(),
                getName().replace(" ", "_"),
                getPrice(),
                getQuantity());
    }
}
