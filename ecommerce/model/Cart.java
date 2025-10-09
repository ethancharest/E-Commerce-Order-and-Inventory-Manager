package ecommerce.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Cart {

    private Map<String, CartItem> items = new HashMap<>(); //key is product ID

    public void add(Product p, int quantity) {
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        CartItem existing = items.get(p.getId());
        if (existing == null) {
            items.put(p.getId(), new CartItem(p, quantity));
        } else {
            existing.setQuantity(existing.getQuantity() + quantity);
        }
    }

    public void remove(String productId) {
        items.remove(productId);
    }

    public void setCartQuantity(String productId, int quantity) {
        if (quantity < 0) {
            items.remove(productId);
        } else {
            items.get(productId).setQuantity(quantity);
        }
    }

    public double subtotal() {
        double subtotal = 0.0;
        for (CartItem item : items.values()) {
            subtotal += item.getTotalPrice();
        }
        return subtotal;
    }

    public ArrayList<CartItem> list() {
        return new ArrayList<>(items.values());
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
    }
}
