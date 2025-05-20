package com.example.librarymanagement.controller;

import com.example.librarymanagement.HelloApplication;
import com.example.librarymanagement.dao.LoanDAO;
import com.example.librarymanagement.dao.MemberDAO;
import com.example.librarymanagement.model.Loan;
import com.example.librarymanagement.model.Member;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller for the user profile page
 */
public class ProfileController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField surnameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TableView<Loan> loansTable;
    
    @FXML
    private TableColumn<Loan, String> bookTitleColumn;
    
    @FXML
    private TableColumn<Loan, String> loanDateColumn;
    
    @FXML
    private TableColumn<Loan, String> dueDateColumn;
    
    @FXML
    private TableColumn<Loan, String> returnDateColumn;
    
    @FXML
    private TableColumn<Loan, String> statusColumn;
    
    @FXML
    private Button updateButton;
    
    @FXML
    private Button returnButton;
    
    @FXML
    private Button backButton;
    
    private MemberDAO memberDAO = new MemberDAO();
    private LoanDAO loanDAO = new LoanDAO();
    private ObservableList<Loan> loansList = FXCollections.observableArrayList();
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Set up the table columns
        bookTitleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook() != null ? 
                    cellData.getValue().getBook().getTitle() : "Unknown"));
        
        loanDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatDateTime(cellData.getValue().getLoanDate())));
        
        dueDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatDateTime(cellData.getValue().getDueDate())));
        
        returnDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getReturnDate() != null ? 
                    formatDateTime(cellData.getValue().getReturnDate()) : "Not returned"));
        
        statusColumn.setCellValueFactory(cellData -> {
            Loan loan = cellData.getValue();
            if (loan.isReturned()) {
                return new SimpleStringProperty("Returned");
            } else if (loan.isOverdue()) {
                return new SimpleStringProperty("Overdue");
            } else {
                return new SimpleStringProperty("Active");
            }
        });
        
        // Add event handlers
        updateButton.setOnAction(this::handleUpdate);
        returnButton.setOnAction(this::handleReturn);
        backButton.setOnAction(this::handleBack);
        
        // Load member data
        loadMemberData();
        
        // Load loans
        loadLoans();
    }
    
    /**
     * Load member data into form fields
     */
    private void loadMemberData() {
        Member currentMember = SessionManager.getInstance().getCurrentMember();
        if (currentMember != null) {
            usernameField.setText(currentMember.getUsername());
            nameField.setText(currentMember.getName());
            surnameField.setText(currentMember.getSurname());
            emailField.setText(currentMember.getEmail());
            phoneField.setText(currentMember.getPhone());
            
            // Username field should be disabled (cannot be changed)
            usernameField.setDisable(true);
        }
    }
    
    /**
     * Load loans for the current member
     */
    private void loadLoans() {
        Member currentMember = SessionManager.getInstance().getCurrentMember();
        if (currentMember != null) {
            List<Loan> loans = loanDAO.getLoansByMemberId(currentMember.getId());
            loansList.clear();
            loansList.addAll(loans);
            loansTable.setItems(loansList);
        }
    }
    
    /**
     * Handle update button click
     * @param event ActionEvent
     */
    private void handleUpdate(ActionEvent event) {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        // Get current member
        Member currentMember = SessionManager.getInstance().getCurrentMember();
        if (currentMember == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No user is currently logged in.");
            return;
        }
        
        // Update member data
        currentMember.setName(nameField.getText().trim());
        currentMember.setSurname(surnameField.getText().trim());
        currentMember.setEmail(emailField.getText().trim());
        currentMember.setPhone(phoneField.getText().trim());
        
        // Update password if provided
        String password = passwordField.getText().trim();
        if (!password.isEmpty()) {
            currentMember.setPassword(password);
        }
        
        // Update in database
        if (memberDAO.updateMember(currentMember)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Your profile has been updated successfully.");
            
            // Clear password fields
            passwordField.clear();
            confirmPasswordField.clear();
            
            // Update session
            SessionManager.getInstance().setCurrentMember(currentMember);
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating your profile.");
        }
    }
    
    /**
     * Handle return button click
     * @param event ActionEvent
     */
    private void handleReturn(ActionEvent event) {
        Loan selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        
        if (selectedLoan == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a loan to return.");
            return;
        }
        
        if (selectedLoan.isReturned()) {
            showAlert(Alert.AlertType.WARNING, "Already Returned", "This book has already been returned.");
            return;
        }
        
        // Return book
        if (loanDAO.returnBook(selectedLoan.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
            
            // Refresh loans
            loadLoans();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while returning the book.");
        }
    }
    
    /**
     * Handle back button click
     * @param event ActionEvent
     */
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("member-view.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            
            Stage stage = HelloApplication.getPrimaryStage();
            stage.setTitle("Library Management System - Member");
            stage.setScene(scene);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while navigating back.");
        }
    }
    
    /**
     * Validate form input
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput() {
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        
        // Check required fields
        if (name.isEmpty() || surname.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Name and surname are required fields.");
            return false;
        }
        
        // Check password match if provided
        if (!password.isEmpty() && !password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Passwords do not match.");
            return false;
        }
        
        // Check password length if provided
        if (!password.isEmpty() && password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 6 characters long.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Format LocalDateTime to string
     * @param dateTime LocalDateTime to format
     * @return Formatted date string
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
    
    /**
     * Show an alert dialog
     * @param type Alert type
     * @param title Alert title
     * @param message Alert message
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}