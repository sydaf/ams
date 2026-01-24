package manager;

import model.Equipment;
import java.util.ArrayList;

/*
 * EquipmentManager class
 * Handles business logic related to equipment
 * Uses ArrayList (as required, no database)
 */
public class EquipmentManager {

    // Encapsulation: internal list is private
    private ArrayList<Equipment> equipmentList = new ArrayList<>();

    public boolean addEquipment(Equipment e) {
        if (e == null) return false;
        if (findByBarcode(e.getBarcode()) != null) return false; // enforce unique barcode
        equipmentList.add(e);
        return true;
    }

    public ArrayList<Equipment> getAllEquipments() {
        return equipmentList;
    }

    public Equipment findByBarcode(String barcode) {
        if (barcode == null) return null;
        String normalized = barcode.trim();
        for (Equipment e : equipmentList) {
            if (e.getBarcode().equalsIgnoreCase(normalized))
                return e;
        }
        return null;
    }
}
