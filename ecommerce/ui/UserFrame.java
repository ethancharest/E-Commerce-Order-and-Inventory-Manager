package ecommerce.ui;
//this class should contain the GUI which is what a CUSTOMER would see when logged in
//can browse products (apply filters like price) and place orders; browse and order products + see past orders

import ecommerce.model.Address;
import ecommerce.model.Cart;
import ecommerce.model.Order;
import ecommerce.service.OrderService;
import ecommerce.service.ProductService;
import ecommerce.service.SimpleTaxCalc;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import javax.swing.*;

public class UserFrame extends JFrame implements ActionListener {

    private ProductService productService; // Service to manage products
    private JPanel mainPanel;              // Main panel for the frame
    private JButton addProductBtn;         // Button
    private JButton updateProductBtn;      // Button
    private JButton deleteProductBtn;      // Button
    private JButton viewProductsBtn;       // Button
    private JComboBox<String> viewFiltersBtn;       // Dropdown for filters
    private JButton viewOrdersBtn;         // Button
    private JButton viewCartBtn;          // Button
    private JButton checkoutBtn;         // Button
    private JButton logoutBtn;             // Button
    private JTextArea displayArea;         // Area to display information
    private JScrollPane scrollPane;        // Scroll pane for display area
    private int filterOption;          // Current filter option
    private Cart cart;                     // Customer's shopping cart
    private Address address;               // Customer's address for orders
    private SimpleTaxCalc taxCalculator; // Tax calculator based on address
    private String usernameStr;
    private OrderService orderService;

