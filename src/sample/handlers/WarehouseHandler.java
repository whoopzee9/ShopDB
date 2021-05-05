package sample.handlers;

import javafx.scene.control.Alert;
import sample.tables.Sale;
import sample.tables.Warehouse;

import java.sql.*;
import java.util.ArrayList;

public class WarehouseHandler {
    private Connection con;

    public WarehouseHandler(Connection con) {
        this.con = con;
    }

    public ArrayList<Warehouse> getWarehouses() {
        ArrayList<Warehouse> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT w.name, w.QUANTITY, w.AMOUNT FROM Warehouses w");

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                Double quantity = resultSet.getDouble(2);
                Double amount = resultSet.getDouble(3);
                list.add(new Warehouse(name, quantity, amount));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public ArrayList<Warehouse> getSoldItems() {
        ArrayList<Warehouse> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT DISTINCT w.name, w.QUANTITY, w.AMOUNT FROM Sales s\n" +
                    "    INNER JOIN Warehouses w ON s.warehouse_id = w.id\n" +
                    "    WHERE (EXTRACT(MONTH from s.sale_date) = EXTRACT(MONTH from sysdate) - 1) \n" +
                    "    AND (EXTRACT(YEAR from s.sale_date) = EXTRACT(YEAR from ADD_MONTHS(sysdate, -1)));");

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                Double quantity = resultSet.getDouble(2);
                Double amount = resultSet.getDouble(3);
                list.add(new Warehouse(name, quantity, amount));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public void addWarehouse(Warehouse warehouse) {
        try {
            //TODO вынести в процедуру
            PreparedStatement ps = con.prepareStatement("INSERT INTO Warehouses(Name, Quantity, Amount) VALUES(?, ?, ?); COMMIT;");
            ps.setString(1, warehouse.getName());
            ps.setDouble(2, warehouse.getQuantity());
            ps.setDouble(3, warehouse.getAmount());

            ps.executeUpdate();
        } catch (SQLException throwables) {
            showAlert();
        }
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Ошибка");
        alert.setContentText("Невозможно выполнить операцию");
        alert.showAndWait();
    }
}
