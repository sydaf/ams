package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Admin;
import model.Student;
import model.User;

/*
 * LoginFrame - Syed
 * Basic login modal with Student basic info
 */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // Dummy login credentials
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String STUDENT_USERNAME = "student";
    private static final String STUDENT_PASSWORD = "student123";
    
    // Static student data
    private static final String DEFAULT_STUDENT_CLASS = "CS101";
    private static final int DEFAULT_STUDENT_YEAR = 1;

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

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Asset Management System", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // ---------- NAME ----------
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtName = new JTextField(20);
        formPanel.add(txtName, gbc);

        // ---------- PASSWORD ----------
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPasswordField txtPassword = new JPasswordField(20);
        formPanel.add(txtPassword, gbc);

        // ---------- ERROR LABEL ----------
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel lblError = new JLabel("invalid credential");
        lblError.setForeground(Color.RED);
        lblError.setVisible(false);
        formPanel.add(lblError, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnLogin);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // ---------- LOGIN ACTION ----------
        btnLogin.addActionListener(e -> {
            // Hide error label initially
            lblError.setVisible(false);

            String name = txtName.getText().trim();
            String password = new String(txtPassword.getPassword());

            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter your name",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter your password",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Auto-detect role based on credentials
            String role = null;
            boolean isValid = false;

            // Try admin credentials first
            if (name.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                role = "Admin";
                isValid = true;
            }
            // Try student credentials
            else if (name.equals(STUDENT_USERNAME) && password.equals(STUDENT_PASSWORD)) {
                role = "Student";
                isValid = true;
            }

            if (!isValid) {
                lblError.setVisible(true);
                formPanel.revalidate();
                formPanel.repaint();
                return;
            }

            User user;

            if (role.equals("Admin")) {
                user = new Admin("A001", name);
                new AdminFrame(user, equipmentManager, rentalManager).setVisible(true);
            } else {
                // Use static data for student class and year
                Student student = new Student("S001", name, DEFAULT_STUDENT_CLASS, DEFAULT_STUDENT_YEAR);
                new StudentFrame(student, equipmentManager, rentalManager).setVisible(true);
            }

            dispose();
        });
    }
}
