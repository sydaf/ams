package ui;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Equipment;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/*
 * StudentFrame
 * Student dashboard with:
 * - Rent equipment
 * - View own rental history
 */
public class StudentFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private Student student;
    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;

    private DefaultTableModel availableTableModel;
    private DefaultTableModel myRentalsTableModel;

    public StudentFrame(Student student,
                        EquipmentManager equipmentManager,
                        RentalManager rentalManager) {

        this.student = student;
        this.equipmentManager = equipmentManager;
        this.rentalManager = rentalManager;

        setTitle("Student Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top Header - User Profile
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Navigation
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Rent Equipment", createRentPanel());
        tabs.add("My Rentals", createMyRentalsPanel());

        mainPanel.add(tabs, BorderLayout.CENTER);

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
        
        userInfoPanel.add(new JLabel("User ID: " + student.getUserId()));
        userInfoPanel.add(new JLabel("Name: " + student.getName()));
        userInfoPanel.add(new JLabel("Class: " + student.getStudentClass()));
        userInfoPanel.add(new JLabel("Year: " + student.getYear()));

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

    // ================= RENT PANEL =================
    private JPanel createRentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Name", "Category", "Available"};
        availableTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(availableTableModel);
        refreshAvailableTable();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel();
        JTextField txtBarcode = new JTextField(10);
        JTextField txtQty = new JTextField(5);
        JButton btnRent = new JButton("Rent");

        form.add(new JLabel("Barcode"));
        form.add(txtBarcode);
        form.add(new JLabel("Quantity"));
        form.add(txtQty);
        form.add(btnRent);

        btnRent.addActionListener(e -> {
            try {
                String barcode = txtBarcode.getText().trim();
                int qty = Integer.parseInt(txtQty.getText());

                Equipment eq = equipmentManager.findByBarcode(barcode);

                if (eq == null) {
                    JOptionPane.showMessageDialog(this,
                            "Equipment not found",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = rentalManager.rentEquipment(
                        student.getName(),
                        eq,
                        qty
                );

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Equipment rented successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    refreshAvailableTable();
                    refreshMyRentalsTable();

                    txtBarcode.setText("");
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

        panel.add(form, BorderLayout.SOUTH);
        return panel;
    }

    // ================= MY RENTALS PANEL =================
    private JPanel createMyRentalsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Equipment", "Quantity", "Rental Date"};
        myRentalsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(myRentalsTableModel);
        refreshMyRentalsTable();

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ================= REFRESH METHODS =================
    private void refreshAvailableTable() {
        availableTableModel.setRowCount(0);

        for (Equipment e : equipmentManager.getAllEquipments()) {
            if (e.getAvailableQty() > 0) {
                availableTableModel.addRow(new Object[]{
                        e.getBarcode(),
                        e.getName(),
                        e.getCategory(),
                        e.getAvailableQty()
                });
            }
        }
    }

    private void refreshMyRentalsTable() {
        myRentalsTableModel.setRowCount(0);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        rentalManager.getRentalHistory().stream()
                .filter(r -> r.getUserName().equals(student.getName()))
                .forEach(r -> myRentalsTableModel.addRow(new Object[]{
                        r.getEquipmentName(),
                        r.getQuantity(),
                        r.getRentalDate().format(formatter)
                }));
    }
}
