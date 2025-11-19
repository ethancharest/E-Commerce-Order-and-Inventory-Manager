package ecommerce.service;

import ecommerce.model.Role;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
// This class handles all user authentication 
// It loads credintials from a CSV file and stores them in memory
// LoginFrame calls authenticate() to determine whether a user exists and what role they have 

public class AuthService {
    // Scanner used to read the CSV file
    private Scanner fileReader;     

    // Maps username --> password (simple in-memory storage for project scope)
    private HashMap<String, String> logins;

    // Maps username --> role (role stored as string, converted to enum on retrieval)
    private HashMap<String, String> roles;

    public AuthService() throws FileNotFoundException {
        // Open the credintials CSV file and read in the data 
        // Expected format: username,password,role
        this.fileReader = new Scanner(new File("ecommerce/data/logins.csv"));

        this.logins = new HashMap<>();
        this.roles = new HashMap<>();

        fileReader.nextLine(); // Skip header line

        // Load each user record and populate the HashMaps 
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();

            // Split by comma, allows additional whitespace 
            String[] parts = line.split(",\\s*");

            // Defensive trim (avoids whitespace bugs)
            for (String part : parts) {
                part = part.trim();
            }

            //parts[0] = username, parts[1] = password, parts[2] = role
            logins.put(parts[0], parts[1]);
            roles.put(parts[0], parts[2]);
        }
        //close file reader 
        fileReader.close();
    }
    /**
    * Validates a login 
    * Checks:
    * 1. Username exists
    * 2. Password matches
    * Returns:
    * -The Role enum if found
    * -Role.NOTFOUND if not found or invalid
    * @param username
    * @param password
    * @return
    */
    public Role authenticate(String username, String password) {

        // Check if username exists in system
        if (logins.containsKey(username)) {

            // Check if password matches
            if (logins.get(username).equals(password)) {

                // Return the user's role as enum
                return Role.valueOf(roles.get(username));
            }
        }

        // If not found OR password mismatch --> authentication failed 
        return Role.NOTFOUND;

    }
}
