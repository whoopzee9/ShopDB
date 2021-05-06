package sample.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sample.handlers.WarehouseHandler;
import sample.tables.Warehouse;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;

public class WarehouseAdditionController {

    public TextField TFAmount;
    public TextField TFQuantity;
    public TextField TFName;

    private WarehouseHandler warehouseHandler;
    private Connection con;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public WarehouseAdditionController() {
    }

    public void onDoneClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Неправильный ввод");

        String amountText = TFAmount.getText();
        String quantityText = TFQuantity.getText();
        String name = TFName.getText();

        if (amountText.isEmpty() || quantityText.isEmpty() || name.isEmpty()) {
            alert.setContentText("Пропущены поля!");
            alert.showAndWait();
            return;
        }

        double amount = 0;
        double quantity = 0;
        try {
            amount = Double.parseDouble(amountText);
            quantity = Double.parseDouble(quantityText);
        } catch (NumberFormatException e) {
            alert.setContentText("Неправильно введены значения!");
            alert.showAndWait();
            return;
        }

        Warehouse warehouse = new Warehouse(name, quantity, amount);
        warehouseHandler.addWarehouse(warehouse);

        Stage stage = (Stage) TFAmount.getScene().getWindow();
        stage.close();

        support.firePropertyChange(MainScreenController.Tables.WAREHOUSE.name(),1,0);
    }

    public void setConnection(Connection con) {
        this.con = con;
        warehouseHandler = new WarehouseHandler(con);
    }

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
