package sample.tables;

public class Warehouse {
    private String name;
    private Double quantity;
    private Double amount;

    public Warehouse(String name, Double quantity, Double amount) {
        this.name = name;
        this.quantity = quantity;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return name;
    }
}
