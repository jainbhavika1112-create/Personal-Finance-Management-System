-- Create database
CREATE DATABASE personal_finance;

-- Select database
USE personal_finance;

-- Create expenses table
CREATE TABLE expenses (
                          id INT PRIMARY KEY AUTO_INCREMENT,
                          expense_name VARCHAR(100) NOT NULL,
                          amount DECIMAL(10,2) NOT NULL,
                          category VARCHAR(50) NOT NULL
);

-- Check the table
SELECT * FROM expenses;