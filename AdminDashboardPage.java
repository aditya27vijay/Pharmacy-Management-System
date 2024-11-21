import com.formdev.flatlaf.FlatLightLaf;  // Import FlatLaf class
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class AdminDashboardPage {

    private JFrame adminFrame;
    private Connection connection;
    // Declare these fields at the top of your class
    private JTextField nameField;    
    private JTextField mobileField;
    private JTextField emailField;

    public AdminDashboardPage(Connection connection, String username) {
        this.connection = connection;
        initializeAdminDashboard();
    }

    private void initializeAdminDashboard() {
        adminFrame = new JFrame("Pharmacy Management System - Admin Dashboard");
        adminFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        adminFrame.setSize(600, 450);
        adminFrame.setLocationRelativeTo(null); // Center the window
        adminFrame.getContentPane().setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Admin Dashboard - Manage Users", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(51, 102, 153)); // Change text color
        adminFrame.getContentPane().add(welcomeLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Add User", createAddUserPanel());
        tabbedPane.add("Remove User", createRemoveUserPanel());
        tabbedPane.add("View Users", createViewUsersPanel());
        tabbedPane.add("Update User", createUpdateUserPanel());

        adminFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JButton logoutButton = createStyledButton("Log Out", e -> {
            adminFrame.dispose();
            new LoginPage();  // Return to login page
        });
        JPanel logoutPanel = new JPanel();
        logoutPanel.add(logoutButton);
        adminFrame.getContentPane().add(logoutPanel, BorderLayout.SOUTH);

        adminFrame.setVisible(true);
    }

    // Method to create Add User panel
    private JPanel createAddUserPanel() {
        JPanel addUserPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 0;
        addUserPanel.add(usernameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        addUserPanel.add(usernameField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();
        gbc.gridx = 0; gbc.gridy = 1;
        addUserPanel.add(passwordLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        addUserPanel.add(passwordField, gbc);

        // Role
        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"Admin", "Pharmacist"});
        gbc.gridx = 0; gbc.gridy = 2;
        addUserPanel.add(roleLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        addUserPanel.add(roleComboBox, gbc);

        // Name
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 3;
        addUserPanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        addUserPanel.add(nameField, gbc);

        // Mobile Number
        JLabel mobileLabel = new JLabel("Mobile Number:");
        JTextField mobileField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 4;
        addUserPanel.add(mobileLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        addUserPanel.add(mobileField, gbc);

        // Email
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 5;
        addUserPanel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        addUserPanel.add(emailField, gbc);

        // Add button
        JButton addButton = createStyledButton("Add User", e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleComboBox.getSelectedItem();
            String name = nameField.getText();
            String mobile = mobileField.getText();
            String email = emailField.getText();

            addUser(username, password, role, name, mobile, email);
        });
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.CENTER;  // Center the button
        addUserPanel.add(addButton, gbc);

        return addUserPanel;
    }

    // Method to create Remove User panel
    private JPanel createRemoveUserPanel() {
        JPanel removeUserPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        // Create a button with an action listener
        JButton removeButton = createStyledButton("Remove User", e -> {
            String username = usernameField.getText();
            if (username.isEmpty()) {
                JOptionPane.showMessageDialog(removeUserPanel, "Please enter a username.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int confirmation = JOptionPane.showConfirmDialog(removeUserPanel, 
                    "Are you sure you want to remove user '" + username + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    // Call the void removeUser method directly
                    removeUser(username);  // Success/failure will be handled within the method
                }
            }
        });

        // Add components to the panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        removeUserPanel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        removeUserPanel.add(usernameField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        removeUserPanel.add(removeButton, gbc);

        return removeUserPanel;
    }


    // Method to create View Users panel
    private JPanel createViewUsersPanel() {
        JPanel viewUserPanel = new JPanel(new BorderLayout());

        JButton viewUsersButton = createStyledButton("View Users", e -> viewUsers());

        viewUserPanel.add(viewUsersButton, BorderLayout.NORTH);

        return viewUserPanel;
    }

    // Method to create Update User panel
    private JPanel createUpdateUserPanel() {
        JPanel updateUserPanel = new JPanel(new GridLayout(8, 2, 10, 10));  // Changed to 8 rows to fit all fields

        // Current Username
        JLabel usernameLabel = new JLabel("Current Username:");
        JTextField usernameField = new JTextField();

        // New Username
        JLabel newUsernameLabel = new JLabel("New Username:");
        JTextField newUsernameField = new JTextField();

        // New Password
        JLabel newPasswordLabel = new JLabel("New Password:");
        JTextField newPasswordField = new JTextField();

        // New Name
        JLabel newNameLabel = new JLabel("New Name:");
        nameField = new JTextField();  // Assuming nameField is declared at class level

        // New Mobile
        JLabel newMobileLabel = new JLabel("New Mobile:");
        mobileField = new JTextField();  // Assuming mobileField is declared at class level

        // New Email
        JLabel newEmailLabel = new JLabel("New Email:");
        emailField = new JTextField();  // Assuming emailField is declared at class level

        // Update Button
        JButton updateButton = createStyledButton("Update User", e -> {
            String currentUsername = usernameField.getText();
            String newUsername = newUsernameField.getText();
            String newPassword = newPasswordField.getText();
            String newName = nameField.getText();
            String newMobile = mobileField.getText();
            String newEmail = emailField.getText();

            // Call the updateUser method with all the updated parameters
            updateUser(currentUsername, newUsername, newPassword, newName, newMobile, newEmail);
        });

        // Add fields to the panel
        updateUserPanel.add(usernameLabel);
        updateUserPanel.add(usernameField);

        updateUserPanel.add(newUsernameLabel);
        updateUserPanel.add(newUsernameField);

        updateUserPanel.add(newPasswordLabel);
        updateUserPanel.add(newPasswordField);

        updateUserPanel.add(newNameLabel);
        updateUserPanel.add(nameField);

        updateUserPanel.add(newMobileLabel);
        updateUserPanel.add(mobileField);

        updateUserPanel.add(newEmailLabel);
        updateUserPanel.add(emailField);

        updateUserPanel.add(new JLabel()); // Empty label for spacing
        updateUserPanel.add(updateButton);

        return updateUserPanel;
    }

    // Method to create styled buttons
    private JButton createStyledButton(String title, ActionListener action) {
        JButton button = new JButton(title);
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.setBackground(new Color(51, 153, 255));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addActionListener(action);
        return button;
    }

    // Method to add user to the database
    private void addUser(String username, String password, String role, String name, String mobile, String email) {
    	try {
            String query = "INSERT INTO users (username, password, role, name, mobile, email) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);  // Ideally, passwords should be hashed
            pst.setString(3, role);
            pst.setString(4, name);
            pst.setString(5, mobile);
            pst.setString(6, email);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(adminFrame, "User added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(adminFrame, "Error adding user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    
    }

    // Method to remove user from the database
    private void removeUser(String username) {
    	 if (username.isEmpty()) {
             JOptionPane.showMessageDialog(adminFrame, "Please enter a valid username.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
         }

         // Confirmation dialog to confirm the action before proceeding
         int confirmation = JOptionPane.showConfirmDialog(
                 adminFrame, 
                 "Are you sure you want to remove the user: " + username + "?", 
                 "Confirm User Removal", 
                 JOptionPane.YES_NO_OPTION);

         if (confirmation == JOptionPane.YES_OPTION) {
             try {
                 String query = "DELETE FROM users WHERE username = ?";
                 PreparedStatement pst = connection.prepareStatement(query);
                 pst.setString(1, username);
                 int rowsAffected = pst.executeUpdate();

                 if (rowsAffected > 0) {
                     JOptionPane.showMessageDialog(adminFrame, "User removed successfully.");
                 } else {
                     JOptionPane.showMessageDialog(adminFrame, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                 }
             } catch (SQLException e) {
                 e.printStackTrace();
                 JOptionPane.showMessageDialog(adminFrame, "Error removing user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             }
         } else {
             JOptionPane.showMessageDialog(adminFrame, "User removal canceled.");
         }
    }

    // Method to view users in the database
    private void viewUsers() {
        try {
            // Adjust the query to retrieve name, mobile, and email as well
            String query = "SELECT username, role, name, mobile, email FROM users"; // Adjust the query to match your table structure
            PreparedStatement pst = connection.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            // Prepare data for the table
            ArrayList<String[]> userList = new ArrayList<>();
            while (rs.next()) {
                String username = rs.getString("username");
                String role = rs.getString("role");
                String name = rs.getString("name");
                String mobile = rs.getString("mobile");
                String email = rs.getString("email");
                userList.add(new String[]{username, role, name, mobile, email});
            }

            // Create table headers including name, mobile, and email
            String[] columnNames = {"Username", "Role", "Name", "Mobile", "Email"};
            String[][] data = userList.toArray(new String[0][]);

            // Display the user data in a new window
            JFrame userFrame = new JFrame("User List");
            userFrame.setSize(800, 400); // Adjust the size for more columns
            userFrame.setLocationRelativeTo(adminFrame); // Center on parent frame
            userFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close the frame on exit

            // Create the table with data
            JTable userTable = new JTable(data, columnNames);
            userTable.setFillsViewportHeight(true); // Ensures the table fills the viewport

            // Enable sorting by clicking column headers
            userTable.setAutoCreateRowSorter(true);

            // Add a scroll pane for the table
            JScrollPane scrollPane = new JScrollPane(userTable);
            userFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

            userFrame.setVisible(true);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(adminFrame, "Error retrieving users: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to update user details
    private void updateUser(String currentUsername, String newUsername, String newPassword, String newName, String newMobile, String newEmail) {
    	if (currentUsername.isEmpty() || (newUsername.isEmpty() && newPassword.isEmpty() && newName.isEmpty() && newMobile.isEmpty() && newEmail.isEmpty())) {
            JOptionPane.showMessageDialog(adminFrame, "Please provide valid inputs.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            // Fetch the current user details from the database for comparison
            String queryFetch = "SELECT username, password, name, mobile, email FROM users WHERE username = ?";
            PreparedStatement pstFetch = connection.prepareStatement(queryFetch);
            pstFetch.setString(1, currentUsername);
            ResultSet rs = pstFetch.executeQuery();

            if (!rs.next()) {
                JOptionPane.showMessageDialog(adminFrame, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Retrieve the current values from the database
            String currentDbUsername = rs.getString("username");
            String currentDbPassword = rs.getString("password");
            String currentDbName = rs.getString("name");
            String currentDbMobile = rs.getString("mobile");
            String currentDbEmail = rs.getString("email");

            // Validate: Ensure that new data is different from existing data
            if (!newUsername.isEmpty() && newUsername.equals(currentDbUsername)) {
                JOptionPane.showMessageDialog(adminFrame, "New username cannot be the same as the current username.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newPassword.isEmpty() && newPassword.equals(currentDbPassword)) {
                JOptionPane.showMessageDialog(adminFrame, "New password cannot be the same as the current password.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newName.isEmpty() && newName.equals(currentDbName)) {
                JOptionPane.showMessageDialog(adminFrame, "New name cannot be the same as the current name.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newMobile.isEmpty() && newMobile.equals(currentDbMobile)) {
                JOptionPane.showMessageDialog(adminFrame, "New mobile number cannot be the same as the current mobile number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!newEmail.isEmpty() && newEmail.equals(currentDbEmail)) {
                JOptionPane.showMessageDialog(adminFrame, "New email cannot be the same as the current email.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Construct the update query dynamically based on which fields have changed
            StringBuilder queryUpdate = new StringBuilder("UPDATE users SET ");
            boolean hasChanges = false;

            if (!newUsername.isEmpty()) {
                queryUpdate.append("username = ?");
                hasChanges = true;
            }
            if (!newPassword.isEmpty()) {
                if (hasChanges) queryUpdate.append(", ");
                queryUpdate.append("password = ?");
                hasChanges = true;
            }
            if (!newName.isEmpty()) {
                if (hasChanges) queryUpdate.append(", ");
                queryUpdate.append("name = ?");
                hasChanges = true;
            }
           
            
            if (!newMobile.isEmpty()) {
                if (hasChanges) queryUpdate.append(", ");
                queryUpdate.append("mobile = ?");
                hasChanges = true;
            }
            if (!newEmail.isEmpty()) {
                if (hasChanges) queryUpdate.append(", ");
                queryUpdate.append("email = ?");
                hasChanges = true;
            }

            queryUpdate.append(" WHERE username = ?");

            PreparedStatement pstUpdate = connection.prepareStatement(queryUpdate.toString());

            // Bind the new values to the prepared statement
            int paramIndex = 1;
            if (!newUsername.isEmpty()) {
                pstUpdate.setString(paramIndex++, newUsername);
            }
            if (!newPassword.isEmpty()) {
                pstUpdate.setString(paramIndex++, newPassword);
            }
            if (!newName.isEmpty()) {
                pstUpdate.setString(paramIndex++, newName);
            }
          
          
            if (!newMobile.isEmpty()) {
                pstUpdate.setString(paramIndex++, newMobile);
            }
            if (!newEmail.isEmpty()) {
                pstUpdate.setString(paramIndex++, newEmail);
            }
            pstUpdate.setString(paramIndex, currentUsername);  // Bind the current username

            // Execute the update
            int rowsAffected = pstUpdate.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(adminFrame, "User details updated successfully.");
            } else {
                JOptionPane.showMessageDialog(adminFrame, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(adminFrame, "Error updating user: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Set the FlatLaf look-and-feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Database connection code here
        Connection connection = null;//... obtain connection;

        SwingUtilities.invokeLater(() -> new AdminDashboardPage(connection, "admin")); // Launch GUI
    }
}
