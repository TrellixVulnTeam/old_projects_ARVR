package ee.ut.math.tvt.salessystem.ui.controllers;

import ee.ut.math.tvt.salessystem.SalesSystemException;
import ee.ut.math.tvt.salessystem.dao.SalesSystemDAO;
import ee.ut.math.tvt.salessystem.dataobjects.Receipt;
import ee.ut.math.tvt.salessystem.dataobjects.SoldItem;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ResourceBundle;

import static ee.ut.math.tvt.salessystem.ui.SalesSystemUI.showErrorDialog;

/**
 * Encapsulates everything that has to do with the purchase tab (the tab
 * labelled "History" in the menu).
 */
public class HistoryController implements Initializable {

    private static final Logger log = LogManager.getLogger(HistoryController.class);

    private final SalesSystemDAO dao;

    @FXML
    private Button showDates;

    @FXML
    private DatePicker startDate;

    private boolean startIsEmpty = true;

    private boolean endIsEmpty = true;

    @FXML
    private DatePicker endDate;

    @FXML
    private TableView<Receipt> receiptView;

    @FXML
    private TableView<SoldItem> itemView;

    public HistoryController(SalesSystemDAO dao) {
        this.dao = dao;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showDates.setDisable(true);
        receiptView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                setItems(newValue);
            }
        });
        startDate.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                startDate.setValue(startDate.getConverter().fromString(newValue));
                startIsEmpty = false;
            } catch (DateTimeParseException ignored) {
                startIsEmpty = true;
                showDates.setDisable(true);
            }
            showDates.setDisable(startIsEmpty || endIsEmpty);
        });
        endDate.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                endDate.setValue(endDate.getConverter().fromString(newValue));
                endIsEmpty = false;
            } catch (DateTimeParseException ignored) {
                endIsEmpty = true;
                showDates.setDisable(true);
            }
            showDates.setDisable(startIsEmpty || endIsEmpty);
        });

        log.info("HistoryController initialized");
    }

    @FXML
    private void showDatesButtonClicked() {
        try {
            receiptView.setItems(FXCollections.observableList(dao.getReceipts(startDate.getValue(), endDate.getValue())));
            receiptView.refresh();
            log.info(()->"Retreived receipts between " + startDate.toString() + " and " + endDate.toString());
        } catch (SalesSystemException e) {
            showErrorDialog(SalesSystemUI.ErrorType.INPUT, e.getMessage());
        } catch (NullPointerException ignored) {
            showErrorDialog(SalesSystemUI.ErrorType.INPUT, "Date fields cannot be empty!");
        }
    }

    @FXML
    private void showAllClicked() {
        receiptView.setItems(FXCollections.observableList(dao.getReceipts()));
            log.info(()->"Retreived all receipts");
    }

    @FXML
    private void showLastClicked() {
        receiptView.setItems(FXCollections.observableList(dao.getReceipts(10)));
            log.info(()->"Retreived last 10 receipts");
    }

    private void setItems(Receipt receipt) {
        itemView.setItems(FXCollections.observableList(receipt.getItems()));
        itemView.refresh();
    }
}
