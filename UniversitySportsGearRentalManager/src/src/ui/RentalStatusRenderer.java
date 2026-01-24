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

        // Always reset to default first to prevent color bleeding in other columns
        c.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());

        try {
            // 1. Dynamically find the column indices
            int statusIdx = -1;
            int timeLeftIdx = -1;

            for (int i = 0; i < table.getColumnCount(); i++) {
                String name = table.getColumnName(i);
                if (name.equalsIgnoreCase("Status")) statusIdx = i;
                if (name.equalsIgnoreCase("Time Left")) timeLeftIdx = i;
            }

            // 2. Logic for "Time Left" column
            if (column == timeLeftIdx && value != null) {
                String timeLeftVal = value.toString();
                if (timeLeftVal.equalsIgnoreCase("OVERDUE")) {
                    c.setForeground(Color.RED);
                } else {
                	c.setForeground(Color.BLACK);
                }
            }

            // 3. Logic for "Status" column
            if (column == statusIdx && value != null) {
                String status = value.toString().toUpperCase();
                
                switch (status) {
                    case "AVAILABLE":
                    case "ACTIVE":
                        c.setForeground(new Color(0, 150, 0)); // Dark Green
                        break;
                    case "UNAVAILABLE":
                    case "CLOSED":
                        c.setForeground(Color.GRAY);
                        break;
                    case "LATE":
                        c.setForeground(Color.RED);
                        break;
                }
            }

        } catch (Exception e) {
            // Silence exceptions to keep the UI stable
        }

        return c;
    }
}