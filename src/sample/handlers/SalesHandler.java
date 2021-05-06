package sample.handlers;

import javafx.scene.control.Alert;
import sample.tables.Sale;

import java.sql.*;
import java.util.ArrayList;

public class SalesHandler {
    private Connection con;

    public SalesHandler(Connection con) {
        this.con = con;
    }

    public ArrayList<Sale> getSales() {
        ArrayList<Sale> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT s.amount, s.quantity, s.sale_date, w.name FROM Sales s " +
                    "INNER JOIN Warehouses w ON w.id = s.warehouse_id ");

            while (resultSet.next()) {
                Double amount = resultSet.getDouble(1);
                Double quantity = resultSet.getDouble(2);
                Timestamp date = resultSet.getTimestamp(3);
                String name = resultSet.getString(4);
                list.add(new Sale(amount, quantity, date, name));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public ArrayList<Double> getSalesAmountAndSum() {
        ArrayList<Double> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT SUM(s.quantity) as quantity, SUM(s.amount) as total_amount FROM Sales s");

            resultSet.next();
            list.add(resultSet.getDouble(1));
            list.add(resultSet.getDouble(2));
        } catch (SQLException e) {
            showAlert();
            e.printStackTrace();
        }
        return list;
    }

    public double getMonthlyProfit() {
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT SUM((s.amount - w.amount) * s.quantity) as profit FROM Sales s\n" +
                    "    INNER JOIN Warehouses w ON w.id = s.warehouse_id\n" +
                    "    WHERE (EXTRACT(MONTH from s.sale_date) = EXTRACT(MONTH from sysdate) - 1) \n" +
                    "    AND (EXTRACT(YEAR from s.sale_date) = EXTRACT(YEAR from ADD_MONTHS(sysdate, -1)))\n");

            resultSet.next();
            return resultSet.getDouble(1);
        } catch (SQLException e) {
            showAlert();
            e.printStackTrace();
        }
        return 0;
    }

    public ArrayList<Sale> getTop5Profit() {
        ArrayList<Sale> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM (    \n" +
                    "SELECT w.name, SUM((s.amount - w.amount) * s.quantity) as profit FROM Sales s\n" +
                    "    INNER JOIN Warehouses w ON w.id = s.warehouse_id\n" +
                    "    GROUP BY w.name\n" +
                    "    ORDER BY profit DESC\n" +
                    ") WHERE ROWNUM <= 5");

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                Double amount = resultSet.getDouble(2);
                list.add(new Sale(amount, null, null, name));
            }
        } catch (SQLException e) {
            showAlert();
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Sale> getAverageAmounts() {
        ArrayList<Sale> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT w.name, AVG(s.amount) FROM Sales s\n" +
                    "        INNER JOIN Warehouses w ON w.id = s.warehouse_id\n" +
                    "        GROUP BY w.name");

            while (resultSet.next()) {
                String name = resultSet.getString(1);
                Double amount = resultSet.getDouble(2);
                list.add(new Sale(amount, null, null, name));
            }

        } catch (SQLException e) {
            showAlert();
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Timestamp> getSimultaneousSaleDates(String item1, String item2) {
        ArrayList<Timestamp> list = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT s.sale_date FROM (SELECT * FROM sales s \n" +
                    "            INNER JOIN Warehouses w ON w.id = s.warehouse_id\n" +
                    "            WHERE w.name = ?) item1Res\n" +
                    "        INNER JOIN Sales s ON s.sale_date = item1Res.sale_date\n" +
                    "        INNER JOIN warehouses w ON s.warehouse_id = w.id\n" +
                    "        WHERE w.name = ?");
            ps.setString(1, item1);
            ps.setString(2, item2);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                Timestamp date = resultSet.getTimestamp(1);
                list.add(date);
            }

        } catch (SQLException e) {
            showAlert();
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<Double> getIncomeAndExpenses(Timestamp date1, Timestamp date2) {
        ArrayList<Double> list = new ArrayList<>();
        try {
            CallableStatement cs = con.prepareCall("EXECUTE incomeAndExpenses(?, ?, ?, ?)");
            cs.setTimestamp(1, date1);
            cs.setTimestamp(2, date2);
            cs.registerOutParameter(3, Types.NUMERIC);
            cs.registerOutParameter(4, Types.NUMERIC);
            cs.execute();

            list.add(cs.getDouble(3));
            list.add(cs.getDouble(4));
            return list;
        } catch (SQLException e) {
            showAlert();
            e.printStackTrace();
        }
        return list;
    }

    public double getExpectedIncome() {
        try {
            CallableStatement cs = con.prepareCall("CALL estimatedProfit(?)");
            cs.registerOutParameter(1, Types.NUMERIC);
            cs.execute();

            return cs.getDouble(1);
        } catch (SQLException e) {
            showAlert();
            e.printStackTrace();
        }
        return 0;
    }

    public void addSale(Sale sale) {
        try {
            PreparedStatement ps = con.prepareStatement("CALL ADD_SALE(?, ?, ?, ?)");
            ps.setString(1, sale.getName());
            ps.setDouble(2, sale.getQuantity());
            ps.setDouble(3, sale.getAmount());
            ps.setTimestamp(4, sale.getSaleDate());

            ps.executeUpdate();
        } catch (SQLException e) {
            showAlert();
            e.printStackTrace();
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
