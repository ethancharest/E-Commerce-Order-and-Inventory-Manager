package ecommerce;

import ecommerce.model.*;
import ecommerce.service.OrderService;
import ecommerce.ui.*;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;

public class EcommerceApp {

    private static LoginFrame loginFrame;

    public static void main(String[] args) throws IOException {

        OrderService orderService = new OrderService();
        orderService.fillQueue();
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                orderService.processNextOrder();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 1, 2, TimeUnit.MINUTES);

        // Create the login frame on the Swing event thread
        SwingUtilities.invokeLater(() -> {
            loginFrame = new LoginFrame((role, username) -> {
                try { // on successful login
                    startApp(role, username);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private static void startApp(Role role, String username) throws IOException {
        loginFrame.dispose(); // close login window
        if (role == Role.ADMIN) {
            SwingUtilities.invokeLater(() -> {
                try {
                    new AdminFrame();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else if (role == Role.CUSTOMER) {
            SwingUtilities.invokeLater(() -> {
                try {
                    new UserFrame(username);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("No role found. Exiting application.");
        }
    }
}
