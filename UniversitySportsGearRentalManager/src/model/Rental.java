package model;

import java.time.LocalDateTime;

/*
 * Rental class
 * Represents ONE rental transaction
 * Demonstrates ENCAPSULATION
 */
public class Rental {

    private String userName;
    private String equipmentName;
    private int quantity;
    private LocalDateTime rentalDate;

    public Rental(String userName, String equipmentName, int quantity) {
        this.userName = userName;
        this.equipmentName = equipmentName;
        this.quantity = quantity;
        this.rentalDate = LocalDateTime.now();
    }

    public String getUserName() { return userName; }
    public String getEquipmentName() { return equipmentName; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getRentalDate() { return rentalDate; }
}
