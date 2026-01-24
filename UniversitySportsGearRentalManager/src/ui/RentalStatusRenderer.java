package ui;

import javax.swing.table.DefaultTableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JTable;

public class RentalStatusRenderer extends DefaultTableCellRenderer {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        // Get values from the model for logic
        String status = table.getValueAt(row, 6).toString(); // Status column
        String dueDateStr = table.getValueAt(row, 5).toString(); // Due Date column
        
        boolean isLate = false; 
        boolean isClosed = false; 

        try {
            LocalDateTime dueDate = LocalDateTime.parse(dueDateStr, formatter);
            // Check if status is LATE or if current time is past due date
            if ("LATE".equalsIgnoreCase(status) && LocalDateTime.now().isAfter(dueDate)) {
            	isClosed = false; 
                isLate = true;
            } else if ("CLOSED".equalsIgnoreCase(status) && LocalDateTime.now().isAfter(dueDate)) {
            	isClosed = true; 
            	isLate = false; 
            } 
        } catch (Exception e) {
            // Handle parsing error if necessary
        } 

        if ("AVAILABLE".equalsIgnoreCase(status)) {
            c.setForeground(Color.GREEN);
        } else if ("UNAVAILABLE".equalsIgnoreCase(status)) {
            c.setForeground(Color.GRAY);
        } else if ("ACTIVE".equalsIgnoreCase(status)) {
            c.setForeground(Color.GREEN);
        } else if (isClosed) {
            c.setForeground(Color.GRAY);
        } else if (isLate) {
            c.setForeground(Color.RED);
        }  else {
            // Reset to default colors so it doesn't stay red when reused
            c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        }

        return c;
    }
}