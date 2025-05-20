package com.example.librarymanagement.controller;

import com.example.librarymanagement.HelloApplication;
import com.example.librarymanagement.dao.MemberDAO;
import com.example.librarymanagement.model.Member;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controller for the registration screen
 */
public class RegisterController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField surnameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private TextField phoneField;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Hyperlink loginLink;
    
    private MemberDAO memberDAO = new MemberDAO();
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Add event handlers
        registerButton.setOnAction(this::handleRegister);
        loginLink.setOnAction(this::handleLogin);
    }
    
    /**
     * Handle register button click
     * @param event ActionEvent
     */
    private void handleRegister(ActionEvent event) {
        // Validate input
        if (!validateInput()) {
            return;
        }
        
        // Check if username already exists
        if (memberDAO.usernameExists(usernameField.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Username already exists. Please choose a different username.");
            return;
        }
        
        // Create new member
        Member member = new Member();
        member.setUsername(usernameField.getText().trim());
        member.setPassword(passwordField.getText().trim());
        member.setName(nameField.getText().trim());
        member.setSurname(surnameField.getText().trim());
        member.setEmail(emailField.getText().trim());
        member.setPhone(phoneField.getText().trim());
        member.setAdmin(false);
        
        // Add member to database
        if (memberDAO.addMember(member)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Your account has been created successfully. You can now login.");
            
            // Navigate back to login screen
            handleLogin(null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "An error occurred while creating your account. Please try again.");
        }
    }
    
    /**
     * Validate registration form input
     * @return true if input is valid, false otherwise
     */
    private boolean validateInput() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        
        // Check required fields
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Please fill in all required fields.");
            return false;
        }
        
        // Check password length
        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Password must be at least 6 characters long.");
            return false;
        }
        
        // Check password match
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Passwords do not match.");
            return false;
        }
        
        // Check username length
        if (username.length() < 4) {
            showAlert(Alert.AlertType.ERROR, "Registration Error", "Username must be at least 4 characters long.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Handle login link click
     * @param event ActionEvent
     */
    private void handleLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);
            
            Stage stage = HelloApplication.getPrimaryStage();
            stage.setTitle("Library Management System - Login");
            stage.setScene(scene);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the login form.");
        }
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