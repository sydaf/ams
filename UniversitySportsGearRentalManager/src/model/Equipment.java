package model;

/*
 * Demonstrates ENCAPSULATION:
 * - All data members are private
 * - Access is controlled via public getter methods
 */
public class Equipment {

  
    private int id;
    private String name;
    private String brand;
    private int totalQty;
    private int availableQty;

    
    public Equipment(int id, String name, String brand, int qty) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.totalQty = qty;
        this.availableQty = qty;
    }

  
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBrand() { return brand; }
    public int getTotalQty() { return totalQty; }
    public int getAvailableQty() { return availableQty; }

    
    public void rent(int qty) {
        availableQty -= qty;
    }

    public boolean returnItem(int qty) {
        if (availableQty + qty <= totalQty) {
            availableQty += qty;
            return true;
        }
        return false;
    }
}
