package ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import manager.EquipmentManager;
import manager.RentalManager;
import model.Admin;
import model.Student;
import model.User;

/*
 * LoginFrame
 * Basic login modal with Student basic info
 */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private EquipmentManager equipmentManager;
    private RentalManager rentalManager;

    public LoginFrame(EquipmentManager equipmentManager, RentalManager rentalManager) {
        this.equipmentManager = equipmentManager;
        this.rentalManager = rentalManager;

        setTitle("AMS - Student Asset Management System");
        setSize(450, 350);
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

        // ---------- ROLE ----------
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Role:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        String[] roles = {"Admin", "Student"};
        JComboBox<String> cbRole = new JComboBox<>(roles);
        formPanel.add(cbRole, gbc);

        // ---------- STUDENT CLASS ----------
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Class:"), gbc);

        gbc.gridx = 1;
        JTextField txtClass = new JTextField(20);
        formPanel.add(txtClass, gbc);

        // ---------- STUDENT YEAR ----------
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Year:"), gbc);

        gbc.gridx = 1;
        JSpinner spYear = new JSpinner(new SpinnerNumberModel(1, 1, 4, 1));
        formPanel.add(spYear, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnLogin);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // 🔹 Initially hide student-only fields
        txtClass.setVisible(false);
        spYear.setVisible(false);

        ((JLabel) formPanel.getComponent(4)).setVisible(false); // Class label
        ((JLabel) formPanel.getComponent(6)).setVisible(false); // Year label

        // ---------- ROLE CHANGE LISTENER ----------
        cbRole.addActionListener(e -> {
            boolean isStudent = cbRole.getSelectedItem().equals("Student");

            txtClass.setVisible(isStudent);
            spYear.setVisible(isStudent);

            ((JLabel) formPanel.getComponent(4)).setVisible(isStudent);
            ((JLabel) formPanel.getComponent(6)).setVisible(isStudent);

            formPanel.revalidate();
            formPanel.repaint();
        });

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

            User user;

            if (role.equals("Admin")) {
                user = new Admin("A001", name);
                new AdminFrame(user, equipmentManager, rentalManager).setVisible(true);
            } else {
                String studentClass = txtClass.getText().trim();
                int year = (int) spYear.getValue();

                if (studentClass.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter class",
                            "Input Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Student student = new Student("S001", name, studentClass, year);
                new StudentFrame(student, equipmentManager, rentalManager).setVisible(true);
            }

            dispose();
        });
    }
}
