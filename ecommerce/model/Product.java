package ecommerce.model;

/**
 * Represents a product available for purchase in the e-commerce system Each
 * product has an ID, name, category, price, and available stock quantity.
 */
public class Product {

    private String id; // unique identifier
    private String name; // Readable product name
    private String category; // grouping (e.g., electronics, clothing)
    private double price; // current price
    private int availableStock; // how many units are in stock

    /**
     * Constructs a new Product with the specified details.
     *
     * @param id Unique product identifier
     * @param name Product name
     * @param category Product category
     * @param price Current price of the product
     * @param availableStock Quantity available in stock
     */
    public Product(String id, String name, String category, double price, int availableStock) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.availableStock = availableStock;
    }

    // Getters and setters for product fields
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public int getAvailableStock() {
        return availableStock;
    }

}
