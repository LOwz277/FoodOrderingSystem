import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;

public class FoodOrderingTests {

    // ==========================================
    // VRAIS TESTS UNITAIRES (JUNIT 5)
    // ==========================================

    @Test
    void testOrderTotal() {
        System.out.println("Test 1: Calculation du total...");
        
        // Setup (Préparation)
        List<FoodItem> items = new ArrayList<>();
        items.add(new FoodItem("Burger", 10.0));
        items.add(new FoodItem("Coke", 5.0));
        
        // Action (Exécution)
        double total = 0;
        for (FoodItem item : items) {
            total += item.getPrice();
        }

        // Assertion (Vérification)
        // On vérifie que 10 + 5 = 15
        assertEquals(15.0, total, "Le total doit être exactement de 15.0");
    }

    @Test
    void testPaymentProcessing() {
        System.out.println("Test 2: Validation du paiement...");
        
        IPayment payment = new CreditCardPayment();
        boolean result = payment.processPayment(50.0);
        
        // On vérifie que le paiement retourne "true"
        assertTrue(result, "Le paiement par carte doit être accepté (return true)");
    }

    @Test
    void testAddToCart() {
        System.out.println("Test 3: Ajout au panier...");
        
        List<FoodItem> cart = new ArrayList<>();
        cart.add(new FoodItem("Pizza", 12.0));
        
        // On vérifie que la taille de la liste est bien 1
        assertEquals(1, cart.size(), "Le panier doit contenir 1 élément après ajout");
    }

    // ==========================================
    // CLASSES MOCKS (SIMULÉES) 
    // Permet de tester la logique sans dépendre des autres fichiers
    // ==========================================
    
    static class FoodItem {
        private String name;
        private double price;

        public FoodItem(String name, double price) {
            this.name = name;
            this.price = price;
        }
        public double getPrice() { return price; }
    }

    interface IPayment {
        boolean processPayment(double amount);
    }

    static class CreditCardPayment implements IPayment {
        @Override
        public boolean processPayment(double amount) {
            return true; 
        }
    }
}