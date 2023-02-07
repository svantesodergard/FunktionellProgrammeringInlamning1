package addtocart;

import database.Costumer;
import database.ProductVariation;
import database.Repository;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class AddToCart {
    final Repository repository = new Repository();
    final Scanner scanner = new Scanner(System.in);
    final List<ProductVariation> products = repository.getProducts();

    AddToCart() throws IOException {
        final Costumer costumer = promptCostumer();
        System.out.println("\nVälkommen " + costumer.getName());

        final String requestedProduct = promptProductName();

        while (true) {
            final int productId = promptProductVariationId(requestedProduct);

            try {
                repository.addToCart(costumer.getId(), costumer.getActiveOrderId(), productId);
                System.out.println("Produkten lades till i Kundvagnen!");
                break;
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public Costumer promptCostumer() {
        String username, password;
        Costumer costumer = null;
        do {
            System.out.println("Skriv in användarnamn:");
            username = scanner.nextLine();
            System.out.println("Skriv in lösenord:");
            password = scanner.nextLine();
            try {
                costumer = this.repository.retrieveCostumer(username, password);
            } catch (SQLException | LoginException e) {
                System.out.println(e.getMessage());
                System.out.println("Var god försök igen...");
            }
        } while (costumer == null);

        return costumer;
    }

    public String promptProductName() {
        String requestedProduct;
        List<String> availableVariations;

        do {
            System.out.println("\nDessa Produkter är Tillgängliga i vårat Lager:");
            this.getAllProducts().forEach(System.out::println);

            System.out.println("\nVar god välj en produkt för att gå vidare:");
            requestedProduct = scanner.nextLine();
            availableVariations = this.getAllVariations(requestedProduct);

            if (availableVariations.isEmpty()) {
                System.out.println("Den produkten finns inte i lagret, var god försök igen:");
            }
        } while (availableVariations.isEmpty());

        return requestedProduct;
    }

    public int promptProductVariationId(final String requestedProduct) {
        Optional<ProductVariation> optionalProduct;

            do {
                System.out.println("\n"+ requestedProduct + " finns tillgängliga i dessa varianter:");
                this.getAllVariations(requestedProduct).forEach(System.out::println);
                System.out.println("Vilken storlek önskas?");
                final int size = Integer.parseInt(scanner.nextLine());
                System.out.println("Vilken färg önskas?");
                final String color = scanner.nextLine();

                optionalProduct = this.products.stream()
                        .filter(p -> requestedProduct.equals(p.getBrand() + " " + p.getModel()))
                        .filter(p -> p.getSize() == size && Objects.equals(p.getColor(), color))
                        .findFirst();

                if (optionalProduct.isEmpty()) {
                    System.out.println("Produkten du vill lägga till fanns inte! Försök igen:");
                }
            } while (optionalProduct.isEmpty());

        return optionalProduct.get().getId();
    }


    /*
        Returnerar en lista med formaterade strängar på alla unika produkter som finns i lagret
     */
    public List<String> getAllProducts() {
        return this.products.stream().filter(p -> p.getStock() > 0)
                .map(p -> " •" + p.getBrand() + " " + p.getModel() + " | " + p.getPrice()+":-")
                .distinct().toList();
    }

    /*
        Returnerar en lista med formaterade strängar på alla variationer, storlek och färg av en vald produkt, sorterad
        efter storlek
     */
    public List<String> getAllVariations(String requestedProduct) {
        return this.products.stream()
                .filter(p -> requestedProduct.equals(p.getBrand() + " " + p.getModel()))
                .filter(p -> p.getStock() > 0).sorted(Comparator.comparing(ProductVariation::getSize))
                .map(p -> " • Storlek: " + p.getSize() + " | Färg: " + p.getColor()).toList();
    }

    public static void main(String[] args) throws IOException {
        new AddToCart();
    }
}
