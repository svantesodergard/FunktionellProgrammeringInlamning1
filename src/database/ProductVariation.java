package database;

public class ProductVariation extends Product {
    int id;
    Integer size;
    int stock;

    public void setId(int id) {
        this.id = id;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public void setColor(String color) {
        this.color = color;
    }

    String color;
}