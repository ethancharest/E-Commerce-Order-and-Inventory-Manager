package ecommerce;

import ecommerce.model.Role;
import ecommerce.service.AuthService;
import ecommerce.service.ProductService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
//this is the main class which contains the main method to run the application

public class EcommerceApp {

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter your password: ");
        String password = scanner.nextLine();
        Role role = Role.NOTFOUND;
        try {
            AuthService authService = new AuthService();
            role = authService.authenticate(username, password);
            if (role == Role.NOTFOUND) {
                System.out.println("Authentication failed. Invalid username or password.");
            } else {
                System.out.println("Authentication successful! Logged in as: " + username + " with role: " + role);
            }
        } catch (FileNotFoundException e) {
            System.out.println("A file not found error occurred during authentication: " + e.getMessage());
        }
        if (role == Role.ADMIN) {
            ProductService productService = new ProductService();
            System.out.println("Welcome Admin! You have access to product management features.");
            String option = "";
            while (!option.equals("6")) {
                System.out.println("1. Add Product\n2. Update Product\n3. Delete Product\n4. View Products\n5. View all orders\n6. Logout");
                System.out.println("Please select an option: ");
                option = scanner.nextLine();
                switch (option) {
                    case "1":
                        System.out.println("Enter the product details to add (all details comma separated, no spaces): ");
                        String[] details = scanner.nextLine().split(",");
                        productService.addProduct(details[0], details[1], Double.parseDouble(details[2]), Integer.parseInt(details[3]));
                        break;
                    case "2":
                        System.out.println("Enter the product ID to update: ");
                        int productId = Integer.parseInt(scanner.nextLine());
                        System.out.println("Enter the new product details (all details comma separated, no spaces): ");
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
        }
    }
}
