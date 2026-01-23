package model;

/*
 * Demonstrates ENCAPSULATION:
 * - All data members are private
 * - Access is controlled via public getter methods
 */
public class Equipment {

    public enum Status {
        AVAILABLE,
        UNAVAILABLE,
        ACTIVE,
        LATE,
        CLOSED
    }

    // Primary identifier (Barcode tracking)
    private String barcode;
    private String name;
    private String category;
    private int totalQty;
    private int availableQty;

    
    public Equipment(String barcode, String name, String category, int totalQty) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category is required");
        }
        if (totalQty < 0) {
            throw new IllegalArgumentException("Total quantity cannot be negative");
        }

        this.barcode = barcode.trim();
        this.name = name.trim();
        this.category = category.trim();
        this.totalQty = totalQty;
        this.availableQty = totalQty;
    }

  
    public String getBarcode() { return barcode; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public int getTotalQty() { return totalQty; }
    public int getAvailableQty() { return availableQty; }
    public Status getStatus() { return availableQty > 0 ? Status.AVAILABLE : Status.UNAVAILABLE; }

    
    public void rent(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Quantity must be > 0");
        }
        if (qty > availableQty) {
            throw new IllegalArgumentException("Not enough available quantity");
        }
        availableQty -= qty;
    }

    public boolean returnItem(int qty) {
        if (qty <= 0) return false;
        if (availableQty + qty <= totalQty) {
            availableQty += qty;
            return true;
        }
        return false;
    }
}
