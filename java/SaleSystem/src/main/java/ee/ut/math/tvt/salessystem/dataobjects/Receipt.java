package ee.ut.math.tvt.salessystem.dataobjects;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "receipt")
public class Receipt {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "time")
    private LocalDateTime time;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "receipt_soldItem",
            joinColumns = {@JoinColumn(name = "receipt_id")},
            inverseJoinColumns = {@JoinColumn(name = "soldItem_id")}
    )
    private List<SoldItem> items;

    @Column(name = "total")
    private double total;

    public Receipt(List<SoldItem> items) {
        this.time = LocalDateTime.now();
        this.items = new ArrayList<>(items);
        this.total = items.stream().mapToDouble(o -> (o.getPrice() * o.getQuantity())).sum();
    }

    public Receipt() {}

    public LocalDateTime getTime() {
        return time;
    }

    public List<SoldItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double getTotal() {
        return total;
    }

    private void setId(long id) {
        this.id = id;
    }

    private void setTime(LocalDateTime time) { this.time = time;}


    private void setItems(List<SoldItem> items) { this.items = items; }

    private void setSum(double total) { this.total = total; }

    @Override
    public String toString() {
        return time.toString() + ", " + items.stream().mapToInt(SoldItem::getQuantity).sum() + " items, Total " + total + "Euro";
    }
}
