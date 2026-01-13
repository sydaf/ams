package model;

/*
 * Abstract User class
 * Demonstrates ABSTRACTION
 */
public abstract class User {

    // Protected allows access by subclasses 
    protected String userId;
    protected String name;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }

    /*
     * POLYMORPHISM:
     * - Each subclass must provide its own implementation
     */
    public abstract String getRole();
}
