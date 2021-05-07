package sample.tables;

import java.sql.Timestamp;

public class Sale {
    private int id;
    private Double amount;
    private Double quantity;
    private Timestamp saleDate;
    private String name;

    public Sale(int id, Double amount, Double quantity, Timestamp saleDate, String name) {
        this.id = id;
        this.amount = amount;
        this.quantity = quantity;
        this.saleDate = saleDate;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Timestamp getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Timestamp saleDate) {
        this.saleDate = saleDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
