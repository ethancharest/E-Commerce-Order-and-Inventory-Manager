package ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * ReportService generates business insights and analytics from order and
 * product data It reads from CSV files (orders, products, and orderProduct) and
 * produces formatted reports for admin review. reports are designed to help
 * track inventory & revenue
 */
public class ReportService {

    // CSV file paths used as data sources for report generation
    private static final String PRODUCTS_CSV = "ecommerce/data/products.csv";       // Contains product inventory data
    private static final String ORDERS_CSV = "ecommerce/data/orders.csv";           // Contains order metadata and totals
    private static final String ORDER_PRODUCTS_CSV = "ecommerce/data/orderProducts.csv"; // Maps orders to products and quantities

    /**
     * Generates an out-of-stock report listing all products with stock <= 0
     * This helps admins identify which products need to be reordered
     *
     * @return formatted report showing product IDs and names that are out of
     * stock
     */
    public String reportOutOfStock() {
        try {
            // Maps to store product information loaded from CSV
            Map<String, String> idToName = new HashMap<>();   // product ID -> product name
            Map<String, Integer> idToStock = new HashMap<>(); // product ID -> stock quantity

            File f = new File(PRODUCTS_CSV);
            if (!f.exists()) {
                return header("Out-of-Stock Report") + "Products file not found: " + PRODUCTS_CSV + "\n";
            }

            // Parse products.csv and extract ID, name, and stock for each product
            Scanner scanby = new Scanner(f);
            if (scanby.hasNextLine()) {
                scanby.nextLine(); // skip CSV header row
            }
            while (scanby.hasNextLine()) {
                String line = scanby.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");

                // CSV format: id, name, category, price, stock
                if (parts.length < 5) {
                    continue;
                }
                String id = parts[0].trim();
                String name = parts[1].trim();
                String stockStr = parts[4].trim();
                int stock = 0;
                try {
                    stock = Integer.parseInt(stockStr);
                } catch (NumberFormatException ignored) {
                }
                idToName.put(id, name);
                idToStock.put(id, stock);
            }
            scanby.close();

            // Build report output with header and product listings
            StringBuilder out = new StringBuilder();
            out.append(header("Out-of-Stock Report"));
            boolean any = false;

            // Iterate through all products and flag those with zero or negative stock
            for (Map.Entry<String, Integer> e : idToStock.entrySet()) {
                if (e.getValue() <= 0) {
                    any = true;
                    out.append("ID: ").append(e.getKey()).append(" | ")
                            .append(idToName.getOrDefault(e.getKey(), "<unknown>")).append("\n");
                }
            }
            // If no out-of-stock items found, display a positive message
            if (!any) {
                out.append("No out-of-stock items at this time.\n");
            }
            return out.toString();
        } catch (IOException ex) {
            return header("Out-of-Stock Report") + "Error reading products: " + ex.getMessage() + "\n";
        }
    }

    /**
     * Generates a report showing the total number of orders placed
     *
     * @return formatted report displaying total order count
     */
    public String reportTotalOrders() {
        try {
            File f = new File(ORDERS_CSV);
            if (!f.exists()) {
                return header("Total Orders Report") + "Orders file not found: " + ORDERS_CSV + "\n";
            }
            Scanner scanby = new Scanner(f);
            int totalOrders = 0;
            if (scanby.hasNextLine()) {
                scanby.nextLine(); // skip CSV header row
            }
            // Parse each order line and increment counter
            while (scanby.hasNextLine()) {
                String line = scanby.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");

                // CSV format: order id, customer id, total price, timestamp, status
                if (parts.length < 3) {
                    continue; // skip malformed lines

                }
                totalOrders++;
            }
            scanby.close();
            StringBuilder out = new StringBuilder();
            out.append(header("Total Orders Report"));
            out.append("Total Orders: ").append(totalOrders).append("\n");
            return out.toString();
        } catch (IOException ex) {
            return header("Total Orders Report") + "Error reading orders: " + ex.getMessage() + "\n";
        }
    }

