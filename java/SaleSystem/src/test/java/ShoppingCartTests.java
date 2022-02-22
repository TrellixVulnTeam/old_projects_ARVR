import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.HibernateSalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.InMemorySalesSystemDAO;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Receipt;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

public class ShoppingCartTests {
    private static final int DEFAULT_QUANTITY = 10;
    private static final long DEFAULT_ID_ITEM_ONE = 50L;
    private static final long DEFAULT_ID_ITEM_TWO=51L;
    private SalesSystemDAO dao;
    private ShoppingCart cart;
    private StockItem testStockItem;
    private StockItem testStockItem2;
    private StockItem testStockItem3;

    @Before
    public void setup() {
        dao = new InMemorySalesSystemDAO();
        Warehouse warehouse = new Warehouse(dao);
        cart = new ShoppingCart(dao);
        testStockItem = new StockItem(DEFAULT_ID_ITEM_ONE, "test item", "test desc", 1d, DEFAULT_QUANTITY);
        testStockItem2= new StockItem(DEFAULT_ID_ITEM_TWO,"test item2", "test desc 2", 2d,DEFAULT_QUANTITY);
        warehouse.processStockItem(testStockItem);
    }

    @Test
    public void testSubmittingPurchaseDecreasesStockQuantity() {
        int initialQuantity = testStockItem.getQuantity();
        cart.addItem(new SoldItem(testStockItem, 1));
        cart.submitCurrentPurchase();
        assertEquals(initialQuantity - 1, dao.findStockItem(DEFAULT_ID_ITEM_ONE).getQuantity());
    }

    @Test
    public void testCancelPurchaseDoesNotDecreaseStockQuantity() {
        int initialQuantity = testStockItem.getQuantity();
        cart.addItem(new SoldItem(testStockItem, 1));
        cart.cancelCurrentPurchase();
        assertTrue(cart.isEmpty());
        assertEquals(initialQuantity, dao.findStockItem(DEFAULT_ID_ITEM_ONE).getQuantity());
    }

    @Test
    public void testAddingProductWithSuitableQuantity() {
        assertNull(cart.getSoldItemByStock(testStockItem));
        cart.addItem(new SoldItem(testStockItem, 1));
        assertNotNull(cart.getSoldItemByStock(testStockItem));
    }

    @Test
    public void testAddingProductWithExcessiveQuantity() {
        assertNull(cart.getSoldItemByStock(testStockItem));
        SoldItem item = new SoldItem(testStockItem, testStockItem.getQuantity() + 10);
        Exception e = assertThrows(SalesSystemException.class, () -> {
            cart.addItem(item);
        });
        assertTrue(e.getMessage().contains("pcs of"));
    }


    @Test
    public void testRemovingProductByID() {
        cart.addItem(new SoldItem(testStockItem, 1));
        assertNotNull(cart.getSoldItemByStock(testStockItem));
        assertTrue(cart.removeItem(DEFAULT_ID_ITEM_ONE));
        assertNull(cart.getSoldItemByStock(testStockItem));
    }

    @Test
    public void testRemovingProductBySoldItem() {
        SoldItem item = new SoldItem(testStockItem, 1);
        cart.addItem(item);
        assertNotNull(cart.getSoldItemByStock(testStockItem));
        assertTrue(cart.removeItem(item));
        assertNull(cart.getSoldItemByStock(testStockItem));
    }

