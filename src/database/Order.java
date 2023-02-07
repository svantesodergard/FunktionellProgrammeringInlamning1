package database;

import salessupport.ProductFilter;

import java.util.List;

public class Order {
    private int id;
    private List<ProductVariation> products;
    private String city;


    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setProducts(List<ProductVariation> products) {
        this.products = products;
    }

    public int getId() {
        return id;
    }

    public List<ProductVariation> getProducts() {
        return products;
    }
}
