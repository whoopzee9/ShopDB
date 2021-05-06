package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import sample.handlers.ChargesHandler;
import sample.handlers.SalesHandler;
import sample.handlers.WarehouseHandler;
import sample.tables.Charge;
import sample.tables.ExpenseItem;
import sample.tables.Sale;
import sample.tables.Warehouse;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainScreenController implements PropertyChangeListener {
    public TabPane TPTabPane;

    public Tab TSales;
    public TableView<Sale> TVSalesTable;
    public TableColumn<Sale, String> TCSaleName;
    public TableColumn<Sale, Double> TCSaleAmount;
    public TableColumn<Sale, Double> TCSaleQuantity;
    public TableColumn<Sale, Timestamp> TCSaleDate;
    public Button BAddSale;
    public ComboBox<String> CBSalesAction;
    
    public Tab TWarehouse;
    public TableView<Warehouse> TVWarehouseTable;
    public TableColumn<Warehouse, String> TCWarehouseName;
    public TableColumn<Warehouse, Double> TCWarehouseAmount;
    public TableColumn<Warehouse, Double> TCWarehouseQuantity;
    public Button BAddWarehouseItem;
    public ComboBox<String> CBWarehouseAction;
    
    public Tab TCharges;

    public TableView<Charge> TVChargeTable;
    public TableColumn<Charge, String> TCChargeName;
    public TableColumn<Charge, Double> TCChargeAmount;
    public TableColumn<Charge, Timestamp> TCChargeDate;
    public Button BAddCharge;
    public ComboBox<String> CBChargeAction;
    public ComboBox<Warehouse> CBItem1;
    public ComboBox<Warehouse> CBItem2;
    public Label LLabel1;
    public Label LLabel2;
    public DatePicker DPDate1;
    public DatePicker DPDate2;
    public Label LLimit;
    public TextField TFLimit;
    public TableView<ExpenseItem> TVExpenseItemsTable;
    public TableColumn<ExpenseItem, String> TCExpenseItemName;

    private Connection con;
    private ChargesHandler chargesHandler;
    private SalesHandler salesHandler;
    private WarehouseHandler warehouseHandler;

    enum Tables {
        SALES,
        CHARGES,
        WAREHOUSE
    }

    public MainScreenController() {
    }

    @FXML
    public void initialize() {
        //Connection init-----------------------------
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String login = "c##test";
        String password = "bublik";
        try {
            con = DriverManager.getConnection(url, login, password);
            setConnection(con);
        } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Ошибка аутентификации");
            alert.setContentText("Логин или пароль неверны, либо нет подключения.");
            alert.showAndWait();
            throwables.printStackTrace();
        }

        //Tables init------------------------------------
        TCSaleDate.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        TCSaleAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TCSaleQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TCSaleName.setCellValueFactory(new PropertyValueFactory<>("name"));

        TCWarehouseName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TCWarehouseAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TCWarehouseQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TCChargeName.setCellValueFactory(new PropertyValueFactory<>("name"));
        TCChargeAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TCChargeDate.setCellValueFactory(new PropertyValueFactory<>("chargeDate"));

        TCExpenseItemName.setCellValueFactory(new PropertyValueFactory<>("name"));

        StringConverter<Double> doubleStringConverter = new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                if (object == null) {
                    return null;
                }
                return object.toString();
            }

            @Override
            public Double fromString(String string) {
                Double value;
                try {
                    value = Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    showAlert("Неправильный ввод!", "Неправильное число!");
                    return null;
                }
                return value;
            }
        };

        TCWarehouseName.setCellFactory(TextFieldTableCell.forTableColumn());
        TCWarehouseAmount.setCellFactory(param -> new TextFieldTableCell<>(doubleStringConverter));
        TCWarehouseQuantity.setCellFactory(param -> new TextFieldTableCell<>(doubleStringConverter));

        TCExpenseItemName.setCellFactory(TextFieldTableCell.forTableColumn());

        LLabel1.setVisible(false);
        LLabel2.setVisible(false);
        CBItem1.setVisible(false);
        CBItem2.setVisible(false);
        DPDate1.setVisible(false);
        DPDate2.setVisible(false);

        LLimit.setVisible(false);
        TFLimit.setVisible(false);

        //Sales combobox init--------------------------------
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("Выберите...");
        list.add("Кол-во товаров и средняя стоимость за всё время");
        list.add("Прибыль за последний месяц");
        list.add("5 самых доходных товаров");
        list.add("Средняя стоимость продаж");
        list.add("Одновременная продажа");
        list.add("Общая сумма дохода и расхода");
        list.add("Предполагаемая прибыль на месяц");
        CBSalesAction.setItems(list);
        CBSalesAction.getSelectionModel().select(0);


        //Warehouse combobox init-----------------------------
        ObservableList<String> list2 = FXCollections.observableArrayList();
        list2.add("Выберите...");
        list2.add("Товары, продавшиеся за месяц");
        CBWarehouseAction.setItems(list2);
        CBWarehouseAction.getSelectionModel().select(0);


        //Charges combobox init----------------------------------
        ObservableList<String> list3 = FXCollections.observableArrayList();
        list3.add("Выберите...");
        list3.add("Расходы за последний месяц");
        list3.add("Стоимость каждой статьи за последний год");
        list3.add("Расходы, превысившие порог");
        list3.add("Стоимость каждой статьи за последний месяц");
        CBChargeAction.setItems(list3);
        CBChargeAction.getSelectionModel().select(0);

        ObservableList<Sale> tmp = FXCollections.observableArrayList(salesHandler.getSales());
        TVSalesTable.setItems(tmp);

        TSales.setOnSelectionChanged(event -> {
            if (!TSales.isSelected()) {
                return;
            }
            ObservableList<Sale> sales = FXCollections.observableArrayList(salesHandler.getSales());
            ObservableList<Sale> old = TVSalesTable.getItems();
            if (!old.equals(sales)) {
                TVSalesTable.setItems(sales);
            }
        });

        TWarehouse.setOnSelectionChanged(event -> {
            if (!TWarehouse.isSelected()) {
                return;
            }
            ObservableList<Warehouse> warehouses = FXCollections.observableArrayList(warehouseHandler.getWarehouses());
            ObservableList<Warehouse> old = TVWarehouseTable.getItems();
            if (!old.equals(warehouses)) {
                TVWarehouseTable.setItems(warehouses);
            }
        });

        TCharges.setOnSelectionChanged(event -> {
            if (!TCharges.isSelected()) {
                return;
            }
            ObservableList<Charge> charges = FXCollections.observableArrayList(chargesHandler.getCharges());
            ObservableList<Charge> old = TVChargeTable.getItems();
            if (!old.equals(charges)) {
                TVChargeTable.setItems(charges);
            }

            ObservableList<ExpenseItem> list1 = FXCollections.observableArrayList(chargesHandler.getExpenseItems());
            ObservableList<ExpenseItem> old1 = TVExpenseItemsTable.getItems();
            if (!old1.equals(list1)) {
                TVExpenseItemsTable.setItems(list1);
            }
        });
    }

    public void setConnection(Connection con) {
        this.con = con;
        chargesHandler = new ChargesHandler(con);
        salesHandler = new SalesHandler(con);
        warehouseHandler = new WarehouseHandler(con);
    }

    //Sales tab impl----------------------------------

    public void onSalesActionClicked(ActionEvent event) {
        int actionIndex = CBSalesAction.getSelectionModel().getSelectedIndex();

        switch (actionIndex) {
            case 0 -> { //none
                ObservableList<Sale> list = FXCollections.observableArrayList(salesHandler.getSales());
                TVSalesTable.setItems(list);
                TCSaleAmount.setText("Цена");
            }
            case 1 -> { //Количество товаров и стоимость продаж за всё время
                ArrayList<Double> list = salesHandler.getSalesAmountAndSum();
                double quantity = list.get(0);
                double amount = list.get(1);

                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/sample/UI/saleActionHandlerScreen.fxml"));
                    Parent root = loader.load();

                    SaleActionHandlerController contr = loader.getController();
                    contr.set2values(quantity, amount, SaleActionHandlerController.Keys.QUANTITY_AMOUNT);

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Магазин");
                    stage.setScene(new Scene(root, 450, 350));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case 2 -> { //Прибыль за последний месяц
                double profit = salesHandler.getMonthlyProfit();
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/sample/UI/saleActionHandlerScreen.fxml"));
                    Parent root = loader.load();

                    SaleActionHandlerController contr = loader.getController();
                    contr.set1value(profit, SaleActionHandlerController.Keys.MONTHLY_PROFIT);

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Магазин");
                    stage.setScene(new Scene(root, 450, 350));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case 3 -> { //5 самых доходных товаров
                ArrayList<Sale> list = salesHandler.getTop5Profit();
                TVSalesTable.setItems(FXCollections.observableArrayList(list));
                TCSaleAmount.setText("Доход");
            }
            case 4 -> { //Средняя стоимость продаж
                ArrayList<Sale> list = salesHandler.getAverageAmounts();
                TVSalesTable.setItems(FXCollections.observableArrayList(list));
                TCSaleAmount.setText("Ср. стоимость");
            }
            case 5 -> { //Одновременная продажа
                Warehouse item1 = CBItem1.getValue();
                Warehouse item2 = CBItem2.getValue();
                if (item1 == null || item2 == null) {
                    showAlert("Неправильный ввод", "Выберите товары!");
                    return;
                }
                ArrayList<Timestamp> list = salesHandler.getSimultaneousSaleDates(item1.getName(), item2.getName());
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/sample/UI/saleActionHandlerScreen.fxml"));
                    Parent root = loader.load();

                    SaleActionHandlerController contr = loader.getController();
                    contr.setList(list);

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Магазин");
                    stage.setScene(new Scene(root, 450, 350));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case 6 -> { //Общая сумма дохода и расхода
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                java.util.Date parsedFrom;
                java.util.Date parsedTo;
                try {
                    parsedFrom = dateFormat.parse(DPDate1.getEditor().getText());
                    parsedTo = dateFormat.parse(DPDate2.getEditor().getText());
                } catch (ParseException e) {
                    showAlert("Неправильный ввод!", "Неправильная дата!");
                    return;
                }
                Timestamp from = new Timestamp(parsedFrom.getTime());
                Timestamp to = new Timestamp(parsedTo.getTime());

                ArrayList<Double> list = salesHandler.getIncomeAndExpenses(from, to);
                double income = list.get(0);
                double expenses = list.get(1);

                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/sample/UI/saleActionHandlerScreen.fxml"));
                    Parent root = loader.load();

                    SaleActionHandlerController contr = loader.getController();
                    contr.set2values(income, expenses, SaleActionHandlerController.Keys.INCOME_EXPENSES);

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Магазин");
                    stage.setScene(new Scene(root, 450, 350));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case 7 -> { //Предполагаемая прибыль на месяц
                double profit = salesHandler.getExpectedIncome();
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/sample/UI/saleActionHandlerScreen.fxml"));
                    Parent root = loader.load();

                    SaleActionHandlerController contr = loader.getController();
                    contr.set1value(profit, SaleActionHandlerController.Keys.EXPECTED_PROFIT);

                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Магазин");
                    stage.setScene(new Scene(root, 450, 350));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void onAddNewSaleClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/UI/saleAdditionScreen.fxml"));
            Parent root = loader.load();

            SaleAdditionController contr = loader.getController();
            contr.setConnection(con);
            contr.addListener(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Магазин");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CBSalesOnAction(ActionEvent event) {
        int actionIndex = CBSalesAction.getSelectionModel().getSelectedIndex();
        LLabel1.setVisible(false);
        LLabel2.setVisible(false);
        CBItem1.setVisible(false);
        CBItem2.setVisible(false);
        DPDate1.setVisible(false);
        DPDate2.setVisible(false);
        switch (actionIndex) {
            case 5 -> {
                LLabel1.setVisible(true);
                LLabel2.setVisible(true);
                CBItem1.setVisible(true);
                CBItem2.setVisible(true);
                LLabel1.setText("Товар 1");
                LLabel2.setText("Товар 2");
            }
            case 6 -> {
                LLabel1.setVisible(true);
                LLabel2.setVisible(true);
                DPDate1.setVisible(true);
                DPDate2.setVisible(true);
                LLabel1.setText("Начало:");
                LLabel2.setText("Конец: ");
            }
        }
    }

    public void CBItem1OnMouseClicked(MouseEvent event) {
        ObservableList<Warehouse> list = FXCollections.observableArrayList(warehouseHandler.getWarehouses());
        ObservableList<Warehouse> oldList = CBItem1.getItems();
        if (!list.equals(oldList)) {
            CBItem1.setItems(list);
        }
    }

    public void CBItem2OnMouseClicked(MouseEvent event) {
        ObservableList<Warehouse> list = FXCollections.observableArrayList(warehouseHandler.getWarehouses());
        ObservableList<Warehouse> oldList = CBItem2.getItems();
        if (!list.equals(oldList)) {
            CBItem2.setItems(list);
        }
    }

    //Warehouse tab impl-------------------------------------------------

    public void onWarehouseActionClicked(ActionEvent event) {
        int actionIndex = CBWarehouseAction.getSelectionModel().getSelectedIndex();
        switch (actionIndex) {
            case 0 -> {
                ObservableList<Warehouse> list = FXCollections.observableArrayList(warehouseHandler.getWarehouses());
                TVWarehouseTable.setItems(list);
            }
            case 1 -> {
                ObservableList<Warehouse> list = FXCollections.observableArrayList(warehouseHandler.getSoldItems());
                TVWarehouseTable.setItems(list);
            }
        }
    }

    public void onAddNewWarehouseClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/UI/warehouseAdditionScreen.fxml"));
            Parent root = loader.load();

            WarehouseAdditionController contr = loader.getController();
            contr.setConnection(con);
            contr.addListener(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Магазин");
            stage.setScene(new Scene(root, 480, 300));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CBWarehouseOnAction(ActionEvent event) {

    }

    public void onWarehouseNameEditCommit(TableColumn.CellEditEvent<Warehouse, String> event) {
        TablePosition<Warehouse, String> pos = event.getTablePosition();

        String name = event.getNewValue();
        int row = pos.getRow();
        Warehouse warehouse = event.getTableView().getItems().get(row);
        if (name == null) {
            event.getTableView().getItems().set(row, warehouse);
            return;
        }
        warehouse.setName(name);

        warehouseHandler.updateWarehouse(warehouse);
    }

    public void onWarehouseAmountEditCommit(TableColumn.CellEditEvent<Warehouse, Double> event) {
        TablePosition<Warehouse, Double> pos = event.getTablePosition();

        Double amount = event.getNewValue();
        int row = pos.getRow();
        Warehouse warehouse = event.getTableView().getItems().get(row);
        if (amount == null) {
            event.getTableView().getItems().set(row, warehouse);
            return;
        }
        warehouse.setAmount(amount);

        warehouseHandler.updateWarehouse(warehouse);
    }

    public void onWarehouseQuantityEditCommit(TableColumn.CellEditEvent<Warehouse, Double> event) {
        TablePosition<Warehouse, Double> pos = event.getTablePosition();

        Double quantity = event.getNewValue();
        int row = pos.getRow();
        Warehouse warehouse = event.getTableView().getItems().get(row);
        if (quantity == null) {
            event.getTableView().getItems().set(row, warehouse);
            return;
        }
        warehouse.setQuantity(quantity);

        warehouseHandler.updateWarehouse(warehouse);
    }



    //Charge tab impl-------------------------------------------------------

    public void onChargeActionClicked(ActionEvent event) {
        int actionIndex = CBChargeAction.getSelectionModel().getSelectedIndex();
        switch (actionIndex) {
            case 0 -> { //none
                ObservableList<Charge> list = FXCollections.observableArrayList(chargesHandler.getCharges());
                TVChargeTable.setItems(list);
            }
            case 1 -> { //Расходы за последний месяц
                ObservableList<Charge> list = FXCollections.observableArrayList(chargesHandler.getChargesForLastMonth());
                TVChargeTable.setItems(list);
            }
            case 2 -> { //Стоимость каждой статьи за последний год
                ObservableList<Charge> list = FXCollections.observableArrayList(chargesHandler.getChargesAmountForLastYear());
                TVChargeTable.setItems(list);
            }
            case 3 -> { //Расходы, превысившие порог
                String text = TFLimit.getText();
                if (text.isEmpty()) {
                    showAlert("Неправильный ввод", "Введите порог!");
                    return;
                }

                double limit;

                try {
                    limit = Double.parseDouble(text);
                } catch (NumberFormatException e) {
                    showAlert("Неправильный ввод", "Введено не число!");
                    return;
                }

                ObservableList<Charge> list = FXCollections.observableArrayList(chargesHandler.getChargesOverLimit(limit));
                TVChargeTable.setItems(list);
            }
            case 4 -> { //Стоимость каждой статьи за последний месяц
                ObservableList<Charge> list = FXCollections.observableArrayList(chargesHandler.getEveryChargeForLastMonth());
                TVChargeTable.setItems(list);
            }
        }
    }

    public void onAddNewChargeClicked(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/sample/UI/chargeAdditionScreen.fxml"));
            Parent root = loader.load();

            ChargeAdditionController contr = loader.getController();
            contr.setConnection(con);
            contr.addListener(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Магазин");
            stage.setScene(new Scene(root, 600, 400));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void CBChargeOnAction(ActionEvent event) {
        int actionIndex = CBChargeAction.getSelectionModel().getSelectedIndex();
        switch (actionIndex) {
            case 0, 1, 2, 4 -> {
                LLimit.setVisible(false);
                TFLimit.setVisible(false);
            }
            case 3 -> {
                LLimit.setVisible(true);
                TFLimit.setVisible(true);
            }
        }
    }

    public void onDeleteChargeClicked(ActionEvent event) {
        int index = TVChargeTable.getSelectionModel().getFocusedIndex();
        if (index == 0 && !TVChargeTable.getSelectionModel().isSelected(0)) {
            showAlert("Неправильный ввод!", "Ничего не выбрано!");
            return;
        }
        Charge charge = TVChargeTable.getItems().get(index);
        try {
            chargesHandler.deleteCharge(charge);
            TVChargeTable.getItems().remove(index);
        } catch (SQLException throwables) {
            showAlert("Ошибка удаления!", "Невозможно удалить!");
        }
    }

    public void onDeleteExpenseItemClicked(ActionEvent event) {
        int index = TVExpenseItemsTable.getSelectionModel().getFocusedIndex();
        System.out.println(index);
        if (index == 0 && !TVExpenseItemsTable.getSelectionModel().isSelected(0)) {
            showAlert("Неправильный ввод!", "Ничего не выбрано!");
            return;
        }
        ExpenseItem expenseItem = TVExpenseItemsTable.getItems().get(index);
        try {
            chargesHandler.deleteExpenseItem(expenseItem);
            TVExpenseItemsTable.getItems().remove(index);
        } catch (SQLException throwables) {
            showAlert("Ошибка удаления!", "Невозможно удалить!");
        }
    }

    public void onExpenseItemEditCommit(TableColumn.CellEditEvent<ExpenseItem, String> event) {
        TablePosition<ExpenseItem, String> pos = event.getTablePosition();

        String newValue = event.getNewValue();
        int row = pos.getRow();
        ExpenseItem expenseItem = event.getTableView().getItems().get(row);
        if (newValue == null) {
            event.getTableView().getItems().set(row, expenseItem);
            return;
        }
        expenseItem.setName(newValue);

        chargesHandler.updateExpenseItem(expenseItem);

        ObservableList<Charge> list = FXCollections.observableArrayList(chargesHandler.getCharges());
        TVChargeTable.setItems(list);
    }

    public void showAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String name = evt.getPropertyName();
        Tables value = Tables.valueOf(name);

        switch (value) {
            case SALES -> {
                ObservableList<Sale> list = FXCollections.observableArrayList(salesHandler.getSales());
                TVSalesTable.setItems(list);
                TCSaleAmount.setText("Цена");
            }
            case CHARGES -> {
                ObservableList<Charge> list = FXCollections.observableArrayList(chargesHandler.getCharges());
                TVChargeTable.setItems(list);
                ObservableList<ExpenseItem> list1 = FXCollections.observableArrayList(chargesHandler.getExpenseItems());
                TVExpenseItemsTable.setItems(list1);
            }
            case WAREHOUSE -> {
                ObservableList<Warehouse> list = FXCollections.observableArrayList(warehouseHandler.getWarehouses());
                TVWarehouseTable.setItems(list);
            }
        }
    }
}
