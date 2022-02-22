package ee.ut.math.tvt.salessystem.ui.controllers;

import com.fasterxml.classmate.GenericType;
import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.ShoppingCart;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static ee.ut.math.tvt.salessystem.ui.SalesSystemUI.showErrorDialog;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "Point-of-sale" in the menu). Consists of the purchase menu,
 * current purchase dialog and shopping cart table.
 */
public class PurchaseController implements Initializable {

    private static final Logger log = LogManager.getLogger(PurchaseController.class);

    private final SalesSystemDAO dao;
    private final ShoppingCart shoppingCart;
    private final ObservableList<StockItem> stock;

    @FXML
    private Button newPurchase;
    @FXML
    private Button submitPurchase;
    @FXML
    private Button cancelPurchase;
    @FXML
    private TextField barCodeField;
    @FXML
    private TextField quantityField;
    @FXML
    private ComboBox<StockItem> nameField;
    @FXML
    private TextField priceField;
    @FXML
    private Button addItemButton;
    @FXML
    private Button removeItemButton;
    @FXML
    private Label sumField;
    @FXML
    private TableView<SoldItem> purchaseTableView;
    @FXML
    private TableColumn<SoldItem, Long> stockId;


    public PurchaseController(SalesSystemDAO dao, ShoppingCart shoppingCart) {
        this.dao = dao;
        this.shoppingCart = shoppingCart;
        this.stock = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        purchaseTableView.setItems(FXCollections.observableList(shoppingCart.getAll()));

        final Callback<ListView<StockItem>, ListCell<StockItem>> cellFactory = (final ListView<StockItem> p) -> new ListCell<>() {
            @Override
            protected void updateItem(final StockItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.getName());
                } else {
                    setText(null);
                }
            }
        };
        nameField.setItems(stock);
        nameField.setCellFactory(cellFactory);
        nameField.setButtonCell(cellFactory.call(null));
        nameField.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> fillInputsBySelectedName());
        purchaseTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            removeItemButton.setDisable(newValue == null);
            if (newValue != null) {
                nameField.getSelectionModel().select(newValue.getStockItem());
            }
        });

        barCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                resetProductField();
            } else if (oldValue.compareTo(newValue) != 0) {
                fillInputsBySelectedBarcode();
            }
        });

        disableInputs();

        log.info("PurchaseController initialized");
    }

    /**
     * Event handler for the <code>new purchase</code> event.
     */
    @FXML
    protected void newPurchaseButtonClicked() {
        log.info("New sale process started");

        try {
            enableInputs();
            nameField.getItems().clear();
            stock.addAll(FXCollections.observableArrayList(dao.findStockItems()));
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>cancel purchase</code> event.
     */
    @FXML
    protected void cancelPurchaseButtonClicked() {
        log.info("Sale cancelled");

        try {
            shoppingCart.cancelCurrentPurchase();
            disableInputs();
            nameField.getItems().clear();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Event handler for the <code>submit purchase</code> event.
     */
    @FXML
    protected void submitPurchaseButtonClicked() {
        log.info("Sale complete");

        try {
            log.debug(() -> String.format("Contents of the current basket:%n%s", shoppingCart.getAll()));
            shoppingCart.submitCurrentPurchase();
            disableInputs();
            barCodeField.clear();
            purchaseTableView.refresh();
        } catch (SalesSystemException e) {
            log.error(e.getMessage(), e);
        }
    }

    // switch UI to the state that allows to proceed with the purchase
    private void enableInputs() {
        resetProductField();
        disableProductField(false);
        cancelPurchase.setDisable(false);
        newPurchase.setDisable(true);
    }

    // switch UI to the state that allows to initiate new purchase
    private void disableInputs() {
        resetProductField();
        cancelPurchase.setDisable(true);
        submitPurchase.setDisable(true);
        removeItemButton.setDisable(true);
        newPurchase.setDisable(false);
        disableProductField(true);
    }

    private void fillInputsBySelectedBarcode() {
        StockItem stockItem;
        try {
            stockItem = dao.findStockItem(Long.parseLong(barCodeField.getText()));
        } catch (NumberFormatException e) {
            resetProductField();
            return;
        }

        if (stockItem != null) {
            log.debug(() -> String.format("entered product barcode: %s", stockItem.getId()));

            nameField.getSelectionModel().select(stockItem);
            priceField.setText(String.valueOf(stockItem.getPrice()));
            addItemButton.setDisable(false);
        } else {
            resetProductField();
        }
    }

    private void fillInputsBySelectedName() {
        StockItem stockItem = nameField.getValue();

        if (stockItem != null) {
            log.debug(() -> String.format("selected product name: %s", stockItem.getName()));
            barCodeField.setText(Long.toString(stockItem.getId()));
            priceField.setText(String.valueOf(stockItem.getPrice()));
            addItemButton.setDisable(false);
        } else {
            resetProductField();
        }
    }

    /**
     * Add new item to the cart.
     */
    @FXML
    public void addItemEventHandler() {
        try {
            StockItem stockItem = nameField.getValue();
            if (stockItem != null) {
                shoppingCart.addItem(
                        SoldItem.parseSoldItem(
                                stockItem,
                                quantityField.getText()));

                submitPurchase.setDisable(false);
                reloadStockList();
                barCodeField.clear();

                //The list isn't selectable if this isn't included for some reason
                //TODO: Find out if this can be removed somehow
                purchaseTableView.setItems(FXCollections.observableList(shoppingCart.getAll()));

                purchaseTableView.refresh();
            }
        } catch (SalesSystemException e) {
            String errorMsg = e.getMessage();
            log.debug(String.format("%s%n %s%n", e, errorMsg));
            showErrorDialog(SalesSystemUI.ErrorType.INPUT, errorMsg);
        }
    }

    @FXML
    protected void removeItemEventHandler() {
        shoppingCart.removeItem(purchaseTableView.getSelectionModel().getSelectedItem());
        purchaseTableView.refresh();
        purchaseTableView.getSelectionModel().clearSelection();
        log.info("Item removed");
        reloadStockList();
        barCodeField.clear();
        submitPurchase.setDisable(shoppingCart.isEmpty());
    }


    /**
     * Sets whether or not the product component is enabled.
     */
    private void disableProductField(boolean disable) {
        this.barCodeField.setDisable(disable);
        this.quantityField.setDisable(disable);
        this.nameField.setDisable(disable);
        this.priceField.setDisable(disable);
    }

    /**
     * Reset dialog fields.
     */
    private void resetProductField() {
        quantityField.setText("");
        priceField.setText("");
        nameField.getSelectionModel().clearSelection();
        addItemButton.setDisable(true);
        reloadStockList();
    }

    private void reloadStockList() {
        sumField.setText(String.valueOf(shoppingCart.getSum()));
        stock.clear();
        stock.setAll(FXCollections.observableArrayList(dao.findStockItems()));
    }
}
