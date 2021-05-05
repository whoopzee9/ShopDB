package sample.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import sample.tables.Warehouse;

public class SaleAdditionController {
    public TextField TFAmount;
    public TextField TFQuantity;
    public DatePicker DPSaleDate;
    public ComboBox<Warehouse> CBWarehouse;
    public Spinner<Integer> DPSaleTimeHours;
    public Spinner<Integer> SpSaleTimeMins;

    public SaleAdditionController() {
    }

    @FXML
    public void initialize() {

    }

    public void onDoneClicked(ActionEvent event) {

    }
}
