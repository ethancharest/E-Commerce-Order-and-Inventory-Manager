package ecommerce.ui;
//this class should contain the GUI which allows for login and login validation

import ecommerce.model.Role;
import ecommerce.service.AuthService;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.util.function.Consumer;
import javax.swing.*;

public class LoginFrame extends JFrame implements ActionListener {

    private JTextField userField;
    private JPasswordField passField;
    private JButton loginButton;
    private Role role;
    private Consumer<Role> onLogin; // receives role

    public LoginFrame(Consumer<Role> onLogin) {
        role = Role.NOTFOUND;
        this.onLogin = onLogin;

        setTitle("Login");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(300, 180);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // padding

        panel.add(new JLabel("Username:"));
        userField = new JTextField();
        panel.add(userField);

        panel.add(new JLabel("Password:"));
        passField = new JPasswordField();
        panel.add(passField);

        panel.add(new JLabel()); // spacer
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        try {
            AuthService authService = new AuthService();
            role = authService.authenticate(username, password);
            if (role == Role.NOTFOUND) {
                JOptionPane.showMessageDialog(this, "Authentication failed. Invalid username or password.");
            } else {
                JOptionPane.showMessageDialog(this, "Authentication successful! Logged in as: " + username + " with role: " + role);
                onLogin.accept(role);
                LoginFrame.this.dispose(); // activates startApp in EcommerceApp
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }
}
