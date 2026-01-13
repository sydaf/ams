package ui;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Equipment;
import model.Student;
import model.User;
import ui.MainFrame;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/*
 * RentEquipmentFrame
 * Student functionality:
 * - View equipment list
 * - Rent equipment
 */

public class RentEquipmentFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;
    private DefaultTableModel tableModel;
    private String userName;


    public RentEquipmentFrame(String userName, EquipmentManager equipmentManager, RentalManager rentalManager) {
        this.userName = userName;
    	this.equipmentManager = equipmentManager;
        this.rentalManager = rentalManager;

        setTitle("Rent Equipment");
        setSize(700, 400);
        setLocationRelativeTo(null);

        // ---------- TABLE ----------
        String[] columns = {"ID", "Name", "Brand", "Available Qty"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);

        // ---------- FORM ----------
        JTextField txtId = new JTextField(5);
        JTextField txtQty = new JTextField(5);

        JButton btnRent = new JButton("Rent");

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Equipment ID"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Quantity"));
        formPanel.add(txtQty);
        formPanel.add(btnRent);

        // ---------- BUTTON ACTION ----------
        btnRent.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                int qty = Integer.parseInt(txtQty.getText());

                Equipment equipment = equipmentManager.findById(id);

                if (equipment == null) {
                    JOptionPane.showMessageDialog(this,
                            "Equipment not found",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = rentalManager.rentEquipment(userName, equipment, qty);

                if (success) {

                    // Green check-mark success popup
                    JOptionPane.showMessageDialog(
                            this,
                            "Equipment rented successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    // Go back to Student Main Page
                    User student = new Student("S001", userName);

                    new MainFrame(
                            student,
                            equipmentManager,
                            rentalManager
                    ).setVisible(true);

                    dispose(); // close RentEquipmentFrame
                }
                else {
                    JOptionPane.showMessageDialog(this,
                            "Not enough quantity available",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid numbers",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }

    // Refresh table to reflect latest data
    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Equipment e : equipmentManager.getAllEquipments()) {
            tableModel.addRow(new Object[]{
                    e.getId(),
                    e.getName(),
                    e.getBrand(),
                    e.getAvailableQty()
            });
        }
    }
}
