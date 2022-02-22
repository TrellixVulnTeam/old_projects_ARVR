package ee.ut.math.tvt.salessystem.dataobjects;

import ee.ut.math.tvt.salessystem.SalesSystemException;

import javax.persistence.*;

/**
 * Stock item.
 */
@Entity
@Table(name = "stockItem")
public class StockItem {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private double price;

    @Column(name = "description")
    private String description;

    @Column(name = "quantity")
    private int quantity;

    private static final String NEGATIVE_ERROR_MESSAGE = "Negative value %s in stock item %s %s field!";

    public StockItem(long id, String name, String desc, double price, int quantity) {
        if (id < 0) throw new SalesSystemException(String.format(NEGATIVE_ERROR_MESSAGE, id, name, "id"));
        if (price < 0) throw new SalesSystemException(String.format(NEGATIVE_ERROR_MESSAGE, price, name, "price"));
        if (quantity < 0)
            throw new SalesSystemException(String.format(NEGATIVE_ERROR_MESSAGE, quantity, name, "amount"));

        this.id = id;
        this.name = name;
        this.description = desc;
        this.price = price;
        this.quantity = quantity;
    }

    // don't remove!
    public StockItem() {}

    public static StockItem parseStockItem(String idStr, String name, String desc, String priceStr, String quantityStr) {
        String error = "bar code";
        try {
            long id = Long.parseLong(idStr);
            error = "price";
            double price = Double.parseDouble(priceStr);
            error = "amount";
            int quantity = Integer.parseInt(quantityStr);
            return new StockItem(id, name, desc, price, quantity);
        } catch (NumberFormatException e) {
            String[] value = e.getMessage().split("\"");
            if (value.length > 1) {
                throw new SalesSystemException(String.format("Unexpected value \"%s\" in stock item %s %s field!", value[1], name, error));
            } else {
                throw new SalesSystemException(String.format("Stock item %s %s field cannot be empty!", name, error));
            }
        }
    }

    private void setId(long id) { this.id = id;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public long getId() {
        return id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%.2f,%d",
                getId(),
                getName().replace(" ", "_"),
                getDescription().replace(" ", "_"),
                getPrice(),
                getQuantity());
    }
}