    @Test
    public void testCartSumIncreasesWhenAddingProducts() {
        assertEquals(0.0, cart.getSum(), 0.00001);
        cart.addItem(new SoldItem(testStockItem, 1));
        assertEquals(testStockItem.getPrice(), cart.getSum(), 0.00001);
        cart.addItem(new SoldItem(testStockItem, 1));
        assertEquals(testStockItem.getPrice() * 2, cart.getSum(), 0.00001);
        cart.addItem(new SoldItem(new StockItem(51, "", "", 5d, 10), 1));
        assertEquals(testStockItem.getPrice() * 2 + 5, cart.getSum(), 0.00001);
    }
    @Test
    public void testAddingExistingItem(){
        StockItem test = dao.findStockItem(testStockItem.getId());
        int testQ = test.getQuantity();
        test.setQuantity(testQ+1);
        Assert.assertEquals(11, dao.findStockItem(test.getId()).getQuantity());
        //testStockItem.setQuantity(DEFAULT_QUANTITY+1);
        //Assert.assertEquals(11,testStockItem.getQuantity());
    }
    @Test
    public void testAddingNewItem(){
        testStockItem3 = new StockItem(52L,"test item 3", "test desc3",10.0,DEFAULT_QUANTITY);
        dao.saveStockItem(testStockItem3);
        Assert.assertEquals(dao.findStockItem(52L),testStockItem3);
    }
    @Test
    public void testAddingItemWithNegativeQuantity(){
        Exception e = assertThrows(SalesSystemException.class, () -> {
            cart.addItem(SoldItem.parseSoldItem(testStockItem,"-1"));
        });

        assertTrue(e.getMessage().toLowerCase().contains("negative"));
        assertTrue(e.getMessage().toLowerCase().contains("quantity"));
    }
    @Test
    public void testAddingItemWithQuantityTooLarge(){
        Exception e = assertThrows(SalesSystemException.class, () -> {
            cart.addItem(SoldItem.parseSoldItem(testStockItem,"11"));
        });
        assertTrue(e.getMessage().toLowerCase().contains("cannot add"));
    }
    @Test
    public void testAddingItemWithQuantitySumTooLarge(){
        cart.addItem(SoldItem.parseSoldItem(testStockItem,"10"));
        Exception e = assertThrows(SalesSystemException.class, () -> {
            cart.addItem(SoldItem.parseSoldItem(testStockItem,"10"));
        });
        assertTrue(e.getMessage().toLowerCase().contains("cannot add"));
    }
    @Test
    public void testSubmittingCurrentPurchaseDecreasesStockQuantity(){
        cart.addItem(new SoldItem(testStockItem, 1));
        cart.submitCurrentPurchase();
        Assert.assertEquals(9,dao.findStockItem(DEFAULT_ID_ITEM_ONE).getQuantity());
    }
    @Test
    public void testSubmittingCurrentPurchaseBeginsAndCommitsTransaction(){
        InMemorySalesSystemDAO mockDao = mock(InMemorySalesSystemDAO.class);
        InOrder inOrder = Mockito.inOrder(mockDao);
        ShoppingCart mockCart = new ShoppingCart(mockDao);
        mockCart.addItem(new SoldItem(testStockItem,1));
        mockCart.submitCurrentPurchase();
        inOrder.verify(mockDao,times(1)).beginTransaction();
        inOrder.verify(mockDao,times(1)).commitTransaction();

    }
    @Test
    public void testSubmittingCurrentOrderCreatesHistoryItem(){
        cart.addItem(new SoldItem(testStockItem,1));
        cart.submitCurrentPurchase();
        Assert.assertEquals(1,dao.getReceipts().size());
        cart.addItem(new SoldItem(testStockItem2,1));
        cart.addItem(new SoldItem(testStockItem,1));
        cart.submitCurrentPurchase();
        Assert.assertEquals(2,dao.getReceipts().size());
    }
    @Test
    public void testSubmittingCurrentOrderSavesCorrectTime(){
        cart.addItem(new SoldItem(testStockItem,1));
        cart.submitCurrentPurchase();
        LocalDateTime now = LocalDateTime.now();
        Assert.assertEquals(now.getHour(),dao.getReceipts().get(0).getTime().getHour());
        Assert.assertEquals(now.getMinute(),dao.getReceipts().get(0).getTime().getMinute());
        Assert.assertEquals(now.getSecond(),dao.getReceipts().get(0).getTime().getSecond());
    }
    @Test
    public void testCancellingOrder(){
        cart.addItem(new SoldItem(testStockItem,1));
        cart.addItem(new SoldItem(testStockItem2,2));
        cart.cancelCurrentPurchase();
        cart.addItem(new SoldItem(testStockItem,7));
        cart.addItem(new SoldItem(testStockItem2,4));
        cart.submitCurrentPurchase();
        var item1 = dao.findStockItem(50L);
        var item2 = dao.findStockItem(51L);
        Assert.assertEquals(DEFAULT_QUANTITY-7,item1.getQuantity());
        Assert.assertEquals(DEFAULT_QUANTITY-4,item2.getQuantity());
    }
    @Test
    public void testCancellingOrderQuanititesUnchanged(){
        int testStockItem1Quantity = testStockItem.getQuantity();
        int testStockItem2Quantity = testStockItem2.getQuantity();
        cart.addItem(new SoldItem(testStockItem,1));
        cart.addItem(new SoldItem(testStockItem2,1));
        cart.cancelCurrentPurchase();
        Assert.assertEquals(testStockItem1Quantity,testStockItem.getQuantity());
        Assert.assertEquals(testStockItem2Quantity,testStockItem2.getQuantity());
    }

}
