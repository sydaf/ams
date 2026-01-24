package ui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Equipment;

/*
 * ReturnEquipmentFrame
 * Student functionality:
 * - View equipment list
 * - Return rented equipment
 */
public class ReturnEquipmentFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;
    private DefaultTableModel tableModel;

    public ReturnEquipmentFrame(EquipmentManager equipmentManager,
                                RentalManager rentalManager) {

        this.equipmentManager = equipmentManager;
        this.rentalManager = rentalManager;

        setTitle("Return Equipment");
        setSize(700, 400);
        setLocationRelativeTo(null);

        // ---------- TABLE ----------
        String[] columns = {"Barcode", "Name", "Category", "Available Qty"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        refreshTable();

        JScrollPane scrollPane = new JScrollPane(table);

        // ---------- FORM ----------
        JTextField txtBarcode = new JTextField(10);
        JTextField txtQty = new JTextField(5);
        JButton btnReturn = new JButton("Return");

        JPanel formPanel = new JPanel();
        formPanel.add(new JLabel("Barcode"));
        formPanel.add(txtBarcode);
        formPanel.add(new JLabel("Quantity"));
        formPanel.add(txtQty);
        formPanel.add(btnReturn);

        // ---------- BUTTON ACTION ----------
        btnReturn.addActionListener(e -> {
            try {
                String barcode = txtBarcode.getText().trim();
                int qty = Integer.parseInt(txtQty.getText());

                Equipment equipment = equipmentManager.findByBarcode(barcode);

                if (equipment == null) {
                    JOptionPane.showMessageDialog(this,
                            "Equipment not found",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = rentalManager.returnEquipment(equipment, qty);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Equipment returned successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    refreshTable();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Return quantity exceeds total equipment available",
                            "Return Error",
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

    // Refresh table data
    private void refreshTable() {
        tableModel.setRowCount(0);

        for (Equipment e : equipmentManager.getAllEquipments()) {
            tableModel.addRow(new Object[]{
                    e.getBarcode(),
                    e.getName(),
                    e.getCategory(),
                    e.getAvailableQty()
            });
        }
    }
}
