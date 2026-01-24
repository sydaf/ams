package model;

import java.time.LocalDateTime;

/*
 * Rental class
 * Represents ONE rental transaction
 * Demonstrates ENCAPSULATION
 */
public class Rental {

    private String userName;
    private String equipmentBarcode;
    private String equipmentName;
    private int quantity;
    private LocalDateTime rentalDate; 
    private LocalDateTime dueDate;
    private boolean isReturned;

    public Rental(String userName, String equipmentBarcode, String equipmentName, int quantity) {
        this.userName = userName;
        this.equipmentBarcode = equipmentBarcode;
        this.equipmentName = equipmentName;
        this.quantity = quantity;
        this.rentalDate = LocalDateTime.now(); 
        this.dueDate = this.rentalDate.plusSeconds(15);
        this.isReturned = false;
    }

    public Equipment.Status getStatus() {
        if (isReturned) {
            return Equipment.Status.CLOSED;
        }
         
        if (LocalDateTime.now().isAfter(dueDate)) {
            return Equipment.Status.LATE;
        }
        
        return Equipment.Status.ACTIVE;
    }

    public void markAsReturned() {
        this.isReturned = true;
    } 
    
    public String getUserName() { return userName; }
    public String getEquipmentBarcode() { return equipmentBarcode; }
    public String getEquipmentName() { return equipmentName; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getRentalDate() { return rentalDate; }
    public LocalDateTime getDueDate() { return dueDate; }
    public boolean isReturned() { return isReturned; } 
}