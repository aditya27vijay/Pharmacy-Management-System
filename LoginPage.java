import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import com.formdev.flatlaf.FlatLightLaf;


public class LoginPage {

    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Connection connection;

    public LoginPage() {
        initializeDatabase();
        initializeLogin();
    }

    private void initializeDatabase() {
        try {
            // Database connection
            String jdbcURL = "jdbc:mysql://localhost:3306/pharmacydb1";
            String username = "root";
            String password = "ADIvij@1";

            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcURL, username, password);
            System.out.println("Database connected successfully.");
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializeLogin() {
        // Set modern look and feel (FlatLaf theme)
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the frame
        frame = new JFrame("Pharmacy Management System - Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setLocationRelativeTo(null);  // Center the window
        // Fullscreen

        // Main panel for the login form
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS)); // Vertical layout
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Padding

        // Title Label
        JLabel titleLabel = new JLabel("Login to Pharmacy System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(0, 123, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center align title
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Spacer

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBackground(Color.WHITE);
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        usernameField = new JTextField();
        usernameField.setFont(new Font("Arial", Font.PLAIN, 22));
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 22));
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 22));

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        
        mainPanel.add(formPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout()); // Center buttons
        buttonPanel.setBackground(Color.WHITE);

        JButton loginButton = createStyledButton("Login", e -> {
            String username = usernameField.getText();
            String password = String.valueOf(passwordField.getPassword());

            String role = authenticateUser(username, password);
            if (role != null) {
                JOptionPane.showMessageDialog(frame, "Login Successful");
                frame.dispose();
                if (role.equals("Admin")) {
                    new AdminDashboardPage(connection, username);  // Redirect to admin dashboard
                } else if (role.equals("Pharmacist")) {
                    new PharmacistDashboardPage(connection);  // Redirect to pharmacist dashboard
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton signUpButton = createStyledButton("Sign Up", e -> openSignUpForm());

        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Spacer

        // Add the main panel to the frame
        frame.getContentPane().add(mainPanel);
        frame.pack();  // Adjusts the frame size based on its content
        frame.setVisible(true);
    }



    private JButton createStyledButton(String title, ActionListener action) {
        JButton button = new JButton(title);
        button.setFont(new Font("Arial", Font.PLAIN, 22));
        button.setBackground(new Color(0, 123, 255));  // Set button background color
        button.setForeground(Color.WHITE);  // Set button text color
        button.setFocusPainted(false);  // Remove button focus border
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));  // Change cursor on hover
        button.addActionListener(action);
        return button;
    }

    private String authenticateUser(String username, String password) {
        try {
            String query = "SELECT role FROM users WHERE username = ? AND password = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);  // Password should be hashed in production
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openSignUpForm() {
        JFrame signUpFrame = new JFrame("Sign Up");
        signUpFrame.setSize(400, 300);  // Increased size for clarity
        signUpFrame.setLocationRelativeTo(null);
        signUpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Use GridBagLayout for flexible control over layout
        JPanel signUpPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);  // Padding around components

        signUpPanel.setBackground(Color.WHITE);

        // Create components
        JLabel usernameLabel = new JLabel("Username:");
        JTextField newUsernameField = new JTextField(15);  // Username text field

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField newPasswordField = new JPasswordField(15);

        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Admin", "Pharmacist"});  // Drop-down for role selection

        JButton registerButton = createStyledButton("Register", e -> {
            String newUsername = newUsernameField.getText();
            String newPassword = String.valueOf(newPasswordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();  // Get selected role

            if (newUsername.isEmpty() || newPassword.isEmpty() || role == null) {
                JOptionPane.showMessageDialog(signUpFrame, "Please enter valid data", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                if (registerUser(newUsername, newPassword, role)) {
                    JOptionPane.showMessageDialog(signUpFrame, "Registration Successful");
                    signUpFrame.dispose();
                } else {
                    JOptionPane.showMessageDialog(signUpFrame, "Username already exists", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Layout components using GridBagLayout

        // Username label and field (side by side)
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;  // Align to the left
        signUpPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signUpPanel.add(newUsernameField, gbc);

        // Password label and field (side by side)
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        signUpPanel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signUpPanel.add(newPasswordField, gbc);

        // Role label and combo box (side by side)
        gbc.gridx = 0;
        gbc.gridy = 2;
        signUpPanel.add(roleLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        signUpPanel.add(roleComboBox, gbc);

        // Register button (centered below all fields)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;  // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER;  // Center the button
        signUpPanel.add(registerButton, gbc);

        // Add panel to frame and show the form
        signUpFrame.add(signUpPanel);
        signUpFrame.setVisible(true);
    }


    private boolean registerUser(String username, String password, String role) {
        try {
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return false;
            }

            String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            pst.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
