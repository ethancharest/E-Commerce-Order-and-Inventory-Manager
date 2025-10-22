package ecommerce;

import ecommerce.model.Role;
import ecommerce.service.AuthService;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class EcommerceApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Please enter your username: ");
        String username = scanner.nextLine();
        System.out.print("Please enter your password: ");
        String password = scanner.nextLine();
        try {
            AuthService authService = new AuthService();
            Role role = authService.authenticate(username, password);
            if (role == Role.NOTFOUND) {
                System.out.println("Authentication failed. Invalid username or password.");
            } else {
                System.out.println("Authentication successful! Logged in as: " + username + " with role: " + role);
            }
        } catch (FileNotFoundException e) {
            System.out.println("A file not found error occurred during authentication: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
