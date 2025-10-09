package ecommerce.model;

public class OrderItem {

    private Product product;
    private int orderQuantity;

    public double lineTotal() {
        return product.getPrice() * orderQuantity;
    }
}
