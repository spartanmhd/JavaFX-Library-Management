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
 * Controller for the login screen
 */
public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Hyperlink registerLink;
    
    private MemberDAO memberDAO = new MemberDAO();
    
    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Add event handlers
        loginButton.setOnAction(this::handleLogin);
        registerLink.setOnAction(this::handleRegister);
    }
    
    /**
     * Handle login button click
     * @param event ActionEvent
     */
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter both username and password.");
            return;
        }
        
        // Authenticate user
        Member member = memberDAO.authenticate(username, password);
        
        if (member != null) {
            // Login successful
            try {
                // Store the logged-in user in a session
                SessionManager.getInstance().setCurrentMember(member);
                
                // Load appropriate screen based on user role
                String fxmlFile = member.isAdmin() ? "admin-view.fxml" : "member-view.fxml";
                FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
                Scene scene = new Scene(loader.load(), 800, 600);
                
                Stage stage = HelloApplication.getPrimaryStage();
                stage.setTitle("Library Management System - " + (member.isAdmin() ? "Admin" : "Member"));
                stage.setScene(scene);
                stage.setResizable(true);
                stage.centerOnScreen();
                
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the application.");
            }
        } else {
            // Login failed
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password.");
        }
    }
    
    /**
     * Handle register link click
     * @param event ActionEvent
     */
    private void handleRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("register-view.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);
            
            Stage stage = HelloApplication.getPrimaryStage();
            stage.setTitle("Library Management System - Register");
            stage.setScene(scene);
            stage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the registration form.");
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