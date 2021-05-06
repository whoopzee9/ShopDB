package sample.tables;

import java.sql.Timestamp;

public class Charge {
    private int id;
    private String name;
    private Double amount;
    private Timestamp chargeDate;

    public Charge(int id, String name, Double amount, Timestamp chargeDate) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.chargeDate = chargeDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Timestamp getChargeDate() {
        return chargeDate;
    }

    public void setChargeDate(Timestamp chargeDate) {
        this.chargeDate = chargeDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        id = id;
    }
}
