package salessupport;

import database.Costumer;
import database.Order;
import database.Product;
import database.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import static java.util.Collections.reverseOrder;

public class SalesSupport {
    private static final Scanner scanner = new Scanner(System.in);
    final Repository repository = new Repository();
    final ProductFilter size = (p, q) -> p.getSize().toString().equals(q);
    final ProductFilter color = (p, q) -> p.getColor().equals(q);
    final ProductFilter brand = (p, q) -> p.getBrand().equals(q);

    SalesSupport() throws IOException {
        System.out.println("Vilken rapport vill du ha ut?");
        System.out.println(" [1] Lista alla kunder filtrerat");
        System.out.println(" [2] Lista antal ordrar per kund");
        System.out.println(" [3] Lista ordervärde per kund");
        System.out.println(" [4] Lista ordervärde per ort");
        System.out.println(" [5] Lista de mest sålda produkterna");

        switch (scanner.nextLine()) {
            case "1" -> filteredCostumersReport();
            case "2" -> countCostumerOrdersReport();
            case "3" -> costumerMoneySpentReport();
            case "4" -> cityMoneySpentReport();
            default -> mostPopularProductsReport();
        }
    }

    public void mostPopularProductsReport() {
        repository.retrieveAllCostumers().stream().flatMap(c -> c.getOrders().stream())
                .flatMap(o -> o.getProducts().stream())
                .collect(Collectors.groupingBy(p -> p.getBrand() + " " + p.getModel())).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size())).entrySet().stream()
                .sorted(reverseOrder(Map.Entry.comparingByValue())).forEach(kvp -> {
                    System.out.println(kvp.getKey() + " | Antal sålda: " + kvp.getValue());
                });
    }

    public void cityMoneySpentReport() {
        repository.retrieveAllCostumers().stream().flatMap(costumer -> costumer.getOrders().stream()).
                collect(Collectors.groupingBy(Order::getCity)).forEach((city, orders) -> {
                    final int totalSpent = orders.stream().flatMap(order -> order.getProducts().stream())
                            .mapToInt(Product::getPrice).sum();
                    System.out.println(city + " | Totalt Beställningsvärde: " + totalSpent+":-");
                });
    }

    public void costumerMoneySpentReport() {
        System.out.println("Dessa kunder hittades i databasen:");
        repository.retrieveAllCostumers().forEach(c -> {
            final int totalSpent = c.getOrders().stream()
                    .flatMap(o -> o.getProducts().stream()).mapToInt(Product::getPrice).sum();
            System.out.println(" •" + c.getName() + " | Pengar spenderade: " + totalSpent + ":-");
        });
    }

    public void countCostumerOrdersReport() {
        System.out.println("Dessa kunder hittades i databasen:");
        repository.retrieveAllCostumers()
                .forEach(c -> System.out.println(" •" + c.getName() + " | Antal Ordrar: " +c.getOrders().size()));
    }

    public void filteredCostumersReport() {
        final ProductFilter filter;

        System.out.println("Vad vill du filtrera på?");
        System.out.println("[S]torlek");
        System.out.println("[F]ärg");
        System.out.println("[M]ärke");

        switch (scanner.nextLine().toUpperCase()) {
            case "S" -> filter = this.size;
            case "F" -> filter = this.color;
            default -> filter = this.brand;
        }

        System.out.println("Sökord:");
        final String query = scanner.nextLine();

        StringBuilder header = new StringBuilder("Dessa kunder har handlat skor");
        if (filter.equals(this.size)) {
            header.append(" i storlek");
        } else if (filter.equals(this.color)) {
            header.append(" med färgen");
        } else if (filter.equals(this.brand)) {
            header.append(" av märket");
        }
        System.out.println(header.append(' ').append(query).append(':'));

        final List<Costumer> filteredCostumers = filterCostumers(filter, query);
        filteredCostumers.forEach(c -> System.out.println(" •" + c.getName() + " | " + "Västerlånggatan 20"));
    }

    public List<Costumer> filterCostumers(ProductFilter filter, String query) {
        return repository.retrieveAllCostumers().stream()
                .filter(c -> c.getOrders().stream()
                .flatMap(o -> o.getProducts().stream())
                .anyMatch(p -> filter.find(p, query))).toList();
    }

    public static void main(String[] args) throws IOException {
        do {
            new SalesSupport();
            System.out.println("Vill du hämta fler rapporter? [J]a / [n]ej");
        } while (!scanner.nextLine().equalsIgnoreCase("n"));
    }
}
