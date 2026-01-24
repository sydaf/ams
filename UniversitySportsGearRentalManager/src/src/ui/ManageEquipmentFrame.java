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
        String[] columns = {"Barcode", "Name", "Category", "Available Qty", "Total Qty", "Status"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);

        // ---------- FORM ----------
        JTextField txtBarcode = new JTextField(10);
        JTextField txtName = new JTextField(10);
        JTextField txtCategory = new JTextField(10);
        JTextField txtQty = new JTextField(5);

        JButton btnAdd = new JButton("Add Equipment");

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Barcode"));
        formPanel.add(txtBarcode);
        formPanel.add(new JLabel("Name"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Category"));
        formPanel.add(txtCategory);
        formPanel.add(new JLabel("Qty"));
        formPanel.add(txtQty);
        formPanel.add(btnAdd);

        // ---------- BUTTON ACTION ----------
        btnAdd.addActionListener(e -> {
            try {
                String barcode = txtBarcode.getText().trim();
                String name = txtName.getText();
                String category = txtCategory.getText();
                int qty = Integer.parseInt(txtQty.getText());

                Equipment equipment = new Equipment(barcode, name, category, qty);
                boolean added = equipmentManager.addEquipment(equipment);
                if (!added) {
                    JOptionPane.showMessageDialog(this,
                            "Barcode already exists",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                tableModel.addRow(new Object[]{
                        barcode, name, category, qty, qty, equipment.getStatus().name()
                });

                // Clear input
                txtBarcode.setText("");
                txtName.setText("");
                txtCategory.setText("");
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