    /**
     * Generates a report of the most frequently ordered products ranked by
     * total units sold.
     *
     * @return formatted report with top 10 products by quantity ordered
     */
    public String reportMostFrequentlyOrderedProducts() {
        try {
            File f = new File(ORDER_PRODUCTS_CSV);
            if (!f.exists()) {
                return header("Most Frequently Ordered Products Report") + "Order-products file not found: "
                        + ORDER_PRODUCTS_CSV + "\n";
            }

            // First pass: load product id -> name mapping from products.csv for display purposes
            Map<String, String> idToName = new HashMap<>();
            File pf = new File(PRODUCTS_CSV);
            if (pf.exists()) {
                Scanner scanman = new Scanner(pf);
                if (scanman.hasNextLine()) {
                    scanman.nextLine();
                }
                while (scanman.hasNextLine()) {
                    String line = scanman.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length < 2) {
                        continue;
                    }
                    idToName.put(parts[0].trim(), parts[1].trim());
                }
                scanman.close();
            }

            // Second pass: aggregate product quantities from order-products file across all orders
            Scanner scanby = new Scanner(f);
            if (scanby.hasNextLine()) {
                scanby.nextLine(); // skip CSV header row

            }
            Map<String, Integer> productCounts = new HashMap<>(); // product ID -> total units ordered
            while (scanby.hasNextLine()) {
                String line = scanby.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                // CSV format: order id, product id1, quantity1, product id2, quantity2
                // Each order can contain multiple products with their respective quantities
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue; // skip lines without at least one product
                }
                
                // Parse pairs of (product id, quantity) starting from index 1
                for (int i = 1; i + 1 < parts.length; i += 2) {
                    String pid = parts[i].trim();
                    String qtyStr = parts[i + 1].trim();
                    int qty = 0;
                    try {
                        qty = Integer.parseInt(qtyStr);
                    } catch (NumberFormatException ignored) {
                    }

                    // Accumulate quantities for each product across all orders
                    productCounts.put(pid, productCounts.getOrDefault(pid, 0) + qty);
                }
            }
            scanby.close();

            // Sort products by total quantity in descending order to identify top sellers
            List<Map.Entry<String, Integer>> list = new ArrayList<>(productCounts.entrySet());
            list.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

            StringBuilder out = new StringBuilder();
            out.append(header("Most Frequently Ordered Products Report"));

            // Handle edge case where no products have been ordered yet (likely never gonna be shown, but just in case)
            if (list.isEmpty()) {
                out.append("No ordered products found.\n");
                return out.toString();
            }

            // Display top 10 products by units ordered to avoid clutter
            int rank = 1;
            for (Map.Entry<String, Integer> e : list) {
                String name = idToName.getOrDefault(e.getKey(), "Product ID " + e.getKey());
                out.append(rank).append(". ").append(name).append(" - ").append(e.getValue()).append(" units\n");
                rank++;
                if (rank > 10) {
                    break; // limit to top 10 

                }
            }
            return out.toString();
        } catch (IOException ex) {
            return header("Most Frequently Ordered Products Report") + "Error reading order-products file: "
                    + ex.getMessage() + "\n";
        }
    }

    /**
     * Generates a revenue report showing total monetary value across all
     * completed orders
     *
     * @return formatted report displaying total revenue with currency
     * formatting
     */
    public String reportTotalRevenue() {
        try {
            File f = new File(ORDERS_CSV);
            if (!f.exists()) {
                return header("Total Revenue Report") + "Orders file not found: " + ORDERS_CSV + "\n";
            }
            Scanner scanman = new Scanner(f);
            if (scanman.hasNextLine()) {
                scanman.nextLine(); // skip CSV header row

            }
            double totalRevenue = 0.0; // accumulated revenue across all orders

            // Parse each order and sum the total price values
            while (scanman.hasNextLine()) {
                String line = scanman.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");

                // CSV format: order id, customer id, total price, timestamp, status
                if (parts.length < 3) {
                    continue; // skip malformed lines

                }
                try {
                    // The total price is in the 3rd column (index 2)
                    totalRevenue += Double.parseDouble(parts[2].trim());
                } catch (NumberFormatException ignored) {
                }
            }
            scanman.close();
            StringBuilder out = new StringBuilder();
            out.append(header("Total Revenue Report"));

            // Format revenue as currency with 2 decimal places for clarity
            out.append("Total Revenue: $").append(String.format("%.2f", totalRevenue)).append("\n");
            return out.toString();
        } catch (IOException ex) {
            return header("Total Revenue Report") + "Error reading orders: " + ex.getMessage() + "\n";
        }
    }

    /**
     * Helper method to generate a visually formatted header for reports.
     * Creates the title with an underline of equal signs for readability
     *
     * @param title the report title to format
     * @return formatted header string with title and matching-length underline
     */
    private String header(String title) {
        return title.replaceAll(".", "=") + "\n" + title + "\n" + title.replaceAll(".", "=") + "\n\n";
    }
}
