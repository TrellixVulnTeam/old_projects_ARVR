package ee.ut.math.tvt.salessystem.dao;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dataobjects.Receipt;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class HibernateSalesSystemDAO implements SalesSystemDAO {

    private final EntityManagerFactory emf;
    private final EntityManager em;

    private static final Logger log = LogManager.getLogger(HibernateSalesSystemDAO.class);

    public HibernateSalesSystemDAO() {
        emf = Persistence.createEntityManagerFactory("pos");
        em = emf.createEntityManager();

        log.info("InMemorySalesSystemDAO started");
    }

    @Override
    public void close() {
        em.close();
        emf.close();
    }

    @Override
    public StockItem findStockItem(long id) {
        try {
            return em.find(StockItem.class, id);
        } catch (Exception e) {
            log.info(String.format("No such StockItem:%n%s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void saveSoldItem(SoldItem item) {
        em.merge(item);
    }

    @Override
    public void saveStockItem(StockItem stockItem) {
            em.merge(stockItem);
    }

    @Override
    public List<Receipt> getReceipts(LocalDate start, LocalDate end) {
        final TypedQuery<Receipt> query = em.createQuery("select c from Receipt as c where c.time >= :start and c.time <= :end", Receipt.class);
        query.setParameter("start", start.atTime(LocalTime.MIDNIGHT));
        query.setParameter("end", end.atTime(LocalTime.MAX));

        return query.getResultList();
    }

    @Override
    public List<Receipt> getReceipts(int n) {
        final TypedQuery<Receipt> query = em.createQuery("select c from Receipt c order by c.time desc", Receipt.class);
        query.setMaxResults(n);
        return query.getResultList();
    }

    @Override
    public List<Receipt> getReceipts() { return em.createQuery("select c from Receipt c", Receipt.class).getResultList(); }

    @Override
    public void createReceipt(List<SoldItem> items) {
        em.merge(new Receipt(items));
    }

    @Override
    public void beginTransaction() {
        em.getTransaction().begin();
    }

    @Override
    public void rollbackTransaction() {
        em.getTransaction().rollback();
    }

    @Override
    public void commitTransaction() {
        em.getTransaction().commit();
    }

    @Override
    public List<StockItem> findStockItems() { return em.createQuery("select c from StockItem c", StockItem.class).getResultList(); }
}
