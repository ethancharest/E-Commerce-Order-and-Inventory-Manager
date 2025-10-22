package ecommerce.model;

/**
 * Defines the two primary user roles in the e-commerce system: ADMIN - can
 * manage products, view all orders CUSTOMER - can browse products, place orders
 *
 * Using enum to ensure only valid roles are assigned.
 */
public enum Role {
    ADMIN, CUSTOMER, NOTFOUND
}
