import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

// ==========================================
// ⚠️ IMPORTANT : Ce fichier DOIT s'appeler "FoodOrderingSystemV3.java"
// ==========================================

// --- 1. INTERFACES & PAYMENTS (Polymorphisme) ---

/**
 * Interface defining the contract for payment processing.
 */
interface IPayment {
    boolean processPayment(double amount);
}

/**
 * Payment via Credit Card.
 */
class CreditCardPayment implements IPayment {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("💳 Processing Credit Card Payment of $" + amount + "...");
        return true; 
    }
}

/**
 * Payment via Cash on Delivery.
 */
class CashPayment implements IPayment {
    @Override
    public boolean processPayment(double amount) {
        System.out.println("💵 Payment will be collected as Cash on Delivery: $" + amount);
        return true;
    }
}

// --- 2. CLASSE PARENTE (Héritage) ---

abstract class User {
    protected int id;
    protected String name;
    protected String email;
    protected String phone; 

    public User(int id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public abstract void showDashboard(); 

    // AJOUT : Méthode pour modifier les infos
    public void updateContactInfo(String newEmail, String newPhone) {
        this.email = newEmail;
        this.phone = newPhone;
        System.out.println("✅ Profile Updated Successfully!");
    }

    public String getName() { return name; }
}

// --- 3. CLASSES ENFANTS ---

class Admin extends User {
    public Admin(int id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public void showDashboard() {
        System.out.println("\n=== ADMIN DASHBOARD ===");
        System.out.println("👤 Admin: " + this.name + " (" + this.email + ")");
        System.out.println("1. View All Orders");
        System.out.println("2. Add New Item to Menu");
        System.out.println("0. Logout");
        System.out.print("Select Action: ");
    }
}

class Customer extends User {
    private List<FoodItem> cart;

    public Customer(int id, String name, String email, String phone) {
        super(id, name, email, phone);
        this.cart = new ArrayList<>();
    }

    public void addToCart(FoodItem item) {
        cart.add(item);
        System.out.println("✅ [OK] " + item.getName() + " added to cart.");
    }

    public void viewCart() {
        System.out.println("\n--- YOUR CART ---");
        if (cart.isEmpty()) {
            System.out.println("Cart is empty.");
        } else {
            double currentTotal = 0;
            for (FoodItem item : cart) {
                System.out.println("- " + item.getName() + ": $" + item.getPrice());
                currentTotal += item.getPrice();
            }
            System.out.println("-------------------------");
            System.out.println("Total: $" + currentTotal);
        }
    }

    public Order checkout() {
        if (cart.isEmpty()) return null;
        Order newOrder = new Order(cart, this); // On lie la commande au client
        cart.clear(); 
        return newOrder;
    }

    @Override
    public void showDashboard() {
        System.out.println("\n=== CUSTOMER DASHBOARD ===");
        System.out.println("👋 Welcome, " + this.name);
        System.out.println("📧 Email: " + this.email); // Affiche l'email
        System.out.println("📞 Phone: " + this.phone);
    }
}

// --- 4. CLASSES MÉTIER ---

class FoodItem {
    private static int counter = 1;
    private int id;
    private String name;
    private double price;

    public FoodItem(String name, double price) {
        this.id = counter++;
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    
    @Override
    public String toString() {
        return id + ". " + name + " - $" + price;
    }
}

class Order {
    private static int count = 1001;
    private int orderId;
    private Date date;
    private List<FoodItem> items;
    private double totalAmount;
    private String status;
    private User customer; // Pour savoir qui a commandé

    public Order(List<FoodItem> cartItems, User customer) {
        this.orderId = count++;
        this.date = new Date();
        this.items = new ArrayList<>(cartItems);
        this.customer = customer;
        calculateTotal();
        this.status = "PENDING";
    }

    private void calculateTotal() {
        this.totalAmount = 0;
        for (FoodItem item : items) {
            this.totalAmount += item.getPrice();
        }
    }

    public double getTotalAmount() { return totalAmount; }

    public void confirmPayment(String method) {
        this.status = "PAID (" + method + ")";
        System.out.println("🎉 [SUCCESS] Order #" + orderId + " confirmed for " + customer.getName() + "!");
    }

    @Override
    public String toString() {
        return "Order #" + orderId + " [" + status + "] | Customer: " + customer.getName() + " | Total: $" + totalAmount;
    }
}

// --- 5. MAIN CLASS ---
public class FoodOrderingSystemV3 {
    
    private static List<FoodItem> menu = new ArrayList<>();
    private static List<Order> allOrders = new ArrayList<>();