    public UserFrame(String username) throws IOException {
        this.usernameStr = username;
        // Initialize the ProductService
        productService = new ProductService();

        orderService = new OrderService();

        // No filter by default
        filterOption = 0;

        // Initialize the customer's cart
        cart = new Cart();

        taxCalculator = new SimpleTaxCalc();

        address = null; // No address initially

        // Set up the frame
        setTitle("User Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null); // centers the window on the screen
        setResizable(true);

        // Create main panel with BorderLayout for better organization
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top Panel: hold all admin action buttons 
        JPanel buttonPanel = createButtonPanel();
        buttonPanel.setBackground(new Color(245, 245, 245));
        mainPanel.add(buttonPanel, BorderLayout.NORTH);

        // Middle Panel: Create center display area, read only text area 
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(displayArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Bottom Panel: Create logout panel 
        JPanel logoutPanel = new JPanel();
        logoutPanel.setBackground(new Color(245, 245, 245));
        logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(this);
        logoutBtn.setBackground(new Color(220, 53, 69)); // Red color
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        logoutPanel.add(logoutBtn);
        mainPanel.add(logoutPanel, BorderLayout.SOUTH);

        add(mainPanel);
        // Pack and ensure layout updates before showing the frame, overrides to 800x600
        pack();
        setSize(800, 600);
        mainPanel.revalidate();
        mainPanel.repaint();
        setVisible(true);

        // Display welcome message, ensures panel isnt empty on launch
        displayWelcomeMessage();
    }

    /**
     * Creates the top panel containing all product/order management buttons
     *
     * @return the button panel
     */
    private JPanel createButtonPanel() {
        // 2 rows, 3 columns grid: keep buttons organized
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Product Management"));

        // Ensure the button panel reserves enough height so buttons dont get squished 
        panel.setPreferredSize(new Dimension(0, 120));

        // Add Product Button
        addProductBtn = new JButton("Add Product to Cart");
        addProductBtn.addActionListener(this);
        addProductBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(addProductBtn);

        // Update Product Button
        updateProductBtn = new JButton("Update Product in Cart");
        updateProductBtn.addActionListener(this);
        updateProductBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(updateProductBtn);

        // Delete Product Button
        deleteProductBtn = new JButton("Delete Product from Cart");
        deleteProductBtn.addActionListener(this);
        deleteProductBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(deleteProductBtn);

        // View Products Button
        viewProductsBtn = new JButton("View Products");
        viewProductsBtn.addActionListener(this);
        viewProductsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(viewProductsBtn);

        // View Filters Button
        viewFiltersBtn = new JComboBox<>(new String[]{"No Filter", "By Name", "By Category", "Price: Low to High", "Price: High to Low", "Stock: Low to High", "Stock: High to Low"});
        viewFiltersBtn.addActionListener(this);
        viewFiltersBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(viewFiltersBtn);
        viewFiltersBtn.setVisible(false); // Hide filter dropdown until on view products page

        // View Orders Button
        viewOrdersBtn = new JButton("View Past Orders");
        viewOrdersBtn.addActionListener(this);
        viewOrdersBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(viewOrdersBtn);

        // View Cart Button
        viewCartBtn = new JButton("View Cart");
        viewCartBtn.addActionListener(this);
        viewCartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(viewCartBtn);

        // Checkout Button 
        checkoutBtn = new JButton("Checkout");
        checkoutBtn.addActionListener(this);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(checkoutBtn);

        return panel;
    }

    /**
     * Displays a welcome message in display area, thank you program, very nice
     * of you to welcome me so kindly!
     */
    private void displayWelcomeMessage() {
        displayArea.setText("========================================\n");
        displayArea.append("Welcome to Customer Dashboard!\n");
        displayArea.append("========================================\n\n");
        displayArea.append("You have access to the following features:\n");
        displayArea.append("• Add Product to Cart\n");
        displayArea.append("• Update Product in Cart\n");
        displayArea.append("• Delete Product in Cart\n");
        displayArea.append("• View Cart\n");
        displayArea.append("• View Past Orders\n\n");
        displayArea.append("Please select an operation using the buttons above.\n");
    }

    /**
     * Event handles for button clicks
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (viewFiltersBtn.isVisible() && e.getSource() != viewFiltersBtn) {
            // Hide filter dropdown when not viewing products
            viewFiltersBtn.setVisible(false);
        }
        try {
            if (e.getSource() == addProductBtn) {
                handleAddProduct();
            } else if (e.getSource() == updateProductBtn) {
                handleUpdateProduct();
            } else if (e.getSource() == deleteProductBtn) {
                handleDeleteProduct();
            } else if (e.getSource() == viewProductsBtn) {
                handleViewProducts();
            } else if (e.getSource() == viewFiltersBtn) {
                handleViewFilters();
            } else if (e.getSource() == viewOrdersBtn) {
                handleViewOrders();
            } else if (e.getSource() == viewCartBtn) {
                handleViewCart();
            } else if (e.getSource() == checkoutBtn) {
                // Checkout functionality to be implemented
                handleCheckout();
            } else if (e.getSource() == logoutBtn) {
                handleLogout();
            }
        } catch (IOException ex) {
            // If any IO exception occurs during handling (e.g file write), show error dialog
            showError("An error occurred: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Opens a dialog that lets the admin enter data for a new product Validates
     * basic feilds and uses ProductService to add product
     *
     * @throws IOException
     */
    private void handleAddProduct() throws IOException {
        // Create a dialog for adding a product
        JDialog dialog = new JDialog(this, "Add Product", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        panel.add(quantityLabel);
        panel.add(quantityField);

        JButton submitBtn = new JButton("Add Product to Cart");
        JButton cancelBtn = new JButton("Cancel");

        //Lamda used for cleaner event handling inside the dialog
        submitBtn.addActionListener(ev -> {
            try {
                String name = nameField.getText().trim();
                String quantityStr = quantityField.getText().trim();

                //Basic Valiation: require all feilds to be filled in 
                if (name.isEmpty() || quantityStr.isEmpty()) {
                    showError("Please fill in all fields.");
                    return;
                }

                int quantity = Integer.parseInt(quantityStr);

                //Additional Validation: price and quantity must be non-negative
                if (quantity <= 0) {
                    showError("Quantity must be positive integer.");
                    return;
                } else if (quantity > productService.getProductByName(name).getAvailableStock()) {
                    showError("Requested quantity exceeds available stock.");
                    return;
                }

                // Delegate actual product creation to ProductService
                cart.add(productService.getProductByName(name), quantity);
                displayArea.setText("✓ Product added successfully: " + name + "\n");
                dialog.dispose();
                showSuccess("Product added successfully!");
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for price and stock.");
            } catch (IOException ex) {
                showError("Error adding product: " + ex.getMessage());
            }
        });

        // Cancels the dialog without applying changes 
        cancelBtn.addActionListener(ev -> dialog.dispose());

        panel.add(submitBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Opens a dialog to update an existing product by ID Admin must provide ID
     * plus the new feild values
     *
     * @throws IOException
     */
    private void handleUpdateProduct() throws IOException {
        JDialog dialog = new JDialog(this, "Update Product", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        panel.add(quantityLabel);
        panel.add(quantityField);

        JButton submitBtn = new JButton("Update Product");
        JButton cancelBtn = new JButton("Cancel");

        submitBtn.addActionListener(ev -> {
            try {
                String name = nameField.getText().trim();
                String quantityStr = quantityField.getText().trim();

                // All feilds required for an update 
                if (name.isEmpty() || quantityStr.isEmpty()) {
                    showError("Please fill in all fields.");
                    return;
                }

                int quantity = Integer.parseInt(quantityStr);

                if (quantity <= 0) {
                    showError("Quantity must be positive integer.");
                    return;
                } else if (quantity > productService.getProductByName(name).getAvailableStock()) {
                    showError("Requested quantity exceeds available stock.");
                    return;
                }

                // Delegate update to Cart
                cart.setCartQuantity(productService.getProductByName(name).getId(), quantity);
                displayArea.setText("✓ Product updated successfully: " + name + "\n");
                dialog.dispose();
                showSuccess("Product updated successfully!");
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for name and quantity.");
            } catch (IOException ex) {
                showError("Error updating product: " + ex.getMessage());
            }
        });

        cancelBtn.addActionListener(ev -> dialog.dispose());

        panel.add(submitBtn);
        panel.add(cancelBtn);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    /**
     * Prompts the admin for a product ID and deletes that product if confirmed
     *
     * @throws IOException
     */
    private void handleDeleteProduct() throws IOException {
        String nameStr = JOptionPane.showInputDialog(this, "Enter the product name to delete:", "Delete Product",
                JOptionPane.QUESTION_MESSAGE);

        // Only happens if the admin didnt cancel and typed something 
        if (nameStr != null && !nameStr.trim().isEmpty()) {
            try {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete product " + nameStr + " from cart?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    cart.remove(productService.getProductByName(nameStr).getId());
                    displayArea.setText("✓ Product removed successfully: " + nameStr + "\n");
                    showSuccess("Product removed successfully!");
                }
            } catch (NumberFormatException ex) {
                showError("Please enter a valid product name.");
            }
        }
    }

    /**
     * Loads product information from ProductService and displays it in the text
     * area
     *
     * @throws IOException
     */
    private void handleViewProducts() throws IOException {
        viewFiltersBtn.setVisible(true); // Show filter dropdown when viewing products
        String products = productService.displayProducts(filterOption, false);
        displayArea.setText("========================================\n");
        displayArea.append("CURRENT PRODUCTS\n");
        displayArea.append("========================================\n\n");
        displayArea.append(products != null ? products : "No products available.");
    }

    /**
     * Sorts and displays products based on selected filter
     *
     * @throws IOException
     */
    private void handleViewFilters() throws IOException {
        String selectedFilter = (String) viewFiltersBtn.getSelectedItem();
        int sortOption;

        switch (selectedFilter) {
            case "By Name":
                sortOption = 1;
                break;
            case "By Category":
                sortOption = 2;
                break;
            case "Price: Low to High":
                sortOption = 3;
                break;
            case "Price: High to Low":
                sortOption = 4;
                break;
            case "Stock: Low to High":
                sortOption = 5;
                break;
            case "Stock: High to Low":
                sortOption = 6;
                break;
            default:
                sortOption = 0; // No Filter
        }
        filterOption = sortOption; // Update current filter option
        handleViewProducts();
    }

    /**
     * Placehoder for VIEW ALL ORDERS feature, this will be changed later
     */
    private void handleViewOrders() {
        displayArea.setText("========================================\n");
        displayArea.append("VIEW ALL ORDERS\n");
        displayArea.append("========================================\n\n");
        displayArea.append("View all orders feature is under development.\n");
        displayArea.append("This feature will display all customer orders in the system.\n");
    }

    /**
     * Displays the contents of the customer's cart
     */
    private void handleViewCart() {
        displayArea.setText("========================================\n");
        displayArea.append("YOUR SHOPPING CART\n");
        displayArea.append("========================================\n\n");
        String cartContents = cart.display();
        displayArea.append(cartContents.isEmpty() ? "Your cart is empty.\n" : cartContents);
    }

    /**
     * Placeholder for checkout functionality
     */
    private void handleCheckout() throws IOException {
        int beginCheckout = JOptionPane.showConfirmDialog(this,
                "Are you ready to checkout?", "Confirm Checkout",
                JOptionPane.YES_NO_OPTION);
        if (beginCheckout == JOptionPane.YES_OPTION && address == null) {
            JDialog dialog = new JDialog(this, "Address", true);
            dialog.setSize(400, 350);
            dialog.setLocationRelativeTo(this);
            dialog.setResizable(false);

            JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel streetLabel = new JLabel("Street:");
            JTextField streetField = new JTextField();
            panel.add(streetLabel);
            panel.add(streetField);

            JLabel cityLabel = new JLabel("City:");
            JTextField cityField = new JTextField();
            panel.add(cityLabel);
            panel.add(cityField);

            JLabel stateLabel = new JLabel("State:");
            JTextField stateField = new JTextField();
            panel.add(stateLabel);
            panel.add(stateField);

            JLabel zipCodeLabel = new JLabel("Zip Code:");
            JTextField zipCodeField = new JTextField();
            panel.add(zipCodeLabel);
            panel.add(zipCodeField);

            JButton submitBtn = new JButton("Confirm Address");
            JButton cancelBtn = new JButton("Cancel");

            submitBtn.addActionListener(ev -> {
                try {
                    String street = streetField.getText().trim();
                    String city = cityField.getText().trim();
                    String state = stateField.getText().trim();
                    String zipCode = zipCodeField.getText().trim();

                    // All feilds required for an update 
                    if (street.isEmpty() || city.isEmpty() || state.isEmpty() || zipCode.isEmpty()) {
                        showError("Please fill in all fields.");
                        return;
                    }
                    address = new Address(street, city, state, zipCode);
                    displayArea.setText("✓ Address updated successfully: " + address.toString() + "\n");
                    dialog.dispose();
                    showSuccess("Address updated successfully!");
                } catch (NumberFormatException ex) {
                    showError("Please enter valid address.");
                }
            });

            cancelBtn.addActionListener(ev -> dialog.dispose());

            panel.add(submitBtn);
            panel.add(cancelBtn);

            dialog.add(panel);
            dialog.setVisible(true);
        }
        double tax = taxCalculator.calculateTax(address, cart.subtotal());
        double totalPrice = tax + cart.subtotal();
        String taxStr = String.format("%.2f", tax);
        String totalPriceStr = String.format("%.2f", totalPrice);
        String subtotalStr = String.format("%.2f", cart.subtotal());
        int confirmCheckout = JOptionPane.showConfirmDialog(this, "Are you sure you want to checkout?\nSubtotal: " + subtotalStr + "\nTax: " + taxStr + "\nTotal: " + totalPriceStr, "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirmCheckout == JOptionPane.YES_OPTION) {
            displayArea.setText("✓ Checkout complete! Total charged: $" + totalPriceStr + "\n");
            try {
                cart.updateProductsStock(productService);
            } catch (IOException ex) {
                showError("Error during checkout: " + ex.getMessage());
            }
            Order order = new Order(usernameStr, cart.getItems(), totalPrice);
            orderService.processOrder(order);
            cart.clear();
            //still need to implement actually placing the order and saving it to orders.csv etc
            showSuccess("Checkout complete! Thank you for your purchase.");
        }
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            displayArea.setText("Logging out...\n");
            dispose();
            // Return to login screen, this can be removed if not needed
            try {
                new LoginFrame((role, username) -> {
                    try {
                        new UserFrame(username); // or appropriate frame based on role, can be changed later
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper method to show success message
     *
     * @param message
     */
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Helper method to show error message
     *
     * @param message
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
