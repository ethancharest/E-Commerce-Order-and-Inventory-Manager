package ecommerce.model;

public class User {
    private final String id;
    private String name;
    private Role role;
    private String username;
    private String password;
    private User(String id, String name, Role role, String username, String password) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.username = username;
        this.password = password;
    }
    @Override public String toString() { return name + " (" + role + ")";
    }
}
