package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sample.handlers.ChargesHandler;
import sample.handlers.SalesHandler;
import sample.handlers.WarehouseHandler;
import sample.tables.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChargeAdditionController {
    public TextField TFAmount;
    public TextField TFExpenseItem;
    public DatePicker DPSaleDate;
    public ComboBox<ExpenseItem> CBExpenseItems;
    public Label LDate;
    public Spinner<Integer> SpChargeTimeHours;
    public Spinner<Integer> SpChargeTimeMins;
    public Label LTime;
    public Label LHour;
    public Label LMin;
    public CheckBox CBCurrent;
    public CheckBox CBNewExpense;
    public Label LNewExpense;

    private Connection con;
    private ChargesHandler chargesHandler;
    private PropertyChangeSupport support = new PropertyChangeSupport(this);

    public ChargeAdditionController() {
    }

    @FXML
    public void initialize() {
        CBExpenseItems.setOnMouseClicked(event -> {
            ObservableList<ExpenseItem> list = FXCollections.observableArrayList(chargesHandler.getExpenseItems());
            ObservableList<ExpenseItem> oldList = CBExpenseItems.getItems();

            if (!list.equals(oldList)) {
                CBExpenseItems.setItems(list);
            }
        });

        CBCurrent.setSelected(true);
        LDate.setVisible(false);
        LTime.setVisible(false);
        LHour.setVisible(false);
        LMin.setVisible(false);
        SpChargeTimeHours.setVisible(false);
        SpChargeTimeMins.setVisible(false);
        DPSaleDate.setVisible(false);

        TFExpenseItem.setVisible(false);

        CBCurrent.setOnAction(event -> {
            if (CBCurrent.isSelected()) {
                LDate.setVisible(false);
                LTime.setVisible(false);
                LHour.setVisible(false);
                LMin.setVisible(false);
                SpChargeTimeMins.setVisible(false);
                SpChargeTimeHours.setVisible(false);
                DPSaleDate.setVisible(false);
            } else {
                LDate.setVisible(true);
                LTime.setVisible(true);
                LHour.setVisible(true);
                LMin.setVisible(true);
                SpChargeTimeMins.setVisible(true);
                SpChargeTimeHours.setVisible(true);
                DPSaleDate.setVisible(true);
            }
        });

        CBNewExpense.setOnAction(event -> {
            if (CBNewExpense.isSelected()) {
                TFExpenseItem.setVisible(true);
                CBExpenseItems.setVisible(false);
            } else {
                TFExpenseItem.setVisible(false);
                CBExpenseItems.setVisible(true);
            }
        });

        SpChargeTimeHours.setValueFactory(new SpinnerValueFactory<Integer>() {
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
        SpChargeTimeHours.getValueFactory().setValue(0);
        SpChargeTimeHours.getEditor().setOnAction(event -> {
            ValueFactory fact = new ValueFactory(23);
            fact.onAction(event, SpChargeTimeHours);
        });

        SpChargeTimeMins.setValueFactory(new SpinnerValueFactory<Integer>() {
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
        SpChargeTimeMins.getValueFactory().setValue(0);
        SpChargeTimeMins.getEditor().setOnAction(event -> {
            ValueFactory fact = new ValueFactory(59);
            fact.onAction(event, SpChargeTimeMins);
        });
    }

    public void onDoneClicked(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Ошибка");
        alert.setHeaderText("Неправильный ввод");

        String amountText = TFAmount.getText();
        String dateText = DPSaleDate.getEditor().getText();
        String newExpense = TFExpenseItem.getText();
        ExpenseItem expenseItem = CBExpenseItems.getValue();
        int hours = SpChargeTimeHours.getValue();
        int mins = SpChargeTimeMins.getValue();

        if (amountText.isEmpty() || (!CBNewExpense.isSelected() && expenseItem == null) ||
                (CBNewExpense.isSelected() && newExpense.isEmpty()) || (!CBCurrent.isSelected() && dateText.isEmpty())) {
            alert.setContentText("Пропущены поля!");
            alert.showAndWait();
            return;
        }

        double amount = 0;
        try {
            amount = Double.parseDouble(amountText);
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

        String name;

        if (CBNewExpense.isSelected()) {
            name = newExpense;
            chargesHandler.addExpenseItem(name);
        } else {
            name = expenseItem.getName();
        }

        Charge charge = new Charge(0, name, amount, timestamp);

        chargesHandler.addCharge(charge);

        Stage stage = (Stage) TFAmount.getScene().getWindow();
        stage.close();

        support.firePropertyChange(MainScreenController.Tables.CHARGES.name(),1,0);
    }

    public void setConnection(Connection con) {
        this.con = con;
        chargesHandler = new ChargesHandler(con);
    }

    public void addListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }
}
