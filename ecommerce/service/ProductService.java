package ecommerce.service;
//this class should handle product-related operations for ADMINS

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class ProductService {

    // CSV file where all products data is stored
    private File productFile;

    // Writer used for appending new products to the CSV
    private FileWriter writer;

    // Reader used when loading products from the CSV
    private Scanner reader;

    public ProductService() throws IOException {
        // Itialize the file pointing to the products CSV
        productFile = new File("ecommerce/data/products.csv");

        // Open writer in append mode so we can add products without overwriting the file 
        writer = new FileWriter(productFile, true);
    }

    /**
     * Generates the next product ID Currently it: -reads all existing products
     * -finds the highest ID -returns maxID + 1
     *
     * Note: IDs are monotonically increasing, deleted IDs are not reused
     *
     * @return next product ID
     * @throws IOException
     */
    private int generateProductID() throws IOException {
        //maybe we could later implement a way for deleted IDs to be reused
        //we could put the deleted IDs in a separate file and read from there first
        int maxID = 0;

        // Load all products so we can inspect their IDs
        ArrayList<String[]> products = getAllProducts();

        // reader = new Scanner("ecommerce/data/deletedID.csv");
        // writer = new FileWriter("ecommerce/data/deletedID.csv", true);
        // FileWriter tempWriter = new FileWriter("ecommerce/data/deletedID.csv");
        // for (String[] product : products) {
        //     int id = Integer.parseInt(product[0]);
        //     if (id != productId) {
        //         String existingEntry = "\n" + String.join(",", product);
        //         tempWriter.write(existingEntry);
        //     }
        // }
        // tempWriter.close();
        // writer.close();
        // File originalFile = new File("ecommerce/data/products.csv");
        // File tempFile = new File("ecommerce/data/temp_products.csv");
        // if (originalFile.delete()) {
        //     tempFile.renameTo(originalFile);
        //     writer = new FileWriter(productFile, true);
        //     System.out.println("Product deleted successfully: ID " + productId);
        // } else {
        //     System.out.println("Failed to delete product: ID " + productId);
        // }
        // if (reader.hasNextLine()) {
        //     String line = reader.nextLine();
        //     maxID = Integer.parseInt(line);
        //     reader.close();
        //     return maxID;
        // }
        // Find the maximum ID currently in the products list
        for (String[] product : products) {
            int id = Integer.parseInt(product[0]);
            if (id > maxID) {
                maxID = id;
            }
        }

        // Next avaliable ID is the largest existing ID + 1
        return maxID + 1;
    }

    /**
     * Reads all products from the products CSV and returns them as a list of
     * string arrays Each String[] corresponds to one row: [id, name, category,
     * price, stock]
     *
     * @return list of all products
     * @throws IOException
     */
    private ArrayList<String[]> getAllProducts() throws IOException {
        ArrayList<String[]> products = new ArrayList<>();

        // Open scanner on the products file 
        reader = new Scanner(productFile);

        // Skip header line
        reader.nextLine();

        // Read each remaining line and split on commas 
        while (reader.hasNextLine()) {
            String line = reader.nextLine();
            String[] parts = line.split(",");
            products.add(parts);
        }

        // Close reader 
        reader.close();

        return products;
    }

    /**
     * Appends a new product to the CSV file ID is auto-generated, everything
     * else comes from the caller
     *
     * @param name
     * @param category
     * @param price
     * @param stock
     * @throws IOException
     */
    public void addProduct(String name, String category, double price, int stock) throws IOException {
        // Generate a unique product ID
        String id = String.valueOf(generateProductID());

        // Build CSV line representing the new product
        String productEntry = "\n" + id + "," + name + "," + category + "," + price + "," + stock;

        // Append to file and flush to ensure it is written immediately
        writer.write(productEntry);
        writer.flush();

        System.out.println("Product added successfully: " + name);
    }

    /**
     * Updates an existing product identified by productId Implementation
     * detail: - Read all products into memory - Write them into a temporary
     * file, replacing the row that matches productId - Replace the original
     * file with the temporary file
     *
     * @param productId
     * @param name
     * @param category
     * @param price
     * @param stock
     * @throws IOException
     */
    public void updateProduct(int productId, String name, String category, double price, int stock) throws IOException {
        ArrayList<String[]> products = getAllProducts();

        // Temporary file that will hold updated contents
        FileWriter tempWriter = new FileWriter("ecommerce/data/temp_products.csv");

        // Write CSV header first 
        tempWriter.write("id, name, category, price, stock"); //header

        // Rewrite each product row, updating only the one with the matching ID
        for (String[] product : products) {
            int id = Integer.parseInt(product[0]);
            if (id == productId) {
                //Build updated line
                String updatedEntry = "\n" + productId + "," + name + "," + category + "," + price + "," + stock;
                tempWriter.write(updatedEntry);
            } else {
                // Keep existing row unchanged
                String existingEntry = "\n" + String.join(",", product);
                tempWriter.write(existingEntry);
            }
        }
        tempWriter.close();
        writer.close(); // Close the original writer before replacing the file

        // Swap temporary file in place of the original products file
        File originalFile = new File("ecommerce/data/products.csv");
        File tempFile = new File("ecommerce/data/temp_products.csv");

        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);

            // Reopen writer in append mode on the new products file
            writer = new FileWriter(productFile, true);
            System.out.println("Product updated successfully: ID " + productId);
        } else {
            System.out.println("Failed to update product: ID " + productId);
        }
    }

    /**
     * Deletes a product by ID Similar pattern to updateProduct(): - Copy all
     * rows except the one to delete into a temp file - Log the deleted ID -
     * Replace original CSV with temp file
     *
     * @param productId
     * @throws IOException
     */
    public void deleteProduct(int productId) throws IOException {
        ArrayList<String[]> products = getAllProducts();

        // Temp file to hold remaining products
        FileWriter tempWriter = new FileWriter("ecommerce/data/temp_products.csv");
        tempWriter.write("id, name, category, price, stock"); //header

        // Copy every product except the one being deleted 
        for (String[] product : products) {
            int id = Integer.parseInt(product[0]);
            if (id != productId) {
                String existingEntry = "\n" + String.join(",", product);
                tempWriter.write(existingEntry);
            } else {
                // Log deleted ID for potential future reuse
                FileWriter logWriter = new FileWriter("ecommerce/data/deletedID.csv", true);
                logWriter.write(productId + "\n");
                logWriter.close();
            }
        }
        tempWriter.close();
        writer.close();
        File originalFile = new File("ecommerce/data/products.csv");
        File tempFile = new File("ecommerce/data/temp_products.csv");
        if (originalFile.delete()) {
            tempFile.renameTo(originalFile);

            // Open writer again for appending 
            writer = new FileWriter(productFile, true);
            System.out.println("Product deleted successfully: ID " + productId);
        } else {
            System.out.println("Failed to delete product: ID " + productId);
        }
    }

    /**
     * Returns a human-readable string listing all products in the system
     * formatted for display in the Admin GUI
     *
     * @return
     * @throws IOException
     */
    public String displayProducts() throws IOException {
        StringBuilder productList = new StringBuilder();
        ArrayList<String[]> products = getAllProducts();

        // Build one line per product using labeled feilds 
        for (String[] product : products) {
            productList.append("ID: ").append(product[0])
                    .append(" | Name: ").append(product[1])
                    .append(" | Category: ").append(product[2])
                    .append(" | Price: $").append(String.format("%.2f", Double.valueOf(product[3]))) //format price to 2 decimal places
                    .append(" | Stock: ").append(product[4]).append("\n");
        }
        return productList.toString();
    }
}
