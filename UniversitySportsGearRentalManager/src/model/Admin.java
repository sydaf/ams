package model;

/*
 * Demonstrates INHERITANCE and POLYMORPHISM
 */
public class Admin extends User {

    public Admin(String userId, String name) {
        super(userId, name);
    }

    // Polymorphism: Admin has its own role behavior
    public String getRole() {
        return "Admin";
    }
}
