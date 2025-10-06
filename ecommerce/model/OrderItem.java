package ecommerce.model;

public class OrderItem {
    public String productId;
    public String productName;
    public double price; 
    public int productQuantity;
    public double lineTotal(){ return price * productQuantity; }
}
