package ecommerce.ui;
// This class creates and displays the login window for the application
// It gathers username and password, and authenticates the user via AuthService
// On successful authentication, it invokes a callback with the user's role

import ecommerce.model.Role;
import ecommerce.service.AuthService;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import javax.swing.*;

public class LoginFrame extends JFrame implements ActionListener {

    private JTextField userField;       // Text input feild for username
    private JPasswordField passField;   // Hidden input field for password
    private JButton loginButton;        // Login button
    private Role role;                  // stores authenticated user role
    private LoginSuccessListener listener;

    public LoginFrame(LoginSuccessListener listener) {
        // Initialize default role as NOTFOUND until authentication is successful
        role = Role.NOTFOUND;

        this.listener = listener;

        setTitle("Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 180);
        setLocationRelativeTo(null);    // centers the window on the screen

        // Panel organizes the form into a simple grid layout
        // Username row, Password row, Login button row
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // padding around the panel

        // Username label and input field
        panel.add(new JLabel("Username:"));
        userField = new JTextField();
        userField.addActionListener(this);
        panel.add(userField);

        //Password label and masked input field
        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        passField.addActionListener(this);
        panel.add(passField);

        // Empty label for spacing 
        panel.add(new JLabel());
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        panel.add(loginButton);

        add(panel);
        setVisible(true); // Display the window after construction 
    }

    public interface LoginSuccessListener {

        void onLoginSuccess(Role role, String username);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        performLogin();
    }

    private void performLogin() {
        // Read user input from the text fields
        String username = userField.getText();
        String password = new String(passField.getPassword());

        try {
            // AuthService loads credentials from file and verifies them 
            AuthService authService = new AuthService();
            role = authService.authenticate(username, password);

            if (role == Role.NOTFOUND) {
                // Failed login: show error, kep window open for retry
                JOptionPane.showMessageDialog(this, "Authentication failed. Invalid username or password.");
            } else {
                //Successful login: notify user and pass role back to main app via callback
                JOptionPane.showMessageDialog(this, "Authentication successful! Logged in as: " + username + " with role: " + role);
                listener.onLoginSuccess(role, username);
                dispose();
                LoginFrame.this.dispose(); // activates startApp in EcommerceApp
            }
        } catch (FileNotFoundException e1) {  // Only thrown if the credentials file is missing
            e1.printStackTrace();
        }
    }
}
