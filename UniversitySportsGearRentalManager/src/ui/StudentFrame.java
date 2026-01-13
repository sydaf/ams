package ui;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Equipment;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/*
 * StudentFrame - Updated to match AMS specification
 * Student view with 2 tabs: Rental Dashboard and My Rentals
 */
public class StudentFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private User user;
    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;
    private JTabbedPane tabbedPane;
    private DefaultTableModel availableEquipmentTableModel;
    private DefaultTableModel myRentalsTableModel;

    public StudentFrame(User user, EquipmentManager equipmentManager, RentalManager rentalManager) {
        this.user = user;
        this.equipmentManager = equipmentManager;
        this.rentalManager = rentalManager;

        setTitle("AMS - Student Dashboard");
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
        
        // Tab 1: Rental Dashboard (Rentables)
        JPanel rentalDashboardPanel = createRentalDashboardPanel();
        tabbedPane.addTab("Rentables", rentalDashboardPanel);

        // Tab 2: My Rentals History
        JPanel myRentalsPanel = createMyRentalsPanel();
        tabbedPane.addTab("My Rentals", myRentalsPanel);

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

    private JPanel createRentalDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Available Equipment Table
        String[] columns = {"ID", "Name", "Brand", "Available Qty"};
        availableEquipmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(availableEquipmentTableModel);
        refreshAvailableEquipmentTable();
        JScrollPane scrollPane = new JScrollPane(table);

        // Rent Form
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtId = new JTextField(5);
        JTextField txtQty = new JTextField(5);
        JButton btnRent = new JButton("Rent");

        formPanel.add(new JLabel("Rent Item:"));
        formPanel.add(new JLabel("Id:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Qty:"));
        formPanel.add(txtQty);
        formPanel.add(btnRent);

        // Rent button action
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

                boolean success = rentalManager.rentEquipment(user.getName(), equipment, qty);

                if (success) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Equipment rented successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    refreshAvailableEquipmentTable();
                    refreshMyRentalsTable();

                    // Clear input
                    txtId.setText("");
                    txtQty.setText("");
                } else {
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

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshAvailableEquipmentTable() {
        availableEquipmentTableModel.setRowCount(0);
        for (Equipment e : equipmentManager.getAllEquipments()) {
            if (e.getAvailableQty() > 0) {
                availableEquipmentTableModel.addRow(new Object[]{
                        e.getId(),
                        e.getName(),
                        e.getBrand(),
                        e.getAvailableQty()
                });
            }
        }
    }

    private JPanel createMyRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // My Rentals Table
        String[] columns = {"Equipment", "Quantity", "Date"};
        myRentalsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable table = new JTable(myRentalsTableModel);
        refreshMyRentalsTable();
        JScrollPane scrollPane = new JScrollPane(table);

        // Return Form
        JPanel formPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField txtId = new JTextField(5);
        JTextField txtQty = new JTextField(5);
        JButton btnReturn = new JButton("Return Item");

        formPanel.add(new JLabel("Return Item"));
        formPanel.add(new JLabel("Id:"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Qty:"));
        formPanel.add(txtQty);
        formPanel.add(btnReturn);

        // Return button action
        btnReturn.addActionListener(e -> {
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

                boolean success = rentalManager.returnEquipment(equipment, qty);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Equipment returned successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    refreshAvailableEquipmentTable();
                    refreshMyRentalsTable();

                    // Clear input
                    txtId.setText("");
                    txtQty.setText("");
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

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(formPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshMyRentalsTable() {
        myRentalsTableModel.setRowCount(0);
        // Show rentals for current user
        rentalManager.getRentalHistory().stream()
                .filter(r -> r.getUserName().equals(user.getName()))
                .forEach(r -> {
                    myRentalsTableModel.addRow(new Object[]{
                            r.getEquipmentName(),
                            r.getQuantity(),
                            r.getRentalDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    });
                });
    }
}
