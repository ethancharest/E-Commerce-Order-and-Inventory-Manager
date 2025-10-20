package ecommerce.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shopping cart containing products selected by the user.
 * This class is responsible for managing cart items, calculating subtotals,
 * and maintaining a consistent view of the cart's contents.
 * 
 * Cart exists in memory only and is cleared when customer serssion is cleared.
 */

public class Cart {

    // Map of product ID to CartItem
    // Using a map avoids duplicate items and allows for easy quantity updates
    private Map<String, CartItem> items = new HashMap<>(); // key is product ID

    /**
     * Adds a product to the cart or increases product quantity.
     * 
     * @param p        The product to add
     * @param quantity The quantity to add
     * @throws IllegalArgumentException if quantity is less than 1, ensures the cate
     *                                  state remains valid
     */
    public void add(Product p, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        // If the product is already in the cart, increase the quantity
        CartItem existing = items.get(p.getId());
        if (existing == null) {
            items.put(p.getId(), new CartItem(p, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
    }

    /**
     * Removes a product from the cart by its product ID.
     * Used when a customer deleted an item from their cart.
     * 
     * @param productId The ID of the product to remove
     */
    public void remove(String productId) {
        items.remove(productId);
    }

    /**
     * Sets the quantity of a specific product in the cart.
     * 
     * @param productId The ID of the product to update
     * @param quantity  The new quantity
     *
     * If quantity is set to zero or less, the item is removed from
     * the cart
     */
    public void setCartQuantity(String productId, int quantity) {
        if (quantity < 0) {
            items.remove(productId);
        } else {
            items.get(productId).setQuantity(quantity);
        }
    }

    /*
     * Calculates and returns the subtotal (before tax) for all items in the cart
     */
    public double subtotal() {
        double subtotal = 0.0;
        for (CartItem item : items.values()) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    /**
     * Returns a list of all CartItems in the cart.
     */
    public ArrayList<CartItem> list() {
        return new ArrayList<>(items.values());
    }

    // Returns true if the cart is empty
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Clears all items from the cart
    public void clear() {
        items.clear();
    }
}
