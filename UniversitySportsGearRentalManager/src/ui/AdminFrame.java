package ui;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Equipment;
import model.Rental;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;
import java.util.TreeSet;

/*
 * AdminFrame
 * Admin dashboard with:
 * - Manage Items (CRUD via popup)
 * - History / Audit (read-only)
 */
public class AdminFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private User user;
    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;

    private DefaultTableModel equipmentTableModel;
    private DefaultTableModel historyTableModel;
    private JTable equipmentTable;
    private JComboBox<String> cbCategoryFilter;
    private JComboBox<String> cbHistoryCategoryFilter;


    public AdminFrame(User user,
                      EquipmentManager equipmentManager,
                      RentalManager rentalManager) {

        this.user = user;
        this.equipmentManager = equipmentManager;
        this.rentalManager = rentalManager;

        setTitle("AMS - Admin Dashboard");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        add(createHeaderPanel(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Manage Items", createManageItemsPanel());
        tabs.add("History / Audit", createHistoryPanel());

        add(tabs, BorderLayout.CENTER);
    } 
    
    private void initAutoRefresh() {
        // 60,000 milliseconds = 1 minute
        Timer timer = new Timer(1000, e -> refreshHistoryTable());
        timer.start();
    }

    // ================= HEADER =================
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblUser = new JLabel("Admin: " + user.getName());
        JButton btnLogout = new JButton("Logout");

        btnLogout.addActionListener(e -> {
            new LoginFrame(equipmentManager, rentalManager).setVisible(true);
            dispose();
        });

        panel.add(lblUser, BorderLayout.WEST);
        panel.add(btnLogout, BorderLayout.EAST);
        return panel;
    }
    
 // ================= EXPORT EQUIPMENT CSV =================
    private void exportEquipmentToCSV() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Equipment List");
        chooser.setSelectedFile(new java.io.File("equipment_list.csv"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (java.io.PrintWriter pw =
                     new java.io.PrintWriter(chooser.getSelectedFile())) {

            pw.println("Barcode,Name,Category,Available,Total,Status");

            for (Equipment e : equipmentManager.getAllEquipments()) {
                pw.println(
                        e.getBarcode() + "," +
                        e.getName() + "," +
                        e.getCategory() + "," +
                        e.getAvailableQty() + "," +
                        e.getTotalQty() + "," +
                        e.getStatus()
                );
            }

            JOptionPane.showMessageDialog(this,
                    "Equipment CSV exported successfully");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage());
        }
    }
    
 // ================= EXPORT HISTORY CSV =================
    private void exportHistoryToCSV() {

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Rental History");
        chooser.setSelectedFile(new java.io.File("rental_history.csv"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        try (java.io.PrintWriter pw =
                     new java.io.PrintWriter(chooser.getSelectedFile())) {

            pw.println("Barcode (ID),Name,Category,Qty,Pending Qty,Rented Date,Return Date,Renter,Status");

            for (Rental r : rentalManager.getRentalHistory()) {

                Equipment equipment = equipmentManager.findByBarcode(r.getEquipmentBarcode());
                String category = (equipment != null) ? equipment.getCategory() : "N/A";
                
                pw.println(
                        r.getEquipmentBarcode() + "," +
                        r.getEquipmentName() + "," +
                        category + "," +
                        r.getQuantity() + "," +
                        r.getRemainingQty() + "," +
                        r.getRentalDate().format(formatter) + "," +
                        r.getDueDate().format(formatter) + "," +
                        r.getUserName() + "," +
                        r.getStatus()
                );
            }

            JOptionPane.showMessageDialog(this,
                    "History CSV exported successfully");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Export failed: " + ex.getMessage());
        }
    }

    private void applyCategoryFilter() {

        Object selected = cbCategoryFilter.getSelectedItem();

        // 🔹 Guard clause (IMPORTANT)
        if (selected == null) {
            return;
        }

        String selectedCategory = selected.toString();

        equipmentTableModel.setRowCount(0);

        for (Equipment e : equipmentManager.getAllEquipments()) {

            if (selectedCategory.equals("All Categories") ||
                e.getCategory().equals(selectedCategory)) {

                equipmentTableModel.addRow(new Object[]{
                        e.getBarcode(),
                        e.getName(),
                        e.getCategory(),
                        e.getAvailableQty(),
                        e.getTotalQty(),
                        e.getStatus().name()
                });
            }
        }
    }




    // ================= MANAGE ITEMS =================
    private JPanel createManageItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbCategoryFilter = new JComboBox<>();
        cbCategoryFilter.addItem("All Categories");

        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(cbCategoryFilter);

        panel.add(filterPanel, BorderLayout.NORTH);
        cbCategoryFilter.addActionListener(e -> applyCategoryFilter());

        String[] cols = {"Barcode (ID)", "Name", "Category", "Availability"};

        equipmentTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        equipmentTable = new JTable(equipmentTableModel);
        refreshEquipmentTable();

        panel.add(new JScrollPane(equipmentTable), BorderLayout.CENTER);

        JButton btnAdd = new JButton("Add Item");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");
        JButton btnExport = new JButton("Export CSV");

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnAdd);
        btnPanel.add(btnEdit);
        btnPanel.add(btnDelete);
        btnPanel.add(btnExport);

        // ADD
        btnAdd.addActionListener(e -> showAddItemDialog());

        // EDIT
        btnEdit.addActionListener(e -> {
            int row = equipmentTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an item to edit");
                return;
            }

            String barcode = equipmentTableModel.getValueAt(row, 0).toString();
            Equipment eq = equipmentManager.findByBarcode(barcode);
            if (eq != null) showEditItemDialog(eq);
        });

        // DELETE 
        btnDelete.addActionListener(e -> {
            int row = equipmentTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select an item to delete");
                return;
            }

            String barcode = equipmentTableModel.getValueAt(row, 0).toString();
            Equipment eq = equipmentManager.findByBarcode(barcode);

            if (eq.getAvailableQty() != eq.getTotalQty()) {
                JOptionPane.showMessageDialog(this,
                        "Cannot delete item with active rentals");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Delete this equipment?",
                    "Confirm",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                equipmentManager.getAllEquipments().remove(eq);
                refreshEquipmentTable();
            }
        });
        
        //Export CSV
        btnExport.addActionListener(e -> exportEquipmentToCSV());


        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

 // ================= ADD POPUP =================
    private void showAddItemDialog() {

        JDialog d = new JDialog(this, "Add Equipment", true);
        d.setSize(380, 300);
        d.setLocationRelativeTo(this);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField txtBarcode = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtQty = new JTextField();

        String[] categories = {
                "Sports Equipment",
                "Laboratory Equipment",
                "Event Equipment",
                "Multimedia Equipment"
        };
        JComboBox<String> cbCategory = new JComboBox<>(categories);

        form.add(new JLabel("Barcode"));
        form.add(txtBarcode);
        form.add(new JLabel("Name"));
        form.add(txtName);
        form.add(new JLabel("Category"));
        form.add(cbCategory);
        form.add(new JLabel("Total Qty"));
        form.add(txtQty);

        JButton btnSave = new JButton("Save");

        btnSave.addActionListener(e -> {
            try {
                String barcode = txtBarcode.getText().trim();
                String name = txtName.getText().trim();
                String category = cbCategory.getSelectedItem().toString();
                int qty = Integer.parseInt(txtQty.getText());

                Equipment eq = new Equipment(barcode, name, category, qty);

                if (!equipmentManager.addEquipment(eq)) {
                    JOptionPane.showMessageDialog(d,
                            "Barcode already exists");
                    return;
                }

                refreshEquipmentTable();
                d.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage());
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnSave);

        outer.add(form, BorderLayout.CENTER);
        outer.add(btnPanel, BorderLayout.SOUTH);
        d.add(outer);

        d.setVisible(true);
    }


 // ================= EDIT POPUP =================
    private void showEditItemDialog(Equipment eq) {

        JDialog d = new JDialog(this, "Edit Equipment", true);
        d.setSize(360, 260);
        d.setLocationRelativeTo(this);

        JPanel outer = new JPanel(new BorderLayout());
        outer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField txtName = new JTextField(eq.getName());
        JTextField txtTotal = new JTextField(String.valueOf(eq.getTotalQty()));

        String[] categories = {
                "Sports Equipment",
                "Laboratory Equipment",
                "Event Equipment",
                "Multimedia Equipment"
        };
        JComboBox<String> cbCategory = new JComboBox<>(categories);
        cbCategory.setSelectedItem(eq.getCategory()); // preselect existing

        form.add(new JLabel("Name"));
        form.add(txtName);
        form.add(new JLabel("Category"));
        form.add(cbCategory);
        form.add(new JLabel("Total Qty"));
        form.add(txtTotal);

        JButton btnSave = new JButton("Save");

        btnSave.addActionListener(e -> {
            try {
                int newTotal = Integer.parseInt(txtTotal.getText());

                if (newTotal < eq.getAvailableQty()) {
                    JOptionPane.showMessageDialog(d,
                            "Total cannot be less than available stock");
                    return;
                }

                String category = cbCategory.getSelectedItem().toString();

                eq.updateDetails(
                        txtName.getText().trim(),
                        category,
                        newTotal
                );

                refreshEquipmentTable();
                d.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(d, ex.getMessage());
            }
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnSave);

        outer.add(form, BorderLayout.CENTER);
        outer.add(btnPanel, BorderLayout.SOUTH);
        d.add(outer);

        d.setVisible(true);
    }

    private void applyHistoryCategoryFilter() {
        Object selected = cbHistoryCategoryFilter.getSelectedItem();

        // Prevent NullPointerException
        if (selected == null) return;

        String selectedCategory = selected.toString();
        historyTableModel.setRowCount(0);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();

        for (Rental r : rentalManager.getRentalHistory()) {
            Equipment eq = equipmentManager.findByBarcode(r.getEquipmentBarcode());
            if (eq == null) continue;

            // Same filter logic
            if (selectedCategory.equals("All Categories") || eq.getCategory().equals(selectedCategory)) {
                
                // Calculate timeLeft logic
                String timeLeft;
                if (r.getStatus().toString().equals("CLOSED")) {
                    timeLeft = "--";
                } else {
                    java.time.Duration duration = java.time.Duration.between(now, r.getDueDate());

                    if (duration.isNegative()) {
                        timeLeft = "OVERDUE";
                    } else {
                        long days = duration.toDays();
                        long hours = duration.toHoursPart();
                        long mins = duration.toMinutesPart();
                        long secs = duration.toSecondsPart();
                        timeLeft = String.format("%dd %02dh %02dm %02ds", days, hours, mins, secs);
                    }
                }

                // Add row with all 7 columns in the correct order
                historyTableModel.addRow(new Object[]{
                        r.getEquipmentBarcode(),           
                        r.getEquipmentName(),       
                        r.getQuantity(),            
                        r.getRentalDate().format(fmt),  
                        r.getDueDate().format(fmt),   
                        r.getUserName(),      
                        timeLeft,                   
                        r.getStatus().name()        
                });
            }
        }
    }


 // ================= HISTORY =================
    private JPanel createHistoryPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // FILTER PANEL (TOP)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        cbHistoryCategoryFilter = new JComboBox<>();
        cbHistoryCategoryFilter.addItem("All Categories");

        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(cbHistoryCategoryFilter);

        panel.add(filterPanel, BorderLayout.NORTH);

        // TABLE
     // Define the headers clearly
        String[] cols = {"Barcode (ID)", "Name", "Category", "Qty", "Pending Qty", "Rented Date", "Return Date", "Renter", "Due In", "Status"};      

        // Initialize the model with these columns
        historyTableModel = new DefaultTableModel(cols, 0);

        // Initialize the table WITH the model
        JTable table = new JTable(historyTableModel);

        // IMPORTANT: Tell the table to automatically create columns from the model
        table.setAutoCreateColumnsFromModel(true);

        RentalStatusRenderer myRenderer = new RentalStatusRenderer();
	     // Apply to the whole table so colors look consistent across the row
	     table.setDefaultRenderer(Object.class, myRenderer);
	     
        refreshHistoryTable();
	     javax.swing.Timer autoRefreshTimer = new javax.swing.Timer(1000, e -> {
	    	 refreshHistoryTable();
	     });
	     autoRefreshTimer.start(); 

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // EXPORT BUTTON (BOTTOM)
        JButton btnExport = new JButton("Export CSV");
        btnExport.addActionListener(e -> exportHistoryToCSV());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnExport);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        // FILTER ACTION
        cbHistoryCategoryFilter.addActionListener(e -> applyHistoryCategoryFilter());

        return panel;
    }


    // ================= REFRESH =================
    private void refreshEquipmentTable() {

        // Remember selection
        Object previousSelection = cbCategoryFilter.getSelectedItem();

        equipmentTableModel.setRowCount(0);
        cbCategoryFilter.removeAllItems();

        cbCategoryFilter.addItem("All Categories");

        for (Equipment e : equipmentManager.getAllEquipments()) {

            // Populate table
            equipmentTableModel.addRow(new Object[]{
                    e.getBarcode(),
                    e.getName(),
                    e.getCategory(),
                    e.getAvailableQty(),
                    e.getTotalQty(),
                    e.getStatus().name()
            });

            // Populate filter 
            boolean exists = false;
            for (int i = 0; i < cbCategoryFilter.getItemCount(); i++) {
                if (cbCategoryFilter.getItemAt(i).equals(e.getCategory())) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                cbCategoryFilter.addItem(e.getCategory());
            }
        }

        // Restore selection 
        if (previousSelection != null) {
            cbCategoryFilter.setSelectedItem(previousSelection);
        }
    } 


    private void refreshHistoryTable() {
        // 1. Save what the user has currently selected
        Object selectedItem = cbHistoryCategoryFilter.getSelectedItem();

        // 2. Clear the table rows ONLY
        historyTableModel.setRowCount(0);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime now = LocalDateTime.now();

        for (Rental r : rentalManager.getRentalHistory()) {
            Equipment eq = equipmentManager.findByBarcode(r.getEquipmentBarcode());
            if (eq == null) continue;

            Equipment equipment = equipmentManager.findByBarcode(r.getEquipmentBarcode());
            String category = (equipment != null) ? equipment.getCategory() : "N/A";
            
            // 3. Filter check: Only add rows that match the CURRENT selection
            String selectedCategory = (selectedItem != null) ? selectedItem.toString() : "All Categories";
            
            if (selectedCategory.equals("All Categories") || eq.getCategory().equals(selectedCategory)) {
                
                // Calculate timeLeft
                String timeLeft;
                if (r.getStatus().toString().equals("CLOSED")) {
                    timeLeft = "--";
                } else {
                    java.time.Duration duration = java.time.Duration.between(now, r.getDueDate());
                    if (duration.isNegative()) {
                        timeLeft = "OVERDUE";
                    } else {
                        timeLeft = String.format("%dd %02dh %02dm %02ds", 
                            duration.toDays(), duration.toHoursPart(), 
                            duration.toMinutesPart(), duration.toSecondsPart());
                    }
                }

                historyTableModel.addRow(new Object[]{
                        r.getEquipmentBarcode(),
                        r.getEquipmentName(), 
                        category,
                        r.getQuantity(),
                        r.getRemainingQty(),
                        r.getRentalDate().format(fmt),
                        r.getDueDate().format(fmt),
                        r.getUserName(),
                        timeLeft,
                        r.getStatus().name()
                });
            }

            // 4. Update the dropdown list WITHOUT clearing it
            // Only add the category if it's not already in the list
            String cat = eq.getCategory();
            boolean exists = false;
            for (int i = 0; i < cbHistoryCategoryFilter.getItemCount(); i++) {
                if (cbHistoryCategoryFilter.getItemAt(i).equals(cat)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                cbHistoryCategoryFilter.addItem(cat);
            }
        }
    }

}
