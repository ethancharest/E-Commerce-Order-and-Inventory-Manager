package ecommerce.model;

public class CartItem {
    public Product product;
    public int quantity;
    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }
    public double getTotalPrice() {
        return product.getPrice() * quantity; //set to getPrice(). Chat was tryna tell me to use product.price * quantity. test later
    }
}
