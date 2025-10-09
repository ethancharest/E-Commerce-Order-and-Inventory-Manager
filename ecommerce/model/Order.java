package ecommerce.model;

import java.util.ArrayList;
import java.util.Date; //try LocalDateTime if Date doesn't work

public class Order {

    private String orderId;
    private String customerId;
    private ArrayList<OrderItem> items;
    private Date createdAt;
    private double subtotal;
    private double tax;
    private double total;
    private OrderStatus status;
    private Address shipTo;

    public Order(String orderId, String customerId, ArrayList<OrderItem> items, Date createdAt, double subtotal, double tax, double total, Address address) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.createdAt = createdAt;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        this.status = OrderStatus.PROCESSED;
        this.shipTo = address;
    }

}
