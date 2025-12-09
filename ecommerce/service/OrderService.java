package ecommerce.service;

/**
 * OrderService
 * Handles persisting orders (CSV-backed) and an in-memory queue
 * 
 * queue used to advance order lifecycle states (PROCESSED -> SHIPPED -> DELIVERED)
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

    /**
     * In-memory queue for staged processing of orders.
     * Populated from `orders.csv` by {@link #fillQueue()} so processing
     * can resume after a restart
     */
    public static Queue<Order> orderQueue = new LinkedList<>();

    public String generateOrderId() {
        /**
         * Generate a simple unique order id using system time millis
         */
        return "ORD" + System.currentTimeMillis();
    }

    /**
     * Return a listing of orders for `username` by
     * reading `orders.csv` and `orderProducts.csv`
     * 
     * Assumes CSV columns: orderId,customerId,total,createdAt,status
     * This method is simple and not optimized for large files
     */
    public String getOrdersByUsername(String username) throws IOException {
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
                
                // Find items for this order (scan `orderProducts.csv`).
                // Format: orderId, productId1, qty1, productId2, qty2, ...
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

    /**
     * Return all order IDs found in `orders.csv`.
     */
    public ArrayList<String> getAllOrderIds() throws IOException {
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

    /**
     * Produce a readable dump of all orders. Re-scans `orderProducts.csv`
     */
    public String getAllOrders() throws IOException {
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
        /**
         * Persist the order to disk (append to CSVs) and enqueue it for
         * staged processing. Uses simple append semantics; there is no
         * concurrency control in this demo code
         */
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
        /**
         * Advance the next queued order through its lifecycle and persist
         * status changes so the queue can be reconstructed later
         */
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
        /**
         * Load non-DELIVERED orders from `orders.csv` into the in-memory
         * queue so processing can resume between runs
         */
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
           /**
            * Build a  Order object from a CSV row.
            * Items are omitted because callers often only need id/status/total for queueing and status updates
            */
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
        /**
         * update an order's status in `orders.csv` by writing
         * a temporary file and replacing the original
         */
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

        // Replace original file with updated file
        if (!inputFile.delete()) {
            System.out.println("Could not delete original orders file");
            return;
        }
        if (!tempFile.renameTo(inputFile)) {
            System.out.println("Could not rename temp file to orders file");
        }
    }
}
