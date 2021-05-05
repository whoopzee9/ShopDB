package sample.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import sample.handlers.ChargesHandler;
import sample.handlers.SalesHandler;
import sample.handlers.WarehouseHandler;
import sample.tables.Charge;
import sample.tables.Sale;
import sample.tables.Warehouse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MainScreenController {
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

    private Connection con;
    private ChargesHandler chargesHandler;
    private SalesHandler salesHandler;
    private WarehouseHandler warehouseHandler;



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
        list.clear();
        list.add("Выберите...");
        list.add("Товары, продавшиеся за месяц");
        CBWarehouseAction.setItems(list);
        CBWarehouseAction.getSelectionModel().select(0);


        //Charges combobox init----------------------------------
        list.clear();
        list.add("Выберите...");
        list.add("Расходы за последний месяц");
        list.add("Стоимость каждой статьи за последний год");
        list.add("Расходы, превысившие порог");
        list.add("Стоимость каждой статьи за последний месяц");
        CBChargeAction.setItems(list);
        CBChargeAction.getSelectionModel().select(0);


    }

    public void setConnection(Connection con) {
        this.con = con;
        chargesHandler = new ChargesHandler(con);
        salesHandler = new SalesHandler(con);
        warehouseHandler = new WarehouseHandler(con);
    }

    //Sales tab impl----------------------------------

    public void onSalesActionClicked(ActionEvent event) {
        int sortIndex = CBSalesAction.getSelectionModel().getSelectedIndex();

        switch (sortIndex) {
            case 0 -> { //none
                ObservableList<Sale> list = FXCollections.observableArrayList(salesHandler.getSales());
                TVSalesTable.setItems(list);
            }
            case 1 -> { //Товары и средняя стоимость за всё время

            }
            case 2 -> { //Прибыль за последний месяц

            }
            case 3 -> { //5 самых доходных товаров

            }
            case 4 -> { //Средняя стоимость продаж

            }
            case 5 -> { //Одновременная продажа

            }
            case 6 -> { //Общая сумма дохода и расхода

            }
            case 7 -> { //Предполагаемая прибыль на месяц

            }
            default -> {

            }
        }
    }


    public void onAddNewSaleClicked(ActionEvent event) {

    }

    public void CBSalesOnAction(ActionEvent event) {

    }

    //Warehouse tab impl-------------------------------------------------

    public void onWarehouseActionClicked(ActionEvent event) {
    }

    public void onAddNewWarehouseClicked(ActionEvent event) {
    }

    public void CBWarehouseOnAction(ActionEvent event) {
    }

    //Charge tab impl-------------------------------------------------------

    public void onChargeActionClicked(ActionEvent event) {
    }

    public void onAddNewChargeClicked(ActionEvent event) {
    }

    public void CBChargeOnAction(ActionEvent event) {
    }
}
