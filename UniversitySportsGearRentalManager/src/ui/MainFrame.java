package ui;

import manager.EquipmentManager;
import manager.RentalManager;
import model.User;
import model.Student;
import ui.LoginFrame;
import javax.swing.*;
import java.awt.*;

/*
 * MainFrame
 * Displays different functions based on user role
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public MainFrame(User user,
                     EquipmentManager equipmentManager,
                     RentalManager rentalManager) {

        setTitle("University Sports Gear Rental Manager - " + user.getRole());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel(
                "Welcome " + user.getName() + " (" + user.getRole() + ")",
                JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        
        JButton btnHome = new JButton("Home");
        JButton btnEquipment = new JButton("Manage Equipment");
        JButton btnRent = new JButton("Rent Equipment");
        JButton btnHistory = new JButton("View Rental History");
        JButton btnReturn = new JButton("Return Equipment");



        JPanel panel = new JPanel();

        // Admin can manage equipment
        if (user.getRole().equals("Admin")) {
            panel.add(btnEquipment);
            panel.add(btnHistory);

        }

        // Student can rent equipment
        if (user.getRole().equals("Student")) {
            panel.add(btnRent);
            panel.add(btnReturn);
        }
        

        panel.add(btnHome);

        btnEquipment.addActionListener(e -> {
            new ManageEquipmentFrame(equipmentManager).setVisible(true);
        });

        btnRent.addActionListener(e -> {
            if (user instanceof model.Student) {
                model.Student student = (model.Student) user;

                new RentEquipmentFrame(
                        student,
                        equipmentManager,
                        rentalManager
                ).setVisible(true);

                dispose(); // optional: close MainFrame
            }
        });

        
        btnHistory.addActionListener(e -> {
            new RentalHistoryFrame(rentalManager).setVisible(true);
        });

        btnReturn.addActionListener(e -> {new ReturnEquipmentFrame(equipmentManager,rentalManager).setVisible(true);
        });
        
        btnHome.addActionListener(e -> {
            new LoginFrame(equipmentManager, rentalManager).setVisible(true);
            dispose(); 
        });


        add(title, BorderLayout.NORTH);
        add(panel, BorderLayout.CENTER);
    }
}
