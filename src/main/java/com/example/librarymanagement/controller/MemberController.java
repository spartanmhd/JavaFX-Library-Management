package com.example.librarymanagement.controller;

import com.example.librarymanagement.HelloApplication;
import com.example.librarymanagement.dao.BookDAO;
import com.example.librarymanagement.dao.LoanDAO;
import com.example.librarymanagement.model.Book;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the member interface
 */
public class MemberController {

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, Integer> idColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> isbnColumn;

    @FXML
    private TableColumn<Book, Integer> yearColumn;

    @FXML
    private TableColumn<Book, String> availabilityColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button borrowButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Label welcomeLabel;

    private BookDAO bookDAO = new BookDAO();
    private LoanDAO loanDAO = new LoanDAO();
    private ObservableList<Book> booksList = FXCollections.observableArrayList();

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        // Set up the table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        availabilityColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isAvailable() ? "Available" : "Borrowed"));

        // Ensure columns are visible and properly sized
        idColumn.setVisible(true);
        titleColumn.setVisible(true);
        authorColumn.setVisible(true);
        isbnColumn.setVisible(true);
        yearColumn.setVisible(true);
        availabilityColumn.setVisible(true);

        // Set minimum width for columns
        idColumn.setMinWidth(50);
        titleColumn.setMinWidth(200);
        authorColumn.setMinWidth(150);
        isbnColumn.setMinWidth(120);
        yearColumn.setMinWidth(100);
        availabilityColumn.setMinWidth(100);

        // Add event handlers
        searchButton.setOnAction(this::handleSearch);
        profileButton.setOnAction(this::handleProfile);
        borrowButton.setOnAction(this::handleBorrow);
        logoutButton.setOnAction(this::handleLogout);

        // Load books
        loadBooks();

        // Set welcome message
        Member currentMember = SessionManager.getInstance().getCurrentMember();
        if (currentMember != null) {
            welcomeLabel.setText("Welcome, " + currentMember.getFullName() + "!");
        }
    }

    /**
     * Load all available books
     */
    private void loadBooks() {
        List<Book> books = bookDAO.getAllBooks();
        booksList.clear();
        booksList.addAll(books);
        booksTable.setItems(booksList);
    }

    /**
     * Handle search button click
     * @param event ActionEvent
     */
    private void handleSearch(ActionEvent event) {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }

        List<Book> searchResults = bookDAO.searchBooks(keyword);
        booksList.clear();
        booksList.addAll(searchResults);
        booksTable.setItems(booksList);
    }

    /**
     * Handle profile button click
     * @param event ActionEvent
     */
    private void handleProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("profile-view.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);

            Stage stage = HelloApplication.getPrimaryStage();
            stage.setTitle("Library Management System - Profile");
            stage.setScene(scene);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while loading the profile page.");
        }
    }

    /**
     * Handle borrow button click
     * @param event ActionEvent
     */
    private void handleBorrow(ActionEvent event) {
        Book selectedBook = booksTable.getSelectionModel().getSelectedItem();

        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to borrow.");
            return;
        }

        if (!selectedBook.isAvailable()) {
            showAlert(Alert.AlertType.WARNING, "Book Not Available", "This book is currently not available for borrowing.");
            return;
        }

        // Show dialog to select loan duration
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Borrow Book");
        dialog.setHeaderText("Select loan duration for: " + selectedBook.getTitle());

        ButtonType borrowButtonType = new ButtonType("Borrow", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(borrowButtonType, ButtonType.CANCEL);

        ComboBox<Integer> daysComboBox = new ComboBox<>();
        daysComboBox.getItems().addAll(7, 14, 21, 30);
        daysComboBox.setValue(14); // Default to 14 days

        dialog.getDialogPane().setContent(new HBox(10, new Label("Loan duration (days):"), daysComboBox));

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == borrowButtonType) {
                return daysComboBox.getValue();
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();

        result.ifPresent(days -> {
            // Create loan
            Member currentMember = SessionManager.getInstance().getCurrentMember();
            LocalDateTime dueDate = LocalDateTime.now().plusDays(days);

            Loan loan = new Loan(selectedBook.getId(), currentMember.getId(), dueDate);

            if (loanDAO.addLoan(loan)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "You have successfully borrowed: " + selectedBook.getTitle() + "\n" +
                        "Due date: " + dueDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

                // Refresh books list
                loadBooks();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while borrowing the book.");
            }
        });
    }

    /**
     * Handle logout button click
     * @param event ActionEvent
     */
    private void handleLogout(ActionEvent event) {
        // Clear session
        SessionManager.getInstance().clearSession();

        try {
            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
            Scene scene = new Scene(loader.load(), 600, 400);

            Stage stage = HelloApplication.getPrimaryStage();
            stage.setTitle("Library Management System - Login");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while logging out.");
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
