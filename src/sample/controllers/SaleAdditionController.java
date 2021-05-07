package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.handlers.SalesHandler;
import sample.handlers.WarehouseHandler;
import sample.tables.Sale;
import sample.tables.ValueFactory;
import sample.tables.Warehouse;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SaleAdditionController {
    public TextField TFAmount;
    public TextField TFQuantity;
    public DatePicker DPSaleDate;
    public ComboBox<Warehouse> CBWarehouse;
    public Spinner<Integer> SpSaleTimeHours;
    public Spinner<Integer> SpSaleTimeMins;
    public CheckBox CBCurrent;
    public Label LDate;
    public Label LTime;
    public Label LHour;
    public Label LMin;

    private SalesHandler salesHandler;
    private WarehouseHandler warehouseHandler;
    private Connection con;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public SaleAdditionController() {
    }

    @FXML
    public void initialize() {
        CBWarehouse.setOnMouseClicked(event -> {
            ObservableList<Warehouse> list = FXCollections.observableArrayList(warehouseHandler.getWarehouses());
            ObservableList<Warehouse> oldList = CBWarehouse.getItems();

            if (!list.equals(oldList)) {
                CBWarehouse.setItems(list);
            }
        });

        CBCurrent.setSelected(true);
        LDate.setVisible(false);
        LTime.setVisible(false);
        LHour.setVisible(false);
        LMin.setVisible(false);
        SpSaleTimeMins.setVisible(false);
        SpSaleTimeHours.setVisible(false);
        DPSaleDate.setVisible(false);

        CBCurrent.setOnAction(event -> {
            if (CBCurrent.isSelected()) {
                LDate.setVisible(false);
                LTime.setVisible(false);
                LHour.setVisible(false);
                LMin.setVisible(false);
                SpSaleTimeMins.setVisible(false);
                SpSaleTimeHours.setVisible(false);
                DPSaleDate.setVisible(false);
            } else {
                LDate.setVisible(true);
                LTime.setVisible(true);
                LHour.setVisible(true);
                LMin.setVisible(true);
                SpSaleTimeMins.setVisible(true);
                SpSaleTimeHours.setVisible(true);
                DPSaleDate.setVisible(true);
            }
        });

        SpSaleTimeHours.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                ValueFactory fact = new ValueFactory(23);
                this.setValue(fact.decrement(this.getValue(), steps));
            }

            @Override
            public void increment(int steps) {
                ValueFactory fact = new ValueFactory(23);
                this.setValue(fact.increment(this.getValue(), steps));
            }
        });
        SpSaleTimeHours.getValueFactory().setValue(0);
        SpSaleTimeHours.getEditor().setOnAction(event -> {
            ValueFactory fact = new ValueFactory(23);
            fact.onAction(event, SpSaleTimeHours);
        });

        SpSaleTimeMins.setValueFactory(new SpinnerValueFactory<Integer>() {
            @Override
            public void decrement(int steps) {
                ValueFactory fact = new ValueFactory(59);
                this.setValue(fact.decrement(this.getValue(), steps));
            }

            @Override
            public void increment(int steps) {
                ValueFactory fact = new ValueFactory(59);
                this.setValue(fact.increment(this.getValue(), steps));
            }
        });
        SpSaleTimeMins.getValueFactory().setValue(0);
        SpSaleTimeMins.getEditor().setOnAction(event -> {
            ValueFactory fact = new ValueFactory(59);
            fact.onAction(event, SpSaleTimeMins);
        });
    }

    public void onDoneClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Неправильный ввод");

        String amountText = TFAmount.getText();
        String quantityText = TFQuantity.getText();
        String dateText = DPSaleDate.getEditor().getText();
        Warehouse warehouse = CBWarehouse.getValue();
        int hours = SpSaleTimeHours.getValue();
        int mins = SpSaleTimeMins.getValue();

        if (amountText.isEmpty() || quantityText.isEmpty() || warehouse == null || (!CBCurrent.isSelected() && dateText.isEmpty())) {
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
        Timestamp timestamp;

        if (!CBCurrent.isSelected()) {
            DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
            Date saleDate;
            try {
                saleDate = format.parse(dateText);
            } catch (ParseException e) {
                e.printStackTrace();
                alert.setContentText("Wrong dates!");
                alert.showAndWait();
                return;
            }
            if (saleDate != null) {
                long time = saleDate.getTime();
                time += hours * 60 * 60 * 1000;
                time += mins * 60 * 1000;
                timestamp = new Timestamp(time);
            } else {
                alert.setContentText("Wrong dates!");
                alert.showAndWait();
                return;
            }
        } else {
            Date date = new Date(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.setTime( date );
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            timestamp = new Timestamp(cal.getTime().getTime());
        }

        Sale sale = new Sale(0, amount, quantity, timestamp, warehouse.getName());

        salesHandler.addSale(sale);

        Stage stage = (Stage) TFAmount.getScene().getWindow();
        stage.close();

        support.firePropertyChange(MainScreenController.Tables.SALES.name(),1,0);

    }

    public void setConnection(Connection con) {
        this.con = con;
        salesHandler = new SalesHandler(con);
        warehouseHandler = new WarehouseHandler(con);
    }

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
