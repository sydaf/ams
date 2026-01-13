package ui;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Equipment;
import model.Rental;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/*
 * AdminFrame - Updated to match AMS specification
 * Admin view with 2 tabs: Manage Items and History/Audit
 */
public class AdminFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private User user;
    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;
    private JTabbedPane tabbedPane;
    private DefaultTableModel equipmentTableModel;
    private DefaultTableModel historyTableModel;

    public AdminFrame(User user, EquipmentManager equipmentManager, RentalManager rentalManager) {
        this.user = user;
        this.equipmentManager = equipmentManager;
        this.rentalManager = rentalManager;

        setTitle("AMS - Admin Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top Header - User Profile
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Navigation
        tabbedPane = new JTabbedPane();
        
        // Tab 1: Manage Items
        JPanel manageItemsPanel = createManageItemsPanel();
        tabbedPane.addTab("Manage Items", manageItemsPanel);

        // Tab 2: History / Audit
        JPanel historyPanel = createHistoryPanel();
        tabbedPane.addTab("History / Audit", historyPanel);

        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        headerPanel.setBackground(new Color(240, 240, 240));

        // Left side - User info
        JPanel userInfoPanel = new JPanel(new GridLayout(2, 2, 10, 5));
        userInfoPanel.setOpaque(false);
        
        userInfoPanel.add(new JLabel("User ID: " + user.getUserId()));
        userInfoPanel.add(new JLabel("Name: " + user.getName()));
        userInfoPanel.add(new JLabel("Class: N/A")); // Placeholder for now
        userInfoPanel.add(new JLabel("Year: N/A")); // Placeholder for now

        // Right side - Logout button
        JButton btnLogout = new JButton("Log out");
        btnLogout.addActionListener(e -> {
            new LoginFrame(equipmentManager, rentalManager).setVisible(true);
            dispose();
        });

        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createManageItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table
        String[] columns = {"ID", "Name", "Brand", "Available Qty", "Total Qty"};
        equipmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(equipmentTableModel);
        refreshEquipmentTable();
        JScrollPane scrollPane = new JScrollPane(table);

        // Form panel
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtId = new JTextField(5);
        JTextField txtName = new JTextField(10);
        JTextField txtBrand = new JTextField(10);
        JTextField txtQty = new JTextField(5);

        JButton btnAdd = new JButton("Add New Item");

        formPanel.add(new JLabel("ID:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Brand:"));
        formPanel.add(txtBrand);
        formPanel.add(new JLabel("Qty:"));
        formPanel.add(txtQty);
        formPanel.add(btnAdd);

        // Add button action
        btnAdd.addActionListener(e -> {
            try {
                int id = Integer.parseInt(txtId.getText());
                String name = txtName.getText();
                String brand = txtBrand.getText();
                int qty = Integer.parseInt(txtQty.getText());

                Equipment equipment = new Equipment(id, name, brand, qty);
                equipmentManager.addEquipment(equipment);

                refreshEquipmentTable();

                // Clear input
                txtId.setText("");
                txtName.setText("");
                txtBrand.setText("");
                txtQty.setText("");

                JOptionPane.showMessageDialog(this,
                        "Equipment added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Please enter valid data",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshEquipmentTable() {
        equipmentTableModel.setRowCount(0);
        for (Equipment e : equipmentManager.getAllEquipments()) {
            equipmentTableModel.addRow(new Object[]{
                    e.getId(),
                    e.getName(),
                    e.getBrand(),
                    e.getAvailableQty(),
                    e.getTotalQty()
            });
        }
    }

    private JPanel createHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Table
        String[] columns = {"User", "Equipment", "Quantity", "Date"};
        historyTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(historyTableModel);
        refreshHistoryTable();
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshHistoryTable() {
        historyTableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (Rental r : rentalManager.getRentalHistory()) {
            historyTableModel.addRow(new Object[]{
                    r.getUserName(),
                    r.getEquipmentName(),
                    r.getQuantity(),
                    r.getRentalDate().format(formatter)
            });
        }
    }
}
