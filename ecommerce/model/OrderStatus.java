package ecommerce.model;

/**
 * OrderStatus helps track how far along an order is in the fulfillment process.
 *
 * Using enum to ensure only valid statuses are used.
 */
public enum OrderStatus {
    PROCESSED, SHIPPED, DELIVERED
}
