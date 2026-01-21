package manager;

import model.Equipment;
import model.Rental;

import java.util.ArrayList;

/*
 * RentalManager
 * Handles rental logic AND rental history
 */
public class RentalManager {

    // Stores rental history (no database required)
    private ArrayList<Rental> rentalHistory = new ArrayList<>();

    public boolean rentEquipment(String userName, Equipment e, int qty) {
        if (e == null) return false;
        if (qty <= 0) return false;
        if (e.getAvailableQty() < qty) return false;

        e.rent(qty);

        // Record rental transaction
        Rental rental = new Rental(userName, e.getBarcode(), e.getName(), qty);
        rentalHistory.add(rental);

        return true;
    }

    public ArrayList<Rental> getRentalHistory() {
        return rentalHistory;
    }
    
 
    public boolean returnEquipment(Equipment e, int qty) {
        if (qty <= 0) return false;

        return e.returnItem(qty); 
    }

    /*
     * Returns rentals for a specific student
     */
    public java.util.List<Rental> getRentalsByStudent(String studentName) {
        return rentalHistory.stream()
                .filter(r -> r.getUserName().equals(studentName))
                .toList();
    }


}
