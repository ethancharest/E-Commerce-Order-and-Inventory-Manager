package ecommerce.model;

import ecommerce.service.ProductService;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a shopping cart containing products selected by the user. This
 * class is responsible for managing cart items, calculating subtotals, and
 * maintaining a consistent view of the cart's contents.
 *
 * Cart exists in memory only and is cleared when customer session is cleared.
 */
public class Cart {

    // Map of product ID to CartItem
    // Using a map avoids duplicate items and allows for easy quantity updates
    private Map<String, CartItem> items = new HashMap<>(); // key is product ID

    /**
     * Adds a product to the cart or increases product quantity.
     *
     * @param p The product to add
     * @param quantity The quantity to add
     * @throws IllegalArgumentException if quantity is less than 1, ensures the
     * cart state remains valid
     */
    public void add(Product p, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartItem existing = items.get(p.getId());
        // if the product is not already in the cart, add it as a new item
        if (existing == null) {
            items.put(p.getId(), new CartItem(p, quantity));
            // else if the product is already in the cart, increase the quantity
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
    }

    /**
     * Removes a product from the cart by its product ID. Used when a customer
     * deleted an item from their cart.
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
     * @param quantity The new quantity
     *
     * If quantity is set to zero or less, the item is removed from the cart
     */
    public void setCartQuantity(String productId, int quantity) {
        if (quantity < 0) {
            items.remove(productId);
        } else {
            items.get(productId).setQuantity(quantity);
        }
    } // do we need this method? seems like it does the same thing as add()

    /*
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
    public String display() {
        StringBuilder productsInCart = new StringBuilder();
        for (CartItem item : items.values()) {
            productsInCart.append(item.getProduct().getName())
                    .append(" - Quantity: ")
                    .append(item.getQuantity())
                    .append(", Total Price: $")
                    .append(String.format("%.2f", item.getTotalPrice()))
                    .append("\n");
        }
        productsInCart.append("Cart Subtotal: $").append(String.format("%.2f", subtotal())).append("\n");
        return productsInCart.toString();
    }

    public void updateProductsStock(ProductService productService) throws IOException {
        for (CartItem item : items.values()) {
            productService.updateProduct(Integer.parseInt(item.getProduct().getId()),
                    item.getProduct().getName(),
                    item.getProduct().getCategory(),
                    item.getProduct().getPrice(),
                    item.getProduct().getAvailableStock() - item.getQuantity());
        }
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
