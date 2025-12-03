package ecommerce.model;

import ecommerce.service.OrderService;
import java.util.ArrayList;
import java.util.Calendar; //try LocalDateTime if Date doesn't work

public class Order {

    /**
     * Represents a customer order. including items purchased, totals, status,
     * and shipping address. After checkout, the cart is converted into an
     * order.
     */
    private String orderId;
    private String customerId;
    private ArrayList<CartItem> items;
    private String createdAt;
    private double total;
    private OrderStatus status; // e.g., PROCESSED, SHIPPED, DELIVERED
    private OrderService orderService;
    private Calendar calendar = java.util.Calendar.getInstance();

    /**
     * Constructs a new Order object.
     *
     * @param orderId Unique identifier for the order
     * @param customerId The ID (username) of the customer who placed it
     * @param items The products and quantities included
     * @param createdAt The timestamp of order creation
     * @param total The grand total (subtotal + tax)
     *
     * Orders are set to PROCESSED status upon creation
     */
    public Order(String customerId, ArrayList<CartItem> items, double total) {
        orderService = new OrderService();
        this.orderId = orderService.generateOrderId();
        this.customerId = customerId;
        this.items = items;
        this.createdAt = calendar.get(Calendar.MONTH + 1) + "/"
                + calendar.get(Calendar.DAY_OF_MONTH) + "/"
                + calendar.get(Calendar.YEAR) + " "
                + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND);
        this.total = total;
        this.status = OrderStatus.PROCESSED; // default
    }

    // Getters and setters
    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public ArrayList<CartItem> getItems() {
        return items;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public double getTotal() {
        return total;
    }

    public OrderStatus getStatus() {
        return status;
    }

}
