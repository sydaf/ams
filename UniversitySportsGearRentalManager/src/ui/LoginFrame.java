package ui;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Admin;
import model.Student;
import model.User;

import javax.swing.*;
import java.awt.*;

/*
 * LoginFrame - Updated to match AMS specification
 * Basic login modal for authentication
 */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;

    public LoginFrame(EquipmentManager equipmentManager, RentalManager rentalManager) {
        this.equipmentManager = equipmentManager;
        this.rentalManager = rentalManager;

        setTitle("AMS - Student Asset Management System");
        setSize(450, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Asset Management System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(title, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        JTextField txtName = new JTextField(20);
        formPanel.add(txtName, gbc);

        // Role field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        String[] roles = {"Admin", "Student"};
        JComboBox<String> cbRole = new JComboBox<>(roles);
        formPanel.add(cbRole, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Login button
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(100, 35));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnLogin);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ---------- LOGIN ACTION ----------
        btnLogin.addActionListener(e -> {
            String name = txtName.getText().trim();
            String role = cbRole.getSelectedItem().toString();

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter your name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // POLYMORPHISM: User reference, different object types
            User user;
            if (role.equals("Admin")) {
                user = new Admin("A001", name);
                new AdminFrame(user, equipmentManager, rentalManager).setVisible(true);
            } else {
                user = new Student("S001", name);
                new StudentFrame(user, equipmentManager, rentalManager).setVisible(true);
            }

            dispose();
        });
    }
}
