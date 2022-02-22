import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class WarehouseInMemoryTests {
    private SalesSystemDAO dao;
    private Warehouse warehouse;
    private StockItem testStockItem;

    @Before
    public void setup() {
        dao = new InMemorySalesSystemDAO();
        warehouse = new Warehouse(dao);
        testStockItem = new StockItem(50L, "test item", "test desc", 1d, 10);
    }

    @Test
    public void testAddingNewItem() {
        long id = testStockItem.getId();

        while (dao.findStockItem(id) != null) id++;
        assertNull(dao.findStockItem(id));

        StockItem item = new StockItem(
                id,
                testStockItem.getName(),
                testStockItem.getDescription(),
                testStockItem.getPrice(),
                testStockItem.getQuantity());
        warehouse.processStockItem(item);
        assertNotNull(dao.findStockItem(id));
    }

    @Test
    public void testFindStockItemWithInvalidBarCode() {
        assertNull(dao.findStockItem(Integer.MIN_VALUE));
    }

    @Test
    public void testAddingExsistingItem() {
        warehouse.processStockItem(testStockItem);
        assertEquals(testStockItem.getQuantity(), dao.findStockItem(testStockItem.getId()).getQuantity());

        SalesSystemDAO spy = spy(dao);
        Warehouse spyWarehouse = new Warehouse(spy);
        int increasedQuantity = testStockItem.getQuantity() + 1;
        StockItem copy = new StockItem(testStockItem.getId(), testStockItem.getName(), testStockItem.getDescription(), testStockItem.getPrice(), increasedQuantity);
        spyWarehouse.processStockItem(copy);
        assertEquals(increasedQuantity, spy.findStockItem(testStockItem.getId()).getQuantity());
        verify(spy, never()).saveStockItem(copy);
    }

    @Test
    public void testAddingItemWithNegativeQuantity() {
        Exception e = assertThrows(SalesSystemException.class, () -> {
            StockItem.parseStockItem("50", "test item", "test desc", "1", "-1");
        });

        assertTrue(e.getMessage().toLowerCase().contains("negative"));
        assertTrue(e.getMessage().toLowerCase().contains("amount"));
    }

    @Test
    public void testAddingItemBeginsAndCommitsTransaction() {
        SalesSystemDAO mockDao = mock(SalesSystemDAO.class);
        InOrder inOrder = Mockito.inOrder(mockDao);

        Warehouse mockWarehouse = new Warehouse(mockDao);
        mockWarehouse.processStockItem(testStockItem);

        inOrder.verify(mockDao, times(1)).beginTransaction();
        inOrder.verify(mockDao, times(1)).commitTransaction();
    }
}
