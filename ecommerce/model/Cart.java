package ecommerce.model;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private final Map<String, CartItem> items = new HashMap<>(); //key is product ID
    public void add(Product p, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be positive");
        CartItem existing = items.get(p.getId());
        if (existing == null) items.put(p.getId(), new CartItem(p, quantity));
        else existing.quantity += quantity;
    }
    public void remove(String productId){ items.remove(productId);} 
    public void setQuantity(String productId, int quantity) {
        if (quantity < 0) items.remove(productId);
        else {
            items.get(productId).quantity = quantity;
        }
    }

    public double subtotal() { 
        return items.values().stream().mapToDouble(CartItem::getTotalPrice).sum(); 
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public List<CartItem> list() {
        return new ArrayList<>(items.values());
    }

    public void clear() { 
        items.clear(); 
    }


}
