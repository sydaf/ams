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

    public Rental(String userName, String equipmentBarcode, String equipmentName, int quantity) {
        this.userName = userName;
        this.equipmentBarcode = equipmentBarcode;
        this.equipmentName = equipmentName;
        this.quantity = quantity;
        this.rentalDate = LocalDateTime.now();
    }

    public String getUserName() { return userName; }
    public String getEquipmentBarcode() { return equipmentBarcode; }
    public String getEquipmentName() { return equipmentName; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getRentalDate() { return rentalDate; }
}
