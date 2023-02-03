package database;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Repository {
    Properties properties = new Properties();

    public Repository() throws IOException {
        properties.load(new FileInputStream("src/database.properties"));
    }

    public List<Costumer> retrieveAllCostumers() {
        List<Costumer> costumers = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        )) {

            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM costumer");
            while (result.next()) {
                Costumer costumer = new Costumer(result.getString("name"));
                costumer.setId(result.getInt("id"));
                costumer.setOrders(retrieveOrdersForCostumer(result.getInt("id")));
                costumers.add(costumer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return costumers;
    }

    public Costumer retrieveCostumer(String name, String password) throws SQLException, LoginException {
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        )) {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * from costumer WHERE name=? AND password = ?");
            statement.setString(1, name);
            statement.setString(2, password);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                Costumer costumer = new Costumer(result.getString("name"));
                costumer.setId(result.getInt("id"));
                costumer.setOrders(retrieveOrdersForCostumer(result.getInt("id")));
                return costumer;
            }
        }

        throw new LoginException("Felaktig Inloggning!");
    }

    public List<Order> retrieveOrdersForCostumer(int costumerId) {
        final List<Order> orders = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        )) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                 "select id, delivery_city from `order` where costumer_id = "+costumerId
            );

            while (result.next()) {
                Order order = new Order();
                order.setId(result.getInt("id"));
                order.setCity(result.getString("delivery_city"));
                order.setProducts(retrieveProductsForOrder(order.getId()));
                orders.add(order);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<ProductVariation> retrieveProductsForOrder(int orderId) {
        final List<ProductVariation> products = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        )) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                    "select product.*, product_variation.* from product_variation_in_order " +
                        "inner join product_variation on product_variation_in_order.product_variation_id = product_variation.id " +
                            "inner join product on product_variation.product_id = product.id " +
                            "where product_variation_in_order.order_id = "+orderId
            );

            while (result.next()) {
                ProductVariation product = new ProductVariation();
                product.setSize(result.getInt("product_variation.size"));
                product.setColor(result.getString("color"));
                product.setBrand(result.getString("brand"));
                product.setModel(result.getString("model"));
                product.setPrice(result.getInt("price"));
                products.add(product);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<ProductVariation> getProducts() {
        List<ProductVariation> products = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        )) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(
                    "select product.brand, product.model, product.price, product_variation.size, " +
                        " product_variation.id, product_variation.stock, product_variation.color from product" +
                        " inner join product_variation on product.id = product_variation.product_id");

            while (result.next()) {
                ProductVariation p = new ProductVariation();
                p.setId(result.getInt("id"));
                p.setBrand(result.getString("product.brand"));
                p.setModel(result.getString("product.model"));
                p.setPrice(result.getInt("product.price"));
                p.setStock(result.getInt("product_variation.stock"));
                p.setSize(result.getInt("product_variation.size"));
                p.setColor(result.getString("product_variation.color"));
                products.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return products;
    }

    public void addToCart(int id, Integer activeOrderId, int productId) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                properties.getProperty("url"),
                properties.getProperty("user"),
                properties.getProperty("password")
        )) {
            CallableStatement statement = connection.prepareCall("CALL AddToCart(?, ?, ?)");
            statement.setInt(1, id);
            statement.setInt(2, activeOrderId);
            statement.setInt(3, productId);
            statement.executeQuery();
        }
    }
}
