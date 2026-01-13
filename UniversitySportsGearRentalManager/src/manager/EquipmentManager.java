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

    public void addEquipment(Equipment e) {
        equipmentList.add(e);
    }

    public ArrayList<Equipment> getAllEquipments() {
        return equipmentList;
    }

    public Equipment findById(int id) {
        for (Equipment e : equipmentList) {
            if (e.getId() == id)
                return e;
        }
        return null;
    }
}
