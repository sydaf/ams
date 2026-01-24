package ui;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Equipment;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.Rental;

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
    private static boolean itemOverdue;
    private JPanel alertPanel; 

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
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		// Create the container for the top section
		JPanel topContainer = new JPanel(new BorderLayout());
		
		// Initialize Alert Panel (Hidden by default)
		 alertPanel = createAlertPanel();
		alertPanel.setVisible(false); 
		
		// Add Alert and Header to the container
		topContainer.add(alertPanel, BorderLayout.NORTH);
		topContainer.add(createHeaderPanel(), BorderLayout.CENTER);
		
		// Put the whole container at the top of the main panel
		mainPanel.add(topContainer, BorderLayout.NORTH);
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Rent Equipment", createRentPanel());
		tabs.add("My Rentals", createMyRentalsPanel());
		mainPanel.add(tabs, BorderLayout.CENTER);
		
		add(mainPanel);
		
		// Run the first refresh to check if the alert should show up immediately
		refreshMyRentalsTable();
		}
    
    public void initAutoRefresh() {
        // 60,000 milliseconds = 1 minute
        Timer timer = new Timer(20000, e -> refreshMyRentalsTable());
        timer.start();
    }
    
    private JPanel createAlertPanel() {
        JPanel alertPanel = new JPanel();
        alertPanel.setBackground(new Color(255, 230, 230));  
        alertPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.RED));
        
        JLabel alertLabel = new JLabel("⚠️ ATTENTION: You have items overdue, please return promptly or disciplinary actions will be taken");
        alertLabel.setForeground(Color.RED);
        alertLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        alertPanel.add(alertLabel);
        return alertPanel;
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

        // UPDATED: Added columns for Due Date and Status
        String[] cols = {"ID", "Name", "Category", "Quantity", "Rental Date", "Due Date", "Status"};
        myRentalsTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(myRentalsTableModel);

	     // 1. Apply the color logic to the Status (6) columns
	     RentalStatusRenderer renderer = new RentalStatusRenderer(); 
	     table.getColumnModel().getColumn(6).setCellRenderer(renderer);
	
	     // 2. Initial data load
	     refreshMyRentalsTable();
	
	     // 3. Set up Auto-Refresh (every 20 seconds)
	     // This ensures that if a deadline passes while the app is open, it turns red.
	     javax.swing.Timer autoRefreshTimer = new javax.swing.Timer(20000, e -> {
	         refreshMyRentalsTable();
	     });
	     autoRefreshTimer.start();
	
	     panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // ---------- RETURN FORM ----------
        JPanel formPanel = new JPanel();
        JTextField txtBarcode = new JTextField(10);
        JTextField txtQty = new JTextField(5);
        JButton btnReturn = new JButton("Return");

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

                Rental activeRental = null;
                for (Rental r : rentalManager.getRentalHistory()) {
                    if (r.getUserName().equals(student.getName()) &&
                        r.getEquipmentBarcode().equals(barcode) &&
                        !r.isReturned()) {
                        activeRental = r;
                        break;
                    }
                }

                if (activeRental == null) {
                    JOptionPane.showMessageDialog(this,
                            "No active rental found for this equipment",
                            "Return Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (qty > activeRental.getQuantity()) {
                    JOptionPane.showMessageDialog(this,
                            "Cannot return more than rented. You rented " + 
                            activeRental.getQuantity() + " item(s).",
                            "Return Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                boolean success = rentalManager.returnEquipment(equipment, qty);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Equipment returned successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);

                    refreshMyRentalsTable();
                    refreshAvailableTable();

                    txtBarcode.setText("");
                    txtQty.setText("");
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Return failed. Please check the barcode and quantity.",
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

        panel.add(formPanel, BorderLayout.SOUTH);
        
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
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();
         
        boolean foundOverdue = false;

        for (Rental r : rentalManager.getRentalHistory()) {
            if (r.getUserName().equals(student.getName())) {
                
                // 1. Determine Late Status
                // Check if status is explicitly LATE or if the clock has passed the due date
                boolean isLate = (r.getStatus().toString().equals("LATE") || now.isAfter(r.getDueDate())) 
                                 && !r.getStatus().toString().equals("CLOSED");
                
                if (isLate) {
                    foundOverdue = true;
                }

                // 2. Prepare Row Data
                Equipment equipment = equipmentManager.findByBarcode(r.getEquipmentBarcode());
                String category = (equipment != null) ? equipment.getCategory() : "N/A";
                
                myRentalsTableModel.addRow(new Object[]{
                        r.getEquipmentBarcode(),
                        r.getEquipmentName(),
                        category,
                        r.getQuantity(),
                        r.getRentalDate().format(formatter),
                        r.getDueDate().format(formatter),
                        r.getStatus().toString()
                });
            }
        }

        // 3. Update the UI State
        itemOverdue = foundOverdue;
        
        // Only trigger layout changes if the visibility actually needs to flip
        if (alertPanel.isVisible() != itemOverdue) {
            alertPanel.setVisible(itemOverdue);
            
            // This forces the "topContainer" to recalculate sizes 
            // so the header panel moves down to make room for the alert
            if (alertPanel.getParent() != null) {
                alertPanel.getParent().revalidate();
                alertPanel.getParent().repaint();
            }
        }
    }
}
