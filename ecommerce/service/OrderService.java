package ecommerce.service;

/*
 * OrderService
 * Responsible for persisting orders to CSV files and managing a simple
 * in-memory queue used for staged processing (e.g., PROCESSED -> SHIPPED
 * -> DELIVERED). Comments are kept brief and focused on intent and
 * assumptions so future maintainers can quickly understand behavior.
 */

import ecommerce.model.Order;
import ecommerce.model.OrderStatus;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class OrderService {

    public static Queue<Order> orderQueue = new LinkedList<>();
    // In-memory queue used for staged processing (ship/deliver transitions).
    // Orders are enqueued when persisted and repopulated from `orders.csv`
    // via `fillQueue()` so the app can resume processing across restarts.

    public String generateOrderId() {
        // Create a simple unique order id using the current epoch millis.
        // This is sufficient for a local demo; a production system would
        // need a stronger, collision-resistant approach.
        return "ORD" + System.currentTimeMillis();
    }

    public String getOrdersByUsername(String username) throws IOException {
        // Read `orders.csv` and `orderProducts.csv` to build a readable
        // listing for a single user's orders. Assumes CSV columns:
        // orderId,customerId,total,createdAt,status
        Scanner orderScanner = new Scanner(new File("ecommerce/data/orders.csv"));
        Scanner orderProductScanner = null;
        ProductService productService = new ProductService();
        StringBuilder ordersDisplay = new StringBuilder();
        orderScanner.nextLine(); // skip header
        while (orderScanner.hasNextLine()) {
            String orderLine = orderScanner.nextLine();
            String[] orderParts = orderLine.split(",");
            String orderId = orderParts[0];
            String customerId = orderParts[1];
            if (customerId.equals(username)) {
                ordersDisplay.append("Order ID: ").append(orderId).append("\n");
                ordersDisplay.append("Total: $").append(orderParts[2]).append("\n");
                ordersDisplay.append("Date: ").append(orderParts[3]).append("\n");
                ordersDisplay.append("Items:\n");
                // Find items for this order
                // `orderProducts.csv` rows: orderId, productId1, qty1, productId2, qty2, ...
                // We re-scan this file per order which is simple but not optimal
                // for very large datasets.
                orderProductScanner = new Scanner(new File("ecommerce/data/orderProducts.csv"));
                orderProductScanner.nextLine(); // skip header
                while (orderProductScanner.hasNextLine()) {
                    String itemLine = orderProductScanner.nextLine();
                    String[] itemParts = itemLine.split(",");
                    if (itemParts[0].equals(orderId)) {
                        for (int i = 1; i < itemParts.length; i += 2) {
                            ordersDisplay.append(" - Product Name: ").append(productService
                                    .getProductByID(Integer.parseInt(itemParts[i])).getName())
                                    .append(", Quantity: ").append(itemParts[i + 1]).append("\n");
                        }
                    }
                }
                ordersDisplay.append("Status: ").append(orderParts[4]).append("\n");
                ordersDisplay.append("----------------------------------------\n");
                orderProductScanner.close();
            }
        }
        orderScanner.close();
        return ordersDisplay.toString();
    }

    public ArrayList<String> getAllOrderIds() throws IOException {
        // Return a list of all order IDs present in `orders.csv`.
        Scanner orderScanner = new Scanner(new File("ecommerce/data/orders.csv"));
        orderScanner.nextLine(); // skip header
        ArrayList<String> ids = new ArrayList<>();
        while (orderScanner.hasNextLine()) {
            String orderLine = orderScanner.nextLine();
            String[] orderParts = orderLine.split(",");
            ids.add(orderParts[0]);
        }
        orderScanner.close();
        return ids;
    }

    public String getAllOrders() throws IOException {
        // Produce a human-readable dump of every order. Useful for admin
        // reporting. Note: this implementation is intentionally simple and
        // re-reads `orderProducts.csv` for each order rather than building
        // an index; acceptable for small CSVs but could be optimized.
        Scanner orderScanner = new Scanner(new File("ecommerce/data/orders.csv"));
        Scanner orderProductScanner = null;
        StringBuilder ordersDisplay = new StringBuilder();
        orderScanner.nextLine(); // skip header
        while (orderScanner.hasNextLine()) {
            String orderLine = orderScanner.nextLine();
            String[] orderParts = orderLine.split(",");
            String orderId = orderParts[0];
            ordersDisplay.append("Order ID: ").append(orderId).append("\n");
            ordersDisplay.append("Customer ID: ").append(orderParts[1]).append("\n");
            ordersDisplay.append("Total: $").append(orderParts[2]).append("\n");
            ordersDisplay.append("Date: ").append(orderParts[3]).append("\n");
            ordersDisplay.append("Items:\n");
            // Find items for this order
                orderProductScanner = new Scanner(new File("ecommerce/data/orderProducts.csv"));
            orderProductScanner.nextLine(); // skip header
            while (orderProductScanner.hasNextLine()) {
                String itemLine = orderProductScanner.nextLine();
                String[] itemParts = itemLine.split(",");
                if (itemParts[0].equals(orderId)) {
                    for (int i = 1; i < itemParts.length; i += 2) {
                        ordersDisplay.append(" - Product ID: ").append(itemParts[i])
                                .append(", Quantity: ").append(itemParts[i + 1]).append("\n");
                    }
                }
            }
            ordersDisplay.append("Status: ").append(orderParts[4]).append("\n");
            ordersDisplay.append("----------------------------------------\n");
        }
        orderScanner.close();
        orderProductScanner.close();
        return ordersDisplay.toString();
    }

    public void processOrder(Order order) throws IOException {
        // Persist the order row to `orders.csv` and its items to
        // `orderProducts.csv`, then enqueue it for staged processing.
        // Uses simple CSV append; no concurrency control is provided here.
        FileWriter orderWriter = new FileWriter("ecommerce/data/orders.csv", true);
        FileWriter orderProductsWriter = new FileWriter("ecommerce/data/orderProducts.csv", true);
        orderWriter.append("\n").append(order.getOrderId()).append(",")
                .append(order.getCustomerId()).append(",")
                .append(String.format("%.2f", order.getTotal())).append(",")
                .append(order.getCreatedAt()).append(",")
                .append(order.getStatus().toString());
        orderWriter.flush();
        orderWriter.close();
        StringJoiner joiner = new StringJoiner(",");
        joiner.add("\n" + order.getOrderId());
        for (var item : order.getItems()) {
            joiner.add(item.getProduct().getId())
                    .add(String.valueOf(item.getQuantity()));
        }
        orderProductsWriter.append(joiner.toString());
        orderProductsWriter.flush();
        orderProductsWriter.close();
        orderQueue.add(order); // add to processing queue
    }

    public void processNextOrder() throws IOException {
        Order order = orderQueue.poll();
        if (order == null) {
            return;
        }
        // Advance the in-memory order lifecycle and persist each status
        // change to disk so `fillQueue()` can rebuild state after a restart.
        switch (order.getStatus()) {
            case PROCESSED:
                order.setStatus(OrderStatus.SHIPPED);
                this.updateOrderStatusInFile(order);
                orderQueue.add(order);   // not done yet
                break;

            case SHIPPED:
                order.setStatus(OrderStatus.DELIVERED);
                this.updateOrderStatusInFile(order);
                // delivered orders are NOT re-added
                break;

            case DELIVERED:
                // should not normally be in queue
                break;
        }

    }

    public void fillQueue() throws IOException {
        // Restore non-DELIVERED orders into `orderQueue` by scanning
        // `orders.csv`. This allows the application to resume processing
        // where it left off between runs.
        Scanner orderScanner = new Scanner(new File("ecommerce/data/orders.csv"));
        orderScanner.nextLine(); // skip header
        while (orderScanner.hasNextLine()) {
            String orderLine = orderScanner.nextLine();
            String[] orderParts = orderLine.split(",");
            OrderStatus status = OrderStatus.valueOf(orderParts[4]);
            if (status != OrderStatus.DELIVERED) {
                OrderService orderService = new OrderService();
                Order order = orderService.createOrderFromFile(orderParts[0]);
                if (order != null) {
                    orderQueue.add(order);
                }
            }
        }
        orderScanner.close();
    }

    public Order createOrderFromFile(String orderId) throws IOException {
           // Construct a lightweight Order object using data from the CSV.
           // The returned Order intentionally omits item details in favor of
           // keeping the object small for status updates and queueing.
           Scanner orderScanner = new Scanner(new File("ecommerce/data/orders.csv"));
           orderScanner.nextLine(); // skip header
        while (orderScanner.hasNextLine()) {
            String orderLine = orderScanner.nextLine();
            String[] orderParts = orderLine.split(",");
            if (orderParts[0].equals(orderId)) {
                OrderStatus status = OrderStatus.valueOf(orderParts[4]);
                Order order = new Order(
                        orderParts[1], // customerId
                        new java.util.ArrayList<>(), // empty items; unnecessary for status updates
                        Double.parseDouble(orderParts[2]) // total
                );
                order.setOrderId(orderId);
                order.setStatus(status);
                orderScanner.close();
                return order;
            }
        }
        orderScanner.close();
        return null; // not found
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) throws IOException {
        Order order = createOrderFromFile(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            updateOrderStatusInFile(order);
        }
    }

    public void updateOrderStatusInFile(Order order) throws IOException {
        // Update the status field in `orders.csv` by writing changes to
        // a temporary file and then replacing the original. This provides
        // a simple atomic-like swap without requiring complex locking.
        File inputFile = new File("ecommerce/data/orders.csv");
        File tempFile = new File("ecommerce/data/orders_temp.csv");

        Scanner scanner = new Scanner(inputFile);
        FileWriter writer = new FileWriter(tempFile);

        String header = scanner.nextLine();
        writer.write(header);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            if (parts[0].equals(order.getOrderId())) {
                // Replace status field for the matching order row.
                parts[4] = order.getStatus().toString();
                String updatedLine = String.join(",", parts);
                writer.write("\n" + updatedLine);
            } else {
                writer.write("\n" + line);
            }
        }

        writer.flush();
        writer.close();
        scanner.close();

        // Replace original file with updated file. If either operation
        // fails we notify on stdout; this could be replaced with proper
        // logging in a production app.
        if (!inputFile.delete()) {
            System.out.println("Could not delete original orders file");
            return;
        }
        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename temp file to orders file");
        }
    }
}
