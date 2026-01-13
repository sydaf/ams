package ui;

import manager.RentalManager;
import model.Rental;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/*
 * RentalHistoryFrame
 * Displays rental history using JTable
 */
public class RentalHistoryFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    public RentalHistoryFrame(RentalManager rentalManager) {

        setTitle("Rental History");
        setSize(700, 400);
        setLocationRelativeTo(null);

        String[] columns = {"User", "Equipment", "Quantity", "Date"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Rental r : rentalManager.getRentalHistory()) {
            tableModel.addRow(new Object[]{
                    r.getUserName(),
                    r.getEquipmentName(),
                    r.getQuantity(),
                    r.getRentalDate().format(formatter)
            });
        }

        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
