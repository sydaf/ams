package manager;

import model.Equipment;
import model.Rental;
import java.util.ArrayList;
import java.util.List;

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

        // Record rental transaction - Constructor now handles 3-day policy
        Rental rental = new Rental(userName, e.getBarcode(), e.getName(), qty);
        rentalHistory.add(rental);

        return true;
    }

    public ArrayList<Rental> getRentalHistory() {
        return rentalHistory;
    }

    /**
     * Updated returnEquipment:
     * Now finds the correct Rental record to close the lifecycle status.
     */
    public boolean returnEquipment(Equipment e, int qty) {
        if (qty <= 0 || e == null) return false;
        
        for (Rental r : rentalHistory) {
            if (r.getEquipmentBarcode().equals(e.getBarcode()) &&
                !r.isFullyReturned()) {
                
                e.returnItem(qty); // Update physical stock
                r.addReturnedQty(qty); // Update specific transaction
                return true;
            }
        }
        return false;
    }

    /**
     * Policy Engine Check:
     * Returns a list of all rentals that are currently LATE.
     */
    public List<Rental> getOverdueRentals() {
        return rentalHistory.stream()
                .filter(r -> r.getStatus() == Equipment.Status.LATE)
                .toList();
    }

    /*
     * Returns rentals for a specific student
     */
    public List<Rental> getRentalsByStudent(String studentName) {
        return rentalHistory.stream()
                .filter(r -> r.getUserName().equals(studentName))
                .toList();
    }
}