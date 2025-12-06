package ecommerce.ui;
//this class should contain the GUI which is what an ADMIN would see when logged in

import ecommerce.model.OrderStatus;
import ecommerce.model.Role;
import ecommerce.service.OrderService;
import ecommerce.service.ProductService;
import ecommerce.service.ReportService;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;

public class AdminFrame extends JFrame implements ActionListener {

    private ProductService productService; // Service to manage products
    private JPanel mainPanel;              // Main panel for the frame
    private JButton addProductBtn;         // Button
    private JButton updateProductBtn;      // Button
    private JButton deleteProductBtn;      // Button
    private JButton viewProductsBtn;       // Button
    private JComboBox<String> viewFiltersBtn;       // Dropdown for filters
    private JButton viewOrdersBtn;         // Button
    private JComboBox<String> updateOrderStatusBtn; // Dropdown for order status updates
    private JButton logoutBtn;             // Button
    private JButton viewReportsBtn;       // Button
    private JButton searchBtn;             // Button for searching products
    private JTextArea displayArea;         // Area to display information
    private JScrollPane scrollPane;        // Scroll pane for display area
    private int filterOption;          // Current filter option
    private OrderService orderService;
    private ReportService reportService;

    public AdminFrame() throws IOException {
        // Initialize the ProductService
        productService = new ProductService();

        orderService = new OrderService();

        reportService = new ReportService();

        // No filter by default
        filterOption = 0;

        // Set up the frame
        setTitle("Admin Dashboard");
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
    private JPanel createButtonPanel() throws IOException {
        // 2 rows, 3 columns grid: keep buttons organized
        JPanel panel = new JPanel(new GridLayout(2, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Product Management"));

        // Ensure the button panel reserves enough height so buttons dont get squished 
        panel.setPreferredSize(new Dimension(0, 120));

        // View Orders Button
        viewOrdersBtn = new JButton("View All Orders");
        viewOrdersBtn.addActionListener(this);
        viewOrdersBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(viewOrdersBtn);

        // Update Order Status Button
        ArrayList<String> orderIds = new ArrayList<>();
        orderIds.add("Update Order Status");
        ArrayList<String> fetchedOrderIds = orderService.getAllOrderIds();
        orderIds.addAll(fetchedOrderIds);
        updateOrderStatusBtn = new JComboBox<>(orderIds.toArray(new String[0]));
        updateOrderStatusBtn.addActionListener(this);
        updateOrderStatusBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(updateOrderStatusBtn);
        updateOrderStatusBtn.setVisible(false); // Hide order status dropdown until on view orders page

        // View Products Button
        viewProductsBtn = new JButton("View Products");
        viewProductsBtn.addActionListener(this);
        viewProductsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(viewProductsBtn);

        // View Filters Button (placed next to View Products)
        viewFiltersBtn = new JComboBox<>(new String[]{"No Filter", "By Name", "By Category", "Price: Low to High", "Price: High to Low", "Stock: Low to High", "Stock: High to Low"});
        viewFiltersBtn.addActionListener(this);
        viewFiltersBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(viewFiltersBtn);
        viewFiltersBtn.setVisible(false); // Hide filter dropdown until on view products page

        // Search Button 
        searchBtn = new JButton("Search");
        searchBtn.addActionListener(this);
        searchBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(searchBtn);

        // Add Product Button
        addProductBtn = new JButton("Add Product");
        addProductBtn.addActionListener(this);
        addProductBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(addProductBtn);

        // Update Product Button
        updateProductBtn = new JButton("Update Product");
        updateProductBtn.addActionListener(this);
        updateProductBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(updateProductBtn);

        // Delete Product Button
        deleteProductBtn = new JButton("Delete Product");
        deleteProductBtn.addActionListener(this);
        deleteProductBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(deleteProductBtn);

        // View Reports Button 
        viewReportsBtn = new JButton("View Reports");
        viewReportsBtn.addActionListener(this);
        viewReportsBtn.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(viewReportsBtn);

        return panel;
    }

    /**
     * Displays a welcome message in display area, thank you program, very nice
     * of you to welcome me so kindly!
     */
    private void displayWelcomeMessage() {
        displayArea.setText("========================================\n");
        displayArea.append("Welcome to Admin Dashboard!\n");
        displayArea.append("========================================\n\n");
        displayArea.append("You have access to the following features:\n");
        displayArea.append("• Add Product\n");
        displayArea.append("• Update Product\n");
        displayArea.append("• Delete Product\n");
        displayArea.append("• View Products\n");
        displayArea.append("• View All Orders\n\n");
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
        if (updateOrderStatusBtn.isVisible() && e.getSource() != updateOrderStatusBtn) {
            // Reset order status dropdown when not updating order status
            updateOrderStatusBtn.setVisible(false);
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
            } else if (e.getSource() == searchBtn) {
                handleSearchProducts();
            } else if (e.getSource() == viewFiltersBtn) {
                handleViewFilters();
            } else if (e.getSource() == viewOrdersBtn) {
                handleViewOrders();
            } else if (e.getSource() == updateOrderStatusBtn) {
                handleUpdateOrderStatus();
            } else if (e.getSource() == viewReportsBtn) {
                handleViewReports();
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

        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField();
        panel.add(categoryLabel);
        panel.add(categoryField);

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();
        panel.add(priceLabel);
        panel.add(priceField);

        JLabel stockLabel = new JLabel("Stock Quantity:");
        JTextField stockField = new JTextField();
        panel.add(stockLabel);
        panel.add(stockField);

        JButton submitBtn = new JButton("Add Product");
        JButton cancelBtn = new JButton("Cancel");

        //Lamda used for cleaner event handling inside the dialog
        submitBtn.addActionListener(ev -> {
            try {
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                String priceStr = priceField.getText().trim();
                String stockStr = stockField.getText().trim();

                //Basic Valiation: require all feilds to be filled in 
                if (name.isEmpty() || category.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
                    showError("Please fill in all fields.");
                    return;
                }

                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);

                //Additional Validation: price and stock must be non-negative
                if (price < 0 || stock < 0) {
                    showError("Price and stock must be non-negative.");
                    return;
                }

                // Delegate actual product creation to ProductService
                productService.addProduct(name, category, price, stock);
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

        JLabel idLabel = new JLabel("Product ID:");
        JTextField idField = new JTextField();
        panel.add(idLabel);
        panel.add(idField);

        JLabel nameLabel = new JLabel("Product Name:");
        JTextField nameField = new JTextField();
        panel.add(nameLabel);
        panel.add(nameField);

        JLabel categoryLabel = new JLabel("Category:");
        JTextField categoryField = new JTextField();
        panel.add(categoryLabel);
        panel.add(categoryField);

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();
        panel.add(priceLabel);
        panel.add(priceField);

        JLabel stockLabel = new JLabel("Stock Quantity:");
        JTextField stockField = new JTextField();
        panel.add(stockLabel);
        panel.add(stockField);

        JButton submitBtn = new JButton("Update Product");
        JButton cancelBtn = new JButton("Cancel");

        submitBtn.addActionListener(ev -> {
            try {
                String idStr = idField.getText().trim();
                String name = nameField.getText().trim();
                String category = categoryField.getText().trim();
                String priceStr = priceField.getText().trim();
                String stockStr = stockField.getText().trim();

                // All feilds required for an update 
                if (idStr.isEmpty() || name.isEmpty() || category.isEmpty() || priceStr.isEmpty()
                        || stockStr.isEmpty()) {
                    showError("Please fill in all fields.");
                    return;
                }

                if (!productService.validateProductByID(idStr)) {
                    showError("Product ID not found.");
                    return;
                }

                int id = Integer.parseInt(idStr);
                double price = Double.parseDouble(priceStr);
                int stock = Integer.parseInt(stockStr);

                if (price < 0 || stock < 0) {
                    showError("Price and stock must be non-negative.");
                    return;
                }

                // Delegate update to ProductService
                productService.updateProduct(id, name, category, price, stock);
                displayArea.setText("✓ Product updated successfully: ID " + id + "\n");
                dialog.dispose();
                showSuccess("Product updated successfully!");
            } catch (NumberFormatException ex) {
                showError("Please enter valid numbers for ID, price, and stock.");
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
        String idStr = JOptionPane.showInputDialog(this, "Enter the product ID to delete:", "Delete Product",
                JOptionPane.QUESTION_MESSAGE);

        // Only happens if the admin didnt cancel and typed something 
        if (idStr != null && !idStr.trim().isEmpty() && productService.validateProductByID(idStr)) {
            try {
                int id = Integer.parseInt(idStr);
                int confirm = JOptionPane.showConfirmDialog(this,
                        "Are you sure you want to delete product ID " + id + "?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    productService.deleteProduct(id);
                    displayArea.setText("✓ Product deleted successfully: ID " + id + "\n");
                    showSuccess("Product deleted successfully!");
                }
            } catch (NumberFormatException ex) {
                showError("Please enter a valid product ID.");
            }
        } else if (idStr != null) {
            showError("Product ID not found.");
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
        String products = productService.displayProducts(filterOption, true);
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
    private void handleViewOrders() throws IOException {
        updateOrderStatusBtn.setVisible(true); // Show order status dropdown when viewing orders
        displayArea.setText("========================================\n");
        displayArea.append("VIEW ALL ORDERS\n");
        displayArea.append("========================================\n\n");
        String orders = orderService.getAllOrders();
        displayArea.append(orders != null ? orders : "No orders available.");
    }

    private void handleUpdateOrderStatus() {
        String selectedOrderId = (String) updateOrderStatusBtn.getSelectedItem();
        if (selectedOrderId != null && !selectedOrderId.equals("Update Order Status")) {
            String[] statusOptions = {"PROCESSED", "SHIPPED", "DELIVERED"};
            OrderStatus newStatus = OrderStatus.valueOf((String) JOptionPane.showInputDialog(this,
                    "Select new status for Order ID " + selectedOrderId + ":",
                    "Update Order Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    statusOptions,
                    statusOptions[0]));

            if (newStatus != null) {
                try {
                    orderService.updateOrderStatus(selectedOrderId, newStatus);
                    displayArea.setText("✓ Order status updated successfully: ID " + selectedOrderId + " to " + newStatus + "\n");
                    showSuccess("Order status updated successfully!");
                } catch (IOException ex) {
                    showError("Error updating order status: " + ex.getMessage());
                }
            }
        }
    }

    private void handleViewReports() {
        // Placeholder for future report viewing functionality
        displayArea.setText("========================================\n");
        displayArea.append("VIEW REPORTS\n");
        displayArea.append("========================================\n\n");
        displayArea.append(reportService.reportOutOfStock() + "\n");
        displayArea.append(reportService.reportTotalOrders() + "\n");
        displayArea.append(reportService.reportMostFrequentlyOrderedProducts() + "\n");
        displayArea.append(reportService.reportTotalRevenue() + "\n");
    }

    /**
     * Prompts admin for a search term and shows matching products
     */
    private void handleSearchProducts() {
        String query = JOptionPane.showInputDialog(this, "Enter product name to search:", "Search Products", JOptionPane.QUESTION_MESSAGE);
        if (query == null) {
            return; // canceled

        }
        try {
            String results = productService.searchProductsByName(query, true);
            displayArea.setText("========================================\n");
            displayArea.append("SEARCH RESULTS for '" + query + "'\n");
            displayArea.append("========================================\n\n");
            displayArea.append(results);
        } catch (IOException ex) {
            showError("Error searching products: " + ex.getMessage());
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
                        if (role == Role.ADMIN) {
                            new AdminFrame();
                        } else if (role == Role.CUSTOMER) {
                            new UserFrame(username);
                        } else {
                            System.out.println("No role found. Exiting application.");
                        }
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
