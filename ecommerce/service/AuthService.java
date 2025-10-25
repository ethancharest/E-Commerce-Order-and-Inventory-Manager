package ecommerce.service;

import ecommerce.model.Role;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
//this class should handle authentication of users based on a CSV file containing usernames, passwords, and roles

public class AuthService {

    private Scanner fileReader;
    private HashMap<String, String> logins;
    private HashMap<String, String> roles;

    public AuthService() throws FileNotFoundException {
        this.fileReader = new Scanner(new File("ecommerce/data/logins.csv"));
        this.logins = new HashMap<>();
        this.roles = new HashMap<>();
        fileReader.nextLine(); // Skip header line
        while (fileReader.hasNextLine()) {
            String line = fileReader.nextLine();
            String[] parts = line.split(",\\s*");
            for (String part : parts) {
                part = part.trim();
            }
            logins.put(parts[0], parts[1]);
            roles.put(parts[0], parts[2]);
        }
        fileReader.close();
    }

    public Role authenticate(String username, String password) {
        if (logins.containsKey(username)) {
            if (logins.get(username).equals(password)) {
                return Role.valueOf(roles.get(username));
            }
        }
        return Role.NOTFOUND;

    }
}
