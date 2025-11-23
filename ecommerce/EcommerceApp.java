package ecommerce;

import ecommerce.model.*;
import ecommerce.ui.*;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.SwingUtilities;

public class EcommerceApp {

    private static Scanner scanner = new Scanner(System.in);
    private static LoginFrame loginFrame;

    public static void main(String[] args) throws IOException {
        /**
         * Removed the admin console prompts and transferred them into
         * AdminFrame()
         */
        // Create the login frame on the Swing event thread
        SwingUtilities.invokeLater(() -> {
            loginFrame = new LoginFrame(role -> {
                // runs after dispose in LoginFrame
                try {
                    startApp(role);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private static void startApp(Role role) throws IOException {
        loginFrame.dispose(); // close login window
        if (role == Role.ADMIN) {
            // Launch Admin GUI on the Swing event thread and exit the console flow
            SwingUtilities.invokeLater(() -> {
                try {
                    new AdminFrame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return; // GUI will handle further interactions
        } else if (role == Role.CUSTOMER) {
            SwingUtilities.invokeLater(() -> {
                try {
                    new UserFrame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return; // GUI will handle further interactions
        } else {
            System.out.println("No role found. Exiting application.");
        }
        scanner.close();
    }
}
/**
 * This function is retired now, but im keeping it here commented out just in case we need it before final submission 
 * 
 * 
 * ProductService productService = new ProductService();
            System.out.println("Welcome Admin! You have access to product management features.");
            String option = "";
            while (!option.equals("6")) {
                System.out.println("1. Add Product\n2. Update Product\n3. Delete Product\n4. View Products\n5. View all orders\n6. Logout");
                System.out.println("Please select an option: ");
                option = scanner.nextLine();
                switch (option) {
                    case "1":
                        System.out.println("Enter the product details to add (name,category,price,stock): ");
                        String[] details = scanner.nextLine().split(",");
                        productService.addProduct(details[0], details[1], Double.parseDouble(details[2]), Integer.parseInt(details[3]));
                        break;
                    case "2":
                        System.out.println("Enter the product ID to update: ");
                        int productId = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter the new product details (name,category,price,stock): ");
                        String[] newDetails = scanner.nextLine().split(",");
                        productService.updateProduct(productId, newDetails[0], newDetails[1], Double.parseDouble(newDetails[2]), Integer.parseInt(newDetails[3]));
                        break;
                    case "3":
                        System.out.println("Enter the product ID to delete: ");
                        int id = Integer.parseInt(scanner.nextLine());
                        productService.deleteProduct(id);
                        break;
                    case "4":
                        System.out.println("Current Products: \n" + productService.displayProducts());
                        break;
                    case "5":
                        System.out.println("View all orders feature is under development.");
                        break;
                    case "6":
                        System.out.println("Logging out...");
                        break;
                    default:
                        System.out.println("Invalid option selected.");
                }
            }
 */
