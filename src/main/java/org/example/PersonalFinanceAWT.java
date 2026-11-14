package org.example;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class PersonalFinanceAWT extends Frame
        implements ActionListener {

    // Database connection details
    private static final String URL =
            "jdbc:mysql://localhost:3306/personal_finance";
    private static final String USER = "root";
    private static final String PASSWORD = "bhavika@1112";

    // Labels
    Label title;
    Label nameLabel;
    Label amountLabel;
    Label categoryLabel;

    // Text fields
    TextField nameField;
    TextField amountField;

    // Category dropdown
    Choice categoryChoice;

    // Buttons
    Button addButton;
    Button readButton;
    Button updateButton;
    Button clearAllButton;
    Button exitButton;

    // Display area
    TextArea displayArea;

    // Connect to MySQL
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Constructor
    PersonalFinanceAWT() {

        setTitle("Personal Finance & Expense Management System");
        setSize(650, 550);
        setLayout(new FlowLayout());

        title = new Label(
                "PERSONAL FINANCE & EXPENSE MANAGEMENT SYSTEM"
        );
        title.setFont(new Font("Arial", Font.BOLD, 16));

        nameLabel = new Label("Expense Name:");
        amountLabel = new Label("Amount:");
        categoryLabel = new Label("Category:");

        nameField = new TextField(20);
        amountField = new TextField(20);

        categoryChoice = new Choice();
        categoryChoice.add("Food");
        categoryChoice.add("Travel");
        categoryChoice.add("Shopping");
        categoryChoice.add("Bills");
        categoryChoice.add("Other");

        addButton = new Button("Add Expense");
        readButton = new Button("Read Expenses");
        updateButton = new Button("Update Expense");
        clearAllButton = new Button("Clear All");
        exitButton = new Button("Exit");

        displayArea = new TextArea(15, 60);
        displayArea.setEditable(false);

        add(title);
        add(nameLabel);
        add(nameField);
        add(amountLabel);
        add(amountField);
        add(categoryLabel);
        add(categoryChoice);

        add(addButton);
        add(readButton);
        add(updateButton);
        add(clearAllButton);
        add(exitButton);
        add(displayArea);

        addButton.addActionListener(this);
        readButton.addActionListener(this);
        updateButton.addActionListener(this);
        clearAllButton.addActionListener(this);
        exitButton.addActionListener(this);

        // Window close event
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
    }

    // Validate amount
    private double getValidAmount() {
        double amount = Double.parseDouble(
                amountField.getText().trim()
        );

        if (!Double.isFinite(amount) || amount <= 0) {
            throw new IllegalArgumentException(
                    "Amount must be a positive number!"
            );
        }

        return amount;
    }

    // ADD: Save an expense in MySQL
    private void addExpense() {

        String name = nameField.getText().trim();
        String category = categoryChoice.getSelectedItem();

        if (name.isEmpty() || amountField.getText().trim().isEmpty()) {
            displayArea.setText("Please enter all expense details!");
            return;
        }

        try {
            double amount = getValidAmount();

            String sql =
                    "INSERT INTO expenses (expense_name, amount, category) "
                            + "VALUES (?, ?, ?)";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setString(1, name);
                ps.setDouble(2, amount);
                ps.setString(3, category);

                ps.executeUpdate();
            }

            displayArea.setText("Expense added successfully!");
            nameField.setText("");
            amountField.setText("");

        } catch (NumberFormatException ex) {
            displayArea.setText("Please enter a valid numeric amount!");
        } catch (IllegalArgumentException ex) {
            displayArea.setText(ex.getMessage());
        } catch (SQLException ex) {
            displayArea.setText("Database error: " + ex.getMessage());
        }
    }

    // READ: Retrieve expenses from MySQL
    private void readExpenses() {

        String sql =
                "SELECT id, expense_name, amount, category "
                        + "FROM expenses ORDER BY id";

        StringBuilder result = new StringBuilder();

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.append("ID: ")
                        .append(rs.getInt("id"))
                        .append("\nExpense Name: ")
                        .append(rs.getString("expense_name"))
                        .append("\nAmount: Rs. ")
                        .append(rs.getBigDecimal("amount"))
                        .append("\nCategory: ")
                        .append(rs.getString("category"))
                        .append("\n--------------------------\n");
            }

            if (result.isEmpty()) {
                displayArea.setText("No expenses found!");
            } else {
                displayArea.setText(result.toString());
            }

        } catch (SQLException ex) {
            displayArea.setText("Database error: " + ex.getMessage());
        }
    }

    // UPDATE: Update an expense by its name
    private void updateExpense() {

        String name = nameField.getText().trim();
        String category = categoryChoice.getSelectedItem();

        if (name.isEmpty() || amountField.getText().trim().isEmpty()) {
            displayArea.setText(
                    "Enter the existing expense name and new amount!"
            );
            return;
        }

        try {
            double amount = getValidAmount();

            String sql =
                    "UPDATE expenses SET amount = ?, category = ? "
                            + "WHERE expense_name = ?";

            try (Connection con = getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setDouble(1, amount);
                ps.setString(2, category);
                ps.setString(3, name);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    displayArea.setText(
                            "Expense updated successfully!\n\n"
                    );
                    readExpenses();
                } else {
                    displayArea.setText(
                            "Expense not found! Enter an existing name."
                    );
                }
            }

        } catch (NumberFormatException ex) {
            displayArea.setText("Please enter a valid numeric amount!");
        } catch (IllegalArgumentException ex) {
            displayArea.setText(ex.getMessage());
        } catch (SQLException ex) {
            displayArea.setText("Database error: " + ex.getMessage());
        }
    }

    // CLEAR ALL: Delete all records from MySQL
    private void clearAllExpenses() {

        String sql = "DELETE FROM expenses";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            int rows = ps.executeUpdate();

            nameField.setText("");
            amountField.setText("");
            displayArea.setText(
                    "All expenses cleared!\nRecords deleted: " + rows
            );

        } catch (SQLException ex) {
            displayArea.setText("Database error: " + ex.getMessage());
        }
    }

    // Button events
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == addButton) {
            addExpense();
        }
        else if (e.getSource() == readButton) {
            readExpenses();
        }
        else if (e.getSource() == updateButton) {
            updateExpense();
        }
        else if (e.getSource() == clearAllButton) {
            clearAllExpenses();
        }
        else if (e.getSource() == exitButton) {
            dispose();
            System.exit(0);
        }
    }

    // Main method
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new PersonalFinanceAWT());
    }
}