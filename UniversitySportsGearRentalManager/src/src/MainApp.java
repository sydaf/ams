import manager.EquipmentManager;
import manager.RentalManager;
import ui.LoginFrame;

/*
 * Application entry point
 */
public class MainApp {
    public static void main(String[] args) {

        EquipmentManager equipmentManager = new EquipmentManager();
        RentalManager rentalManager = new RentalManager();

        new LoginFrame(equipmentManager, rentalManager).setVisible(true);
    }
}
