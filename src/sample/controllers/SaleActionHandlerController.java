package sample.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;

public class SaleActionHandlerController {
    public ListView<Timestamp> LVListView;
    public Label LTitle;
    public Label LText1;
    public Label LText2;

    enum Keys {
        INCOME_EXPENSES,
        QUANTITY_AMOUNT,
        MONTHLY_PROFIT,
        EXPECTED_PROFIT
    }

    public SaleActionHandlerController() {
    }

    public void set2values(double num1, double num2, Keys key) {
        switch (key) {
            case INCOME_EXPENSES -> {
                String text1 = "Доход за указанный период: " + num1;
                String text2 = "Расходы за указанный период: " + num2;
                LVListView.setVisible(false);
                LText2.setVisible(true);
                LText1.setText(text1);
                LText2.setText(text2);
                LText1.setAlignment(Pos.CENTER);
            }
            case QUANTITY_AMOUNT -> {
                String text1 = "Количество проданных товаров: " + num1;
                String text2 = "Сумма продаж: " + num2;
                LVListView.setVisible(false);
                LText2.setVisible(true);
                LText1.setText(text1);
                LText2.setText(text2);
            }
        }
    }

    public void set1value(double num, Keys key) {
        switch (key) {
            case MONTHLY_PROFIT -> {
                String text1 = "Доход за последний месяц: " + num;
                LVListView.setVisible(false);
                LText2.setVisible(false);
                LText1.setText(text1);
            }
            case EXPECTED_PROFIT -> {
                String text1 = "Ожидаемый доход: " + String.format("%.2f", num);
                LVListView.setVisible(false);
                LText2.setVisible(false);
                LText1.setText(text1);
            }
        }
    }

    public void setList(ArrayList<Timestamp> list) {
        String text1 = "Даты одновременной покупки товаров: ";
        LVListView.setItems(FXCollections.observableArrayList(list));
        LVListView.setVisible(true);
        LText2.setVisible(false);
        LText1.setText(text1);
    }

    @FXML
    public void initialize() {

    }

}
