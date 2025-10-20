package ecommerce.model;
/**
 * Represents a user in the e-commerce system
 * Each User has an assigned Role (ADMIN or CUSTOMER) @see Role.java
 * which determines what actions they can perform
 */
public class User {

    // User fields
    private String id;
    private String name;
    private Role role;
    private String username;
    private String password;

    /**
     * Constructs a new User with the specified details.
     * 
     * @param id        Unique user identifier (could be auto generated)
     * @param name      User display name
     * @param role      The role assigned to this user (ADMIN or CUSTOMER)
     * @param username  Login  
     * @param password  Password 
     * 
     * This constructor does not handle any validation
     */

    public User(String id, String name, Role role, String username, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.username = username;
        this.password = password;
    }

    // Returns the user's role with their name 
    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
}
