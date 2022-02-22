package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.StockItem;
import ee.ut.math.tvt.salessystem.logic.Warehouse;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

import static ee.ut.math.tvt.salessystem.ui.SalesSystemUI.showErrorDialog;

public class StockController implements Initializable {

    private static final Logger log = LogManager.getLogger(StockController.class);

    private final Warehouse warehouse;

    private final SalesSystemDAO dao;

    @FXML
    private TextField barCodeField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField descriptionField;
    @FXML
    private TextField priceField;
    @FXML
    private TextField quantityField;
    @FXML
    private Button addItemButton;
    @FXML
    private TableView<StockItem> warehouseTableView;

    public StockController(SalesSystemDAO dao) {
        this.dao = dao;
        warehouse = new Warehouse(dao);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refreshStockItems();
        addItemButton.setDisable(true);
        barCodeField.textProperty().addListener((observable, oldValue, newValue) -> checkBarCodeStr(newValue));
        warehouseTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != oldSelection) {
                selectStockItem(newSelection);
            }
        });

        log.info("StockController initialized");
    }

    @FXML
    public void refreshButtonClicked() {
        log.info("warehouse view refreshed");
        refreshStockItems();
    }

    private void refreshStockItems() {
        warehouseTableView.setItems(FXCollections.observableList(dao.findStockItems()));
        warehouseTableView.refresh();
    }

    @FXML
    public void addProductClicked() {
        try {
            warehouse.processStockItem(
                    StockItem.parseStockItem(barCodeField.getText(),
                            nameField.getText(),
                            descriptionField.getText(),
                            priceField.getText(),
                            quantityField.getText()));
            refreshStockItems();
        } catch (SalesSystemException e) {
            log.debug(e.getMessage(), e);
            showErrorDialog(SalesSystemUI.ErrorType.INPUT, e.getMessage());
        }
    }

    private void clearText() {
        nameField.clear();
        descriptionField.clear();
        priceField.clear();
        quantityField.clear();
        warehouseTableView.getSelectionModel().clearSelection();
    }

    private void selectStockItem(StockItem stockItem) {
        warehouseTableView.getSelectionModel().select(stockItem);
        if (stockItem != null) {
            barCodeField.setText(String.valueOf(stockItem.getId()));
            nameField.setText(stockItem.getName());
            descriptionField.setText(stockItem.getDescription());
            priceField.setText(String.valueOf(stockItem.getPrice()));
            quantityField.setText(String.valueOf(stockItem.getQuantity()));

            addItemButton.setText("Modify product");
        } else {
            clearText();
            addItemButton.setText("Add product");
        }
        addItemButton.setDisable(false);
    }

    public void checkBarCodeStr(String str) {
        if (str.isEmpty() || str.isBlank()) {
            clearText();
            addItemButton.setText("Enter product bar code");
            addItemButton.setDisable(true);
            return;
        }
        try {
            selectStockItem(dao.findStockItem(Long.parseLong(str)));
        } catch (NumberFormatException e) {
            log.debug(e.getMessage(), e);
            clearText();
            addItemButton.setText("Enter product bar code");
            addItemButton.setDisable(true);
        }
    }
}
