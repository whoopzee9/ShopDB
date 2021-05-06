package sample.handlers;

import javafx.scene.control.Alert;
import sample.tables.Charge;
import sample.tables.ExpenseItem;
import sample.tables.Sale;

import java.sql.*;
import java.util.ArrayList;

public class ChargesHandler {
    private Connection con;

    public ChargesHandler(Connection con) {
        this.con = con;
    }

    public ArrayList<Charge> getCharges() {
        ArrayList<Charge> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT c.ID, EI.NAME, c.AMOUNT, c.CHARGE_DATE FROM Charges c " +
                    "INNER JOIN EXPENSE_ITEMS EI on c.EXPENSE_ITEM_ID = EI.ID");

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Double amount = resultSet.getDouble(3);
                Timestamp date = resultSet.getTimestamp(4);
                list.add(new Charge(id, name, amount, date));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public ArrayList<Charge> getChargesForLastMonth() {
        ArrayList<Charge> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT c.id, e.name, c.amount, c.charge_date FROM Charges c\n" +
                    "    INNER JOIN Expense_items e ON e.id = c.expense_item_id\n" +
                    "    WHERE (EXTRACT(MONTH from c.charge_date) = EXTRACT(MONTH from sysdate) - 1) \n" +
                    "    AND (EXTRACT(YEAR from c.charge_date) = EXTRACT(YEAR from ADD_MONTHS(sysdate, -1)))");

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Double amount = resultSet.getDouble(3);
                Timestamp date = resultSet.getTimestamp(4);
                list.add(new Charge(id, name, amount, date));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public ArrayList<Charge> getChargesAmountForLastYear() {
        ArrayList<Charge> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT e.id, e.name, \n" +
                    "    SUM(CASE WHEN (c.amount IS NULL) OR (EXTRACT(YEAR from c.charge_date) != EXTRACT(YEAR from sysdate) - 1) THEN 0 ELSE c.amount end) as profit \n" +
                    "    FROM Expense_items e\n" +
                    "    LEFT JOIN Charges c ON e.id = c.expense_item_id\n" +
                    "    GROUP BY e.name, e.id\n" +
                    "    ORDER BY profit");

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Double total = resultSet.getDouble(3);
                list.add(new Charge(id, name, total, null));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public ArrayList<Charge> getChargesOverLimit(double limit) {
        ArrayList<Charge> list = new ArrayList<>();
        try {
            PreparedStatement ps = con.prepareStatement("SELECT e.id, e.name, SUM(c.amount) as total FROM Charges c\n" +
                    "    INNER JOIN expense_items e ON e.id = c.expense_item_id\n" +
                    "    GROUP BY e.name, e.id\n" +
                    "    HAVING (SUM(c.amount) > ?)");
            ps.setDouble(1, limit);

            ResultSet resultSet = ps.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Double total = resultSet.getDouble(3);
                list.add(new Charge(id, name, total, null));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public ArrayList<Charge> getEveryChargeForLastMonth() {
        ArrayList<Charge> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT e.id, e.name, \n" +
                    "    SUM(CASE WHEN (c.amount IS NULL) OR  ((EXTRACT(MONTH from c.charge_date) != EXTRACT(MONTH from sysdate) - 1) \n" +
                    "    OR (EXTRACT(YEAR from c.charge_date) != EXTRACT(YEAR from ADD_MONTHS(sysdate, -1)))) THEN 0 ELSE c.amount end) as total \n" +
                    "    FROM Charges c\n" +
                    "    RIGHT JOIN expense_items e ON e.id = c.expense_item_id\n" +
                    "    GROUP BY e.name, e.id");

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                Double total = resultSet.getDouble(3);
                list.add(new Charge(id, name, total, null));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public void addCharge(Charge charge) {
        try {
            PreparedStatement ps = con.prepareStatement("CALL add_expense_charge(?, ?, ?)");
            ps.setDouble(1, charge.getAmount());
            ps.setTimestamp(2, charge.getChargeDate());
            ps.setString(3, charge.getName());

            ps.executeUpdate();
        } catch (SQLException throwables) {
            showAlert();
        }
    }

    public void addExpenseItem(String name) {
        try {
            PreparedStatement ps = con.prepareStatement("CALL add_expense_item(?)");
            ps.setString(1, name);

            ps.executeUpdate();
        } catch (SQLException throwables) {
            showAlert();
        }
    }

    public ArrayList<ExpenseItem> getExpenseItems() {
        ArrayList<ExpenseItem> list = new ArrayList<>();
        try {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("SELECT e.id, e.name FROM EXPENSE_ITEMS e ");

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString(2);
                list.add(new ExpenseItem(id, name));
            }
        } catch (SQLException e) {
            showAlert();
        }
        return list;
    }

    public void deleteCharge(Charge charge) throws SQLException {
        PreparedStatement ps = con.prepareStatement("DELETE FROM CHARGES WHERE id = ?");
        ps.setInt(1, charge.getId());
        ps.executeUpdate();
    }

    public void deleteExpenseItem(ExpenseItem expenseItem) throws SQLException {
        PreparedStatement ps = con.prepareStatement("CALL DELETE_EXPENSE(?)");
        ps.setString(1, expenseItem.getName());
        ps.executeUpdate();
    }

    private void showAlert() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText("Ошибка");
        alert.setContentText("Невозможно выполнить операцию");
        alert.showAndWait();
    }
}
