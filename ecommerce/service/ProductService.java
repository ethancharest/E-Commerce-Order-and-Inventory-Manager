package ecommerce.service;
//this class should handle product-related operations for ADMINS

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProductService {

    private File productFile;
    private FileWriter writer;
    private Scanner reader;

    public ProductService() throws IOException {
        productFile = new File("ecommerce/data/products.csv");
        writer = new FileWriter(productFile, true);
    }

    private int generateProductID() throws IOException {
        //maybe we could later implement a way for deleted IDs to be reused
        //we could put the deleted IDs in a separate file and read from there first
        int maxID = 0;
        ArrayList<String[]> products = getAllProducts();
        for (String[] product : products) {
            int id = Integer.parseInt(product[0]);
            if (id > maxID) {
                maxID = id;
            }
        }
        return maxID + 1;
    }

    private ArrayList<String[]> getAllProducts() throws IOException {
        ArrayList<String[]> products = new ArrayList<>();
        reader = new Scanner(productFile);
        reader.nextLine(); //skip header
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] parts = line.split(",");
            products.add(parts);
        }
        reader.close();
        return products;
    }

    public void addProduct(String name, String category, double price, int stock) throws IOException {
        String id = String.valueOf(generateProductID());
        String productEntry = "\n" + id + "," + name + "," + category + "," + price + "," + stock;
        writer.write(productEntry);
        writer.flush();
        System.out.println("Product added successfully: " + name);
    }

    public void updateProduct(int productId, String name, String category, double price, int stock) throws IOException {
        ArrayList<String[]> products = getAllProducts();
        FileWriter tempWriter = new FileWriter("ecommerce/data/temp_products.csv");
        tempWriter.write("id, name, category, price, stock"); //header
        for (String[] product : products) {
            int id = Integer.parseInt(product[0]);
            if (id == productId) {
                String updatedEntry = "\n" + productId + "," + name + "," + category + "," + price + "," + stock;
                tempWriter.write(updatedEntry);
            } else {
                String existingEntry = "\n" + String.join(",", product);
                tempWriter.write(existingEntry);
            }
        }
        tempWriter.close();
        writer.close();
        File originalFile = new File("ecommerce/data/products.csv");
        File tempFile = new File("ecommerce/data/temp_products.csv");
        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
            writer = new FileWriter(productFile, true);
            System.out.println("Product updated successfully: ID " + productId);
        } else {
            System.out.println("Failed to update product: ID " + productId);
        }
    }

    public void deleteProduct(int productId) throws IOException {
        ArrayList<String[]> products = getAllProducts();
        FileWriter tempWriter = new FileWriter("ecommerce/data/temp_products.csv");
        tempWriter.write("id, name, category, price, stock"); //header
        for (String[] product : products) {
            int id = Integer.parseInt(product[0]);
            if (id != productId) {
                String existingEntry = "\n" + String.join(",", product);
                tempWriter.write(existingEntry);
            }
        }
        tempWriter.close();
        writer.close();
        File originalFile = new File("ecommerce/data/products.csv");
        File tempFile = new File("ecommerce/data/temp_products.csv");
        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);
            writer = new FileWriter(productFile, true);
            System.out.println("Product deleted successfully: ID " + productId);
        } else {
            System.out.println("Failed to delete product: ID " + productId);
        }
    }

    public String displayProducts() throws IOException {
        StringBuilder productList = new StringBuilder();
        ArrayList<String[]> products = getAllProducts();
        for (String[] product : products) {
            productList.append("ID: ").append(product[0])
                    .append(" | Name: ").append(product[1])
                    .append(" | Category: ").append(product[2])
                    .append(" | Price: $").append(product[3])
                    .append(" | Stock: ").append(product[4]).append("\n");
        }
        return productList.toString();
    }
}
