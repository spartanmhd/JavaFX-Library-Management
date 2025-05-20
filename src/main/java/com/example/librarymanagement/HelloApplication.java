package com.example.librarymanagement;

import com.example.librarymanagement.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Main application class for the Library Management System
 */
public class HelloApplication extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;

        // Initialize database
        DatabaseUtil.initializeDatabase();

        // Load login screen
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 500);
        stage.setTitle("Library Management System - Login");

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Get the primary stage
     * @return Primary stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch();
    }
}