    public static void main(String[] args) {
        seedData();
        Scanner scanner = new Scanner(System.in);
        scanner.useLocale(Locale.US);
        
        // Utilisateurs avec EMAIL et TÉLÉPHONE
        Customer currentCustomer = new Customer(101, "Student", "student@arel.edu.tr", "+905551234567");
        Admin admin = new Admin(1, "Admin", "admin@arel.edu.tr", "+905009998877");

        while (true) {
            System.out.println("\n=================================");
            System.out.println("   ONLINE FOOD ORDERING SYSTEM   ");
            System.out.println("=================================");
            System.out.println("1. Login as Customer");
            System.out.println("2. Login as Admin");
            System.out.println("0. Exit Application");
            System.out.print("Choice: ");
            
            int choice = -1;
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.next(); 
                continue;
            }

            if (choice == 0) break;
            
            if (choice == 1) {
                customerFlow(scanner, currentCustomer);
            } else if (choice == 2) {
                adminFlow(scanner, admin);
            } else {
                System.out.println("Invalid choice.");
            }
        }
        System.out.println("System closed.");
        scanner.close();
    }

    // --- WORKFLOW CLIENT ---
    private static void customerFlow(Scanner scanner, Customer customer) {
        // Boucle du menu client
        boolean back = false;
        while (!back) {
            customer.showDashboard(); // Affiche dashboard à chaque tour
            System.out.println("\n1. View Menu & Order");
            System.out.println("2. View Cart");
            System.out.println("3. Checkout & Pay");
            System.out.println("4. Update Profile"); // NOUVELLE OPTION
            System.out.println("0. Logout");
            System.out.print("Action: ");
            
            int action = -1;
            try {
                action = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.next(); continue;
            }

            switch (action) {
                case 1:
                    System.out.println("\n--- MENU ---");
                    for (FoodItem item : menu) {
                        System.out.println(item);
                    }
                    System.out.print("Enter ID of item to add (0 to cancel): ");
                    try {
                        int foodId = scanner.nextInt();
                        if (foodId > 0 && foodId <= menu.size()) {
                            customer.addToCart(menu.get(foodId - 1));
                        }
                    } catch (InputMismatchException e) {
                        scanner.next();
                    }
                    break;
                case 2:
                    customer.viewCart();
                    break;
                case 3:
                    Order order = customer.checkout();
                    if (order != null) {
                        System.out.println("Total Amount: $" + order.getTotalAmount());
                        
                        System.out.println("Select Payment Method:");
                        System.out.println("1. Credit Card");
                        System.out.println("2. Cash on Delivery");
                        System.out.print("Choice: ");
                        
                        int payChoice = scanner.nextInt();
                        IPayment paymentMethod = null;
                        String methodStr = "";

                        if (payChoice == 1) {
                            paymentMethod = new CreditCardPayment();
                            methodStr = "Credit Card";
                        } else if (payChoice == 2) {
                            paymentMethod = new CashPayment();
                            methodStr = "Cash";
                        } else {
                            System.out.println("Invalid payment choice. Order Cancelled.");
                            break;
                        }

                        if (paymentMethod.processPayment(order.getTotalAmount())) {
                            order.confirmPayment(methodStr);
                            allOrders.add(order);
                        }
                    } else {
                        System.out.println("Cart is empty!");
                    }
                    break;
                case 4: // UPDATE PROFILE LOGIC
                    System.out.println("\n--- UPDATE PROFILE ---");
                    System.out.print("Enter New Email: ");
                    String newEmail = scanner.next();
                    System.out.print("Enter New Phone (No spaces): ");
                    String newPhone = scanner.next();
                    customer.updateContactInfo(newEmail, newPhone);
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // --- WORKFLOW ADMIN ---
    private static void adminFlow(Scanner scanner, Admin admin) {
        boolean back = false;
        while (!back) {
            admin.showDashboard(); 
            int action = -1;
            try {
                action = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.next(); continue;
            }

            switch (action) {
                case 1: 
                    System.out.println("\n--- ALL CUSTOMER ORDERS ---");
                    if (allOrders.isEmpty()) {
                        System.out.println("No orders placed yet.");
                    } else {
                        for (Order o : allOrders) {
                            System.out.println(o);
                        }
                    }
                    System.out.println("---------------------------");
                    break;
                
                case 2: 
                    System.out.println("\n--- ADD NEW MENU ITEM ---");
                    System.out.print("Enter Name (No spaces): ");
                    String name = scanner.next(); 
                    System.out.print("Enter Price (e.g. 15.5): ");
                    try {
                        double price = scanner.nextDouble();
                        menu.add(new FoodItem(name, price));
                        System.out.println("✅ Item added!");
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid price.");
                        scanner.next();
                    }
                    break;

                case 0: back = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void seedData() {
        menu.add(new FoodItem("CheeseBurger", 8.99));
        menu.add(new FoodItem("Pizza", 12.50));
        menu.add(new FoodItem("Coke", 2.00));
    }
}