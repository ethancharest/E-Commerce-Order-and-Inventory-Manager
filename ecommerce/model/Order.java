package ecommerce.model;

import java.util.ArrayList;
import java.util.Date; //try LocalDateTime if Date doesn't work

public class Order {
    /**
     * Represents a customer order. including items purchased, totals, status, and
     * shipping address.
     * After checkout, the cart is converted into an order.
     */
    private String orderId;
    private String customerId;
    private ArrayList<OrderItem> items;
    private Date createdAt;
    private double subtotal;
    private double tax;
    private double total;
    private OrderStatus status; // e.g., PROCESSED, SHIPPED, DELIVERED
    private Address shipTo;

    /**
     * Constructs a new Order object.
     *
     * @param orderId    Unique identifier for the order
     * @param customerId The ID of the customer who placed it
     * @param items      The products and quantities included
     * @param createdAt  The timestamp of order creation
     * @param subtotal   The sum of all item prices before tax
     * @param tax        The calculated tax based on the shipping address
     * @param total      The grand total (subtotal + tax)
     * @param address    Shipping destination
     * 
     *                   Orders are set to PROCESSED status upon creation
     */

    public Order(String orderId, String customerId, ArrayList<OrderItem> items, Date createdAt, double subtotal,
            double tax, double total, Address address) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.createdAt = createdAt;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.status = OrderStatus.PROCESSED; // default
        this.shipTo = address;
    }

}
