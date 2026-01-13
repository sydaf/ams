package ui;

import manager.EquipmentManager;
import model.Equipment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/*
 * ManageEquipmentFrame
 * Admin functionality:
 * - View equipment (JTable)
 * - Add new equipment
 */
public class ManageEquipmentFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private EquipmentManager equipmentManager;
    private DefaultTableModel tableModel;

    public ManageEquipmentFrame(EquipmentManager equipmentManager) {
        this.equipmentManager = equipmentManager;

        setTitle("Manage Equipment");
        setSize(700, 400);
        setLocationRelativeTo(null);

        // ---------- TABLE ----------
        String[] columns = {"ID", "Name", "Brand", "Available Qty", "Total Qty"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        // ---------- FORM ----------
        JTextField txtId = new JTextField(5);
        JTextField txtName = new JTextField(10);
        JTextField txtBrand = new JTextField(10);
        JTextField txtQty = new JTextField(5);

        JButton btnAdd = new JButton("Add Equipment");

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("ID"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Name"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Brand"));
        formPanel.add(txtBrand);
        formPanel.add(new JLabel("Qty"));
        formPanel.add(txtQty);
        formPanel.add(btnAdd);

        // ---------- BUTTON ACTION ----------
        btnAdd.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                String brand = txtBrand.getText();
                int qty = Integer.parseInt(txtQty.getText());

                Equipment equipment = new Equipment(id, name, brand, qty);
                equipmentManager.addEquipment(equipment);

                tableModel.addRow(new Object[]{
                        id, name, brand, qty, qty
                });

                // Clear input
                txtId.setText("");
                txtName.setText("");
                txtBrand.setText("");
                txtQty.setText("");

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid data",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(scrollPane, BorderLayout.CENTER);
        add(formPanel, BorderLayout.SOUTH);
    }
}
