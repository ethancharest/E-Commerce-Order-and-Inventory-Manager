package ecommerce.model;

/**
 * Represents an item in the shopping cart, including the product and its
 * quantity.
 */
public class CartItem {

    // product being purchased and its quantity
    private Product product;
    private int quantity;

    /**
     * Creates a new CartItem with the specified product and quantity.
     *
     * @param product The product being added to the cart
     * @param quantity The quantity of the product
     *
     * Quantity validiation is handled in the Cart class
     */
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    /**
     * Calculates and returns the total price for this cart item (product price
     * * quantity). Uses the product's getPrice() method rather than stored copy
     * to ensure any price updates are reflected.
     *
     * @return the total price for this product line
     */
    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

    // returns the quantity of this item
    public int getQuantity() {
        return quantity;
    }

    // updates the quantity of this item, would typically be called if user was
    // modifying their cart
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
