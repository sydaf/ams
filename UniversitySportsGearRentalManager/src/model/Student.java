package model;

/*
 * Student class
 * Demonstrates INHERITANCE:
 * - Student inherits from User
 * Demonstrates POLYMORPHISM:
 * - Overrides getRole() method
 */
public class Student extends User {

    public Student(String userId, String name) {
        super(userId, name);
    }

    
    public String getRole() {
        return "Student";
    }
}
