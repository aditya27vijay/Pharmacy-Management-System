import com.formdev.flatlaf.FlatLightLaf; // Import FlatLaf class
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class PharmacistDashboardPage {

    private JFrame pharmacistFrame;
    private Connection connection;
    private JTextField nameField;
    private JTextField mobileField;
    private JTextField emailField;

    public PharmacistDashboardPage(Connection connection) {
        this.connection = connection;
        initializePharmacistDashboard();
    }

    private void initializePharmacistDashboard() {
        pharmacistFrame = new JFrame("Pharmacy Management System - Pharmacist Dashboard");
        pharmacistFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pharmacistFrame.setSize(600, 450);
        pharmacistFrame.setLocationRelativeTo(null); // Center the window
        pharmacistFrame.getContentPane().setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("Pharmacist Dashboard - Manage Inventory", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(51, 102, 153)); // Change text color
        pharmacistFrame.getContentPane().add(welcomeLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Add Medicine", createAddMedicinePanel());
        tabbedPane.add("Remove Medicine", createRemoveMedicinePanel());
        tabbedPane.add("View Medicines", createViewMedicinesPanel());
        tabbedPane.add("Update Medicine", createUpdateMedicinePanel());
        tabbedPane.add("Generate Bill", createGenerateBillPanel()); // New tab for Generate Bill

        pharmacistFrame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        JButton logoutButton = createStyledButton("Log Out", e -> {
            pharmacistFrame.dispose();
            new LoginPage();  // Return to login page
        });
        JPanel logoutPanel = new JPanel();
        logoutPanel.add(logoutButton);
        pharmacistFrame.getContentPane().add(logoutPanel, BorderLayout.SOUTH);

        pharmacistFrame.setVisible(true);
    }

    // Method to create Add Medicine panel
    private JPanel createAddMedicinePanel() {
        JPanel addMedicinePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Medicine Name
        JLabel nameLabel = new JLabel("Medicine Name:");
        JTextField nameField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 0;
        addMedicinePanel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        addMedicinePanel.add(nameField, gbc);

        // Quantity
        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 1;
        addMedicinePanel.add(quantityLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        addMedicinePanel.add(quantityField, gbc);

        // Price
        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();
        gbc.gridx = 0; gbc.gridy = 2;
        addMedicinePanel.add(priceLabel, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        addMedicinePanel.add(priceField, gbc);

        // Add button
        JButton addButton = createStyledButton("Add Medicine", e -> {
            String medicineName = nameField.getText();
            int quantity = Integer.parseInt(quantityField.getText());
            double price = Double.parseDouble(priceField.getText());
            addMedicine(medicineName, quantity, price);
        });
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER; // Center the button
        addMedicinePanel.add(addButton, gbc);

        return addMedicinePanel;
    }

    // Method to create Remove Medicine panel
    private JPanel createRemoveMedicinePanel() {
        JPanel removeMedicinePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Medicine Name:");
        JTextField nameField = new JTextField(15);

        JButton removeButton = createStyledButton("Remove Medicine", e -> {
            String medicineName = nameField.getText();
            if (medicineName.isEmpty()) {
                JOptionPane.showMessageDialog(removeMedicinePanel, "Please enter a medicine name.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } else {
                int confirmation = JOptionPane.showConfirmDialog(removeMedicinePanel, 
                    "Are you sure you want to remove medicine '" + medicineName + "'?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    removeMedicine(medicineName);
                }
            }
        });

        gbc.gridx = 0;
        gbc.gridy = 0;
        removeMedicinePanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        removeMedicinePanel.add(nameField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        removeMedicinePanel.add(removeButton, gbc);

        return removeMedicinePanel;
    }

    // Method to create View Medicines panel
    private JPanel createViewMedicinesPanel() {
        JPanel viewMedicinesPanel = new JPanel(new BorderLayout());

        JButton viewMedicinesButton = createStyledButton("View Medicines", e -> viewMedicines());

        viewMedicinesPanel.add(viewMedicinesButton, BorderLayout.NORTH);

        return viewMedicinesPanel;
    }

    // Method to create Update Medicine panel
    private JPanel createUpdateMedicinePanel() {
        JPanel updateMedicinePanel = new JPanel(new GridLayout(5, 2, 10, 10));  // Adjusted to 5 rows

        // Current Medicine Name
        JLabel currentNameLabel = new JLabel("Current Medicine Name:");
        JTextField currentNameField = new JTextField();

        // New Medicine Name
        JLabel newNameLabel = new JLabel("New Medicine Name:");
        JTextField newNameField = new JTextField();

        // New Quantity
        JLabel newQuantityLabel = new JLabel("New Quantity:");
        JTextField newQuantityField = new JTextField();

        // New Price
        JLabel newPriceLabel = new JLabel("New Price:");
        JTextField newPriceField = new JTextField();

        // Update Button
        JButton updateButton = createStyledButton("Update Medicine", e -> {
            String currentName = currentNameField.getText();
            String newName = newNameField.getText();
            int newQuantity = Integer.parseInt(newQuantityField.getText());
            double newPrice = Double.parseDouble(newPriceField.getText());
            updateMedicine(currentName, newName, newQuantity, newPrice);
        });

        // Add fields to the panel
        updateMedicinePanel.add(currentNameLabel);
        updateMedicinePanel.add(currentNameField);
        updateMedicinePanel.add(newNameLabel);
        updateMedicinePanel.add(newNameField);
        updateMedicinePanel.add(newQuantityLabel);
        updateMedicinePanel.add(newQuantityField);
        updateMedicinePanel.add(newPriceLabel);
        updateMedicinePanel.add(newPriceField);
        updateMedicinePanel.add(new JLabel()); // Empty label for spacing
        updateMedicinePanel.add(updateButton);

        return updateMedicinePanel;
    }

    // Method to create Generate Bill panel
    private JPanel createGenerateBillPanel() {
        JPanel generateBillPanel = new JPanel(new BorderLayout());

        JTextArea billArea = new JTextArea(10, 30);
        billArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(billArea);

        JButton generateButton = createStyledButton("Generate Bill", e -> {
            String input = JOptionPane.showInputDialog(pharmacistFrame, 
                "Enter the medicines and their quantities (e.g., Medicine A - 2, Medicine B - 1):", "Generate Bill", JOptionPane.QUESTION_MESSAGE);
            if (input != null && !input.trim().isEmpty()) {
                String[] items = input.split(",");
                double totalCost = 0;
                StringBuilder billDetails = new StringBuilder("Receipt:\n\n");

                for (String item : items) {
                    String[] parts = item.trim().split(" - ");
                    if (parts.length == 2) {
                        String medicineName = parts[0].trim();
                        int quantity = Integer.parseInt(parts[1].trim());
                        try {
                            // Fetch the price of the medicine from the database
                            String priceQuery = "SELECT price FROM medicines WHERE name = ?";
                            PreparedStatement pst = connection.prepareStatement(priceQuery);
                            pst.setString(1, medicineName);
                            ResultSet resultSet = pst.executeQuery();

                            if (resultSet.next()) {
                                double price = resultSet.getDouble("price");
                                double itemTotal = price * quantity;
                                totalCost += itemTotal;

                                // Append item details to the bill
                                billDetails.append(medicineName).append(" x").append(quantity)
                                        .append(" = ").append(itemTotal).append("\n");
                            } else {
                                JOptionPane.showMessageDialog(pharmacistFrame, "Medicine " + medicineName + " not found.", "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            resultSet.close();
                            pst.close();
                        } catch (SQLException ex) {
                            JOptionPane.showMessageDialog(pharmacistFrame, "Error retrieving medicine price: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }

                // Final bill details
                billDetails.append("\nTotal Cost: ").append(totalCost);
                billArea.setText(billDetails.toString());
            }
        });

        generateBillPanel.add(scrollPane, BorderLayout.CENTER);
        generateBillPanel.add(generateButton, BorderLayout.SOUTH);

        return generateBillPanel;
    }

    // Method to create styled buttons
    private JButton createStyledButton(String text, ActionListener actionListener) {
        JButton button = new JButton(text);
        button.addActionListener(actionListener);
        button.setBackground(new Color(51, 153, 255)); // Customize the button color
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        return button;
    }

 // Method to add medicine to the database
    private void addMedicine(String medicineName, int quantity, double price) {
        try {
            String query = "INSERT INTO medicines (name, quantity, price) VALUES (?, ?, ?)";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, medicineName);
            pst.setInt(2, quantity);
            pst.setDouble(3, price);
            pst.executeUpdate();
            JOptionPane.showMessageDialog(pharmacistFrame, "Medicine added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(pharmacistFrame, "Error adding medicine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to remove medicine from the database
    private void removeMedicine(String medicineName) {
        if (medicineName.isEmpty()) {
            JOptionPane.showMessageDialog(pharmacistFrame, "Please enter a valid medicine name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Prompt the user for the quantity to remove
        String quantityInput = JOptionPane.showInputDialog(pharmacistFrame, 
            "Enter the quantity of " + medicineName + " to remove:", "Remove Medicine", JOptionPane.QUESTION_MESSAGE);

        // Validate quantity input
        int quantityToRemove;
        try {
            quantityToRemove = Integer.parseInt(quantityInput);
            if (quantityToRemove <= 0) {
                JOptionPane.showMessageDialog(pharmacistFrame, "Please enter a positive quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(pharmacistFrame, "Invalid quantity entered. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmation = JOptionPane.showConfirmDialog(
            pharmacistFrame, 
            "Are you sure you want to remove " + quantityToRemove + " of " + medicineName + "?", 
            "Confirm Medicine Removal", 
            JOptionPane.YES_NO_OPTION);

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                // Get current quantity from the database
                String selectQuery = "SELECT quantity FROM medicines WHERE name = ?";
                PreparedStatement selectPst = connection.prepareStatement(selectQuery);
                selectPst.setString(1, medicineName);
                ResultSet resultSet = selectPst.executeQuery();

                if (resultSet.next()) {
                    int currentQuantity = resultSet.getInt("quantity");

                    if (currentQuantity < quantityToRemove) {
                        JOptionPane.showMessageDialog(pharmacistFrame, "Not enough stock available. Current quantity: " + currentQuantity, "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Update the quantity in the database
                    int newQuantity = currentQuantity - quantityToRemove;
                    String updateQuery = "UPDATE medicines SET quantity = ? WHERE name = ?";
                    PreparedStatement updatePst = connection.prepareStatement(updateQuery);
                    updatePst.setInt(1, newQuantity);
                    updatePst.setString(2, medicineName);
                    int rowsAffected = updatePst.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(pharmacistFrame, quantityToRemove + " of " + medicineName + " removed successfully. New quantity: " + newQuantity);
                    } else {
                        JOptionPane.showMessageDialog(pharmacistFrame, "Error updating the medicine quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(pharmacistFrame, "Medicine not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(pharmacistFrame, "Error removing medicine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // Method to update medicine details
    private void updateMedicine(String currentName, String newName, int newQuantity, double newPrice) {
        try {
            String query = "UPDATE medicines SET name = ?, quantity = ?, price = ? WHERE name = ?";
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, newName);
            pst.setInt(2, newQuantity);
            pst.setDouble(3, newPrice);
            pst.setString(4, currentName);
            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(pharmacistFrame, "Medicine updated successfully.");
            } else {
                JOptionPane.showMessageDialog(pharmacistFrame, "Medicine not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(pharmacistFrame, "Error updating medicine: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to view all medicines in the database
    private void viewMedicines() {
        try {
            String query = "SELECT * FROM medicines";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Get metadata to determine number of columns
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Create column names array
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }

            // Create a list to hold medicine data
            ArrayList<Object[]> medicines = new ArrayList<>();
            while (resultSet.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    row[i] = resultSet.getObject(i + 1);
                }
                medicines.add(row);
            }

            // Convert list to a 2D array for JTable
            Object[][] data = medicines.toArray(new Object[0][]);

            // Create JTable with data and column names
            JTable table = new JTable(data, columnNames);
            table.setFillsViewportHeight(true);

            // Create JScrollPane to hold the table
            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(500, 300)); // Set preferred size

            // Show the table in a dialog
            JOptionPane.showMessageDialog(pharmacistFrame, scrollPane, "Medicines", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(pharmacistFrame, "Error retrieving medicines: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            Connection connection = DriverManager.getConnection("jdbc:sqlite:pharmacy.db"); // Adjust your database path
            SwingUtilities.invokeLater(() -> new PharmacistDashboardPage(connection));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
