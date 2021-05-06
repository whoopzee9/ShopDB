module ShopDB {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires java.base;

    opens sample;
    opens sample.tables;
    opens sample.UI;
    opens sample.controllers;

}