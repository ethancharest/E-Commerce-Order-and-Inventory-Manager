package ecommerce.model;

/**
 * Represents an item within a customer order, including the product and
 * quantity ordered. Each OrderItem links to a Product to access current pricing
 * and details.
 *
 * If product prices change after an order is placed, the OrderItem will reflect
 * the updated price
 */
public class OrderItem {

    private Product product;
    /**
     * The product being ordered. Direct reference to Product to access current
     * price/info
     */

    private int orderQuantity; // How many units of this product were ordered

    /**
     * Calculates the total price for this order item
     *
     * @return the line total (product price * quantity)
     */
    public double lineTotal() {
        return product.getPrice() * orderQuantity;
    }
}
