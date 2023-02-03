package database;

import salessupport.ProductFilter;

import java.util.List;

public class Costumer {
    int id;
    String name;
    List<Order> orders;

    public Integer getActiveOrderId() {
        if (this.orders.size() == 0) {
            return null;
        }
        return orders.get(orders.size() - 1).getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public Costumer(String name) {
        this.name = name;
    }
}