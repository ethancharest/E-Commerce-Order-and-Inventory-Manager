package ecommerce.model;

import java.util.List;
import java.util.Date; //try LocalDateTime if Date doesn't work

import java.util.*;

public class Order {
    public final String orderId;
    public final String customerId;
    public final List<OrderItem> items;
    public final Date createdAt; 
    public double subtotal;
    public double tax;
    public double total;
    public OrderStatus status = OrderStatus.PLACED;
    public Order(String orderId, String customerId, List<OrderItem> items, Date createdAt, double subtotal, double tax, double total) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
        this.createdAt = createdAt;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
        //this.shipTo = address; //this doesnt seem to be working so commenting out for now
    }   



}
