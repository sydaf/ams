package model;

/*
 * Student class
 * Demonstrates INHERITANCE:
 * - Student inherits from User
 * Demonstrates POLYMORPHISM:
 * - Overrides getRole() method
 * Demonstrates ENCAPSULATION:
 * - Student-specific attributes are private
 */
public class Student extends User {

    // Student basic information
    private String studentClass;
    private int year;

    // Constructor including basic student info
    public Student(String userId, String name, String studentClass, int year) {
        super(userId, name);
        this.studentClass = studentClass;
        this.year = year;
    }

    // Encapsulation via getters
    public String getStudentClass() {
        return studentClass;
    }

    public int getYear() {
        return year;
    }

    // Polymorphism
    @Override
    public String getRole() {
        return "Student";
    }
}
