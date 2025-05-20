package com.example.librarymanagement.controller;

import com.example.librarymanagement.HelloApplication;
import com.example.librarymanagement.dao.BookDAO;
import com.example.librarymanagement.dao.LoanDAO;
import com.example.librarymanagement.dao.MemberDAO;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Controller for the admin interface
 */
public class AdminController {

    // Books Tab
    @FXML private TabPane tabPane;
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, Integer> bookIdColumn;
    @FXML private TableColumn<Book, String> bookTitleColumn;
    @FXML private TableColumn<Book, String> bookAuthorColumn;
    @FXML private TableColumn<Book, String> bookIsbnColumn;
    @FXML private TableColumn<Book, Integer> bookYearColumn;
    @FXML private TableColumn<Book, String> bookAvailabilityColumn;
    @FXML private TextField bookTitleField;
    @FXML private TextField bookAuthorField;
    @FXML private TextField bookIsbnField;
    @FXML private TextField bookYearField;
    @FXML private TextField bookSearchField;
    @FXML private Button bookAddButton;
    @FXML private Button bookUpdateButton;
    @FXML private Button bookDeleteButton;
    @FXML private Button bookSearchButton;
    @FXML private Button bookClearButton;

    // Members Tab
    @FXML private TableView<Member> membersTable;
    @FXML private TableColumn<Member, Integer> memberIdColumn;
    @FXML private TableColumn<Member, String> memberUsernameColumn;
    @FXML private TableColumn<Member, String> memberNameColumn;
    @FXML private TableColumn<Member, String> memberSurnameColumn;
    @FXML private TableColumn<Member, String> memberEmailColumn;
    @FXML private TableColumn<Member, String> memberPhoneColumn;
    @FXML private TableColumn<Member, Boolean> memberAdminColumn;
    @FXML private TextField memberUsernameField;
    @FXML private PasswordField memberPasswordField;
    @FXML private TextField memberNameField;
    @FXML private TextField memberSurnameField;
    @FXML private TextField memberEmailField;
    @FXML private TextField memberPhoneField;
    @FXML private CheckBox memberAdminCheckbox;
    @FXML private TextField memberSearchField;
    @FXML private Button memberAddButton;
    @FXML private Button memberUpdateButton;
    @FXML private Button memberDeleteButton;
    @FXML private Button memberSearchButton;
    @FXML private Button memberClearButton;

    // Loans Tab
    @FXML private TableView<Loan> loansTable;
    @FXML private TableColumn<Loan, Integer> loanIdColumn;
    @FXML private TableColumn<Loan, String> loanBookColumn;
    @FXML private TableColumn<Loan, String> loanMemberColumn;
    @FXML private TableColumn<Loan, String> loanDateColumn;
    @FXML private TableColumn<Loan, String> loanDueDateColumn;
    @FXML private TableColumn<Loan, String> loanReturnDateColumn;
    @FXML private TableColumn<Loan, String> loanStatusColumn;
    @FXML private ComboBox<Book> loanBookComboBox;
    @FXML private ComboBox<Member> loanMemberComboBox;
    @FXML private ComboBox<Integer> loanDurationComboBox;
    @FXML private Button loanAddButton;
    @FXML private Button loanReturnButton;
    @FXML private Button loanDeleteButton;
    @FXML private Button loanClearButton;
    @FXML private Button logoutButton;

    private BookDAO bookDAO = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();
    private LoanDAO loanDAO = new LoanDAO();

    private ObservableList<Book> booksList = FXCollections.observableArrayList();
    private ObservableList<Member> membersList = FXCollections.observableArrayList();
    private ObservableList<Loan> loansList = FXCollections.observableArrayList();

    private Book selectedBook;
    private Member selectedMember;
    private Loan selectedLoan;

    /**
     * Initialize the controller
     */
    @FXML
    public void initialize() {
        initializeBooksTab();
        initializeMembersTab();
        initializeLoansTab();

        // Add event handler for logout button
        logoutButton.setOnAction(this::handleLogout);
    }

    /**
     * Initialize the Books Tab
     */
    private void initializeBooksTab() {
        // Set up the table columns
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        bookIsbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        bookYearColumn.setCellValueFactory(new PropertyValueFactory<>("publicationYear"));
        bookAvailabilityColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().isAvailable() ? "Available" : "Borrowed"));

        // Ensure columns are visible and properly sized
        bookIdColumn.setVisible(true);
        bookTitleColumn.setVisible(true);
        bookAuthorColumn.setVisible(true);
        bookIsbnColumn.setVisible(true);
        bookYearColumn.setVisible(true);
        bookAvailabilityColumn.setVisible(true);

        // Set minimum width for columns
        bookIdColumn.setMinWidth(50);
        bookTitleColumn.setMinWidth(250);
        bookAuthorColumn.setMinWidth(200);
        bookIsbnColumn.setMinWidth(150);
        bookYearColumn.setMinWidth(100);
        bookAvailabilityColumn.setMinWidth(100);

        // Add event handlers
        bookAddButton.setOnAction(this::handleAddBook);
        bookUpdateButton.setOnAction(this::handleUpdateBook);
        bookDeleteButton.setOnAction(this::handleDeleteBook);
        bookSearchButton.setOnAction(this::handleSearchBook);
        bookClearButton.setOnAction(e -> clearBookFields());

        // Add selection listener
        booksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedBook = newSelection;
                populateBookFields(newSelection);
            }
        });

        // Load books
        loadBooks();
    }

    /**
     * Initialize the Members Tab
     */
    private void initializeMembersTab() {
        // Set up the table columns
        memberIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        memberUsernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        memberNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        memberSurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        memberEmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        memberPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        memberAdminColumn.setCellValueFactory(new PropertyValueFactory<>("admin"));

        // Ensure columns are visible and properly sized
        memberIdColumn.setVisible(true);
        memberUsernameColumn.setVisible(true);
        memberNameColumn.setVisible(true);
        memberSurnameColumn.setVisible(true);
        memberEmailColumn.setVisible(true);
        memberPhoneColumn.setVisible(true);
        memberAdminColumn.setVisible(true);

        // Set minimum width for columns
        memberIdColumn.setMinWidth(50);
        memberUsernameColumn.setMinWidth(120);
        memberNameColumn.setMinWidth(150);
        memberSurnameColumn.setMinWidth(150);
        memberEmailColumn.setMinWidth(200);
        memberPhoneColumn.setMinWidth(120);
        memberAdminColumn.setMinWidth(60);

        // Add event handlers
        memberAddButton.setOnAction(this::handleAddMember);
        memberUpdateButton.setOnAction(this::handleUpdateMember);
        memberDeleteButton.setOnAction(this::handleDeleteMember);
        memberSearchButton.setOnAction(this::handleSearchMember);
        memberClearButton.setOnAction(e -> clearMemberFields());

        // Add selection listener
        membersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMember = newSelection;
                populateMemberFields(newSelection);
            }
        });

        // Load members
        loadMembers();
    }

    /**
     * Initialize the Loans Tab
     */
    private void initializeLoansTab() {
        // Set up the table columns
        loanIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loanBookColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook() != null ? 
                    cellData.getValue().getBook().getTitle() : "Unknown"));
        loanMemberColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getMember() != null ? 
                    cellData.getValue().getMember().getFullName() : "Unknown"));
        loanDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatDateTime(cellData.getValue().getLoanDate())));
        loanDueDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatDateTime(cellData.getValue().getDueDate())));
        loanReturnDateColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getReturnDate() != null ? 
                    formatDateTime(cellData.getValue().getReturnDate()) : "Not returned"));
        loanStatusColumn.setCellValueFactory(cellData -> {
            Loan loan = cellData.getValue();
            if (loan.isReturned()) {
                return new SimpleStringProperty("Returned");
            } else if (loan.isOverdue()) {
                return new SimpleStringProperty("Overdue");
            } else {
                return new SimpleStringProperty("Active");
            }
        });

        // Ensure columns are visible and properly sized
        loanIdColumn.setVisible(true);
        loanBookColumn.setVisible(true);
        loanMemberColumn.setVisible(true);
        loanDateColumn.setVisible(true);
        loanDueDateColumn.setVisible(true);
        loanReturnDateColumn.setVisible(true);
        loanStatusColumn.setVisible(true);

        // Set minimum width for columns
        loanIdColumn.setMinWidth(50);
        loanBookColumn.setMinWidth(200);
        loanMemberColumn.setMinWidth(200);
        loanDateColumn.setMinWidth(120);
        loanDueDateColumn.setMinWidth(120);
        loanReturnDateColumn.setMinWidth(120);
        loanStatusColumn.setMinWidth(100);

        // Set up combo boxes
        loanDurationComboBox.getItems().addAll(7, 14, 21, 30);
        loanDurationComboBox.setValue(14); // Default to 14 days

        // Add event handlers
        loanAddButton.setOnAction(this::handleAddLoan);
        loanReturnButton.setOnAction(this::handleReturnLoan);
        loanDeleteButton.setOnAction(this::handleDeleteLoan);
        loanClearButton.setOnAction(e -> clearLoanFields());

        // Add selection listener
        loansTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedLoan = newSelection;
            }
        });

        // Load loans, books, and members for combo boxes
        loadLoans();
        loadBooksForComboBox();
        loadMembersForComboBox();
    }

    // Data loading methods
    private void loadBooks() {
        List<Book> books = bookDAO.getAllBooks();
        booksList.clear();
        booksList.addAll(books);
        booksTable.setItems(booksList);
    }

    private void loadMembers() {
        List<Member> members = memberDAO.getAllMembers();
        membersList.clear();
        membersList.addAll(members);
        membersTable.setItems(membersList);
    }

    private void loadLoans() {
        List<Loan> loans = loanDAO.getAllLoans();
        loansList.clear();
        loansList.addAll(loans);
        loansTable.setItems(loansList);
    }

    private void loadBooksForComboBox() {
        List<Book> availableBooks = bookDAO.getAllBooks().stream()
                .filter(Book::isAvailable)
                .toList();
        loanBookComboBox.getItems().clear();
        loanBookComboBox.getItems().addAll(availableBooks);

        // Set cell factory to display book title
        loanBookComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });

        // Set button cell to display book title
        loanBookComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Book item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTitle());
            }
        });
    }

    private void loadMembersForComboBox() {
        List<Member> members = memberDAO.getAllMembers();
        loanMemberComboBox.getItems().clear();
        loanMemberComboBox.getItems().addAll(members);

        // Set cell factory to display member name
        loanMemberComboBox.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });

        // Set button cell to display member name
        loanMemberComboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Member item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFullName());
            }
        });
    }

    // Field population and clearing methods
    private void populateBookFields(Book book) {
        bookTitleField.setText(book.getTitle());
        bookAuthorField.setText(book.getAuthor());
        bookIsbnField.setText(book.getIsbn());
        bookYearField.setText(String.valueOf(book.getPublicationYear()));
    }

    private void populateMemberFields(Member member) {
        memberUsernameField.setText(member.getUsername());
        memberPasswordField.clear(); // Don't show password
        memberNameField.setText(member.getName());
        memberSurnameField.setText(member.getSurname());
        memberEmailField.setText(member.getEmail());
        memberPhoneField.setText(member.getPhone());
        memberAdminCheckbox.setSelected(member.isAdmin());
    }

    private void clearBookFields() {
        bookTitleField.clear();
        bookAuthorField.clear();
        bookIsbnField.clear();
        bookYearField.clear();
        bookSearchField.clear();
        selectedBook = null;
        booksTable.getSelectionModel().clearSelection();
    }

    private void clearMemberFields() {
        memberUsernameField.clear();
        memberPasswordField.clear();
        memberNameField.clear();
        memberSurnameField.clear();
        memberEmailField.clear();
        memberPhoneField.clear();
        memberAdminCheckbox.setSelected(false);
        memberSearchField.clear();
        selectedMember = null;
        membersTable.getSelectionModel().clearSelection();
    }

    private void clearLoanFields() {
        loanBookComboBox.getSelectionModel().clearSelection();
        loanMemberComboBox.getSelectionModel().clearSelection();
        loanDurationComboBox.setValue(14);
        selectedLoan = null;
        loansTable.getSelectionModel().clearSelection();
    }

    // Event handlers for Books tab
    private void handleAddBook(ActionEvent event) {
        if (!validateBookInput()) return;

        Book existingBook = bookDAO.getBookByIsbn(bookIsbnField.getText().trim());
        if (existingBook != null) {
            showAlert(Alert.AlertType.ERROR, "Error", "A book with this ISBN already exists.");
            return;
        }

        Book book = new Book();
        book.setTitle(bookTitleField.getText().trim());
        book.setAuthor(bookAuthorField.getText().trim());
        book.setIsbn(bookIsbnField.getText().trim());
        book.setPublicationYear(Integer.parseInt(bookYearField.getText().trim()));
        book.setAvailable(true);

        if (bookDAO.addBook(book)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book added successfully.");
            clearBookFields();
            loadBooks();
            loadBooksForComboBox();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the book.");
        }
    }

    private void handleUpdateBook(ActionEvent event) {
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to update.");
            return;
        }

        if (!validateBookInput()) return;

        Book existingBook = bookDAO.getBookByIsbn(bookIsbnField.getText().trim());
        if (existingBook != null && existingBook.getId() != selectedBook.getId()) {
            showAlert(Alert.AlertType.ERROR, "Error", "A book with this ISBN already exists.");
            return;
        }

        selectedBook.setTitle(bookTitleField.getText().trim());
        selectedBook.setAuthor(bookAuthorField.getText().trim());
        selectedBook.setIsbn(bookIsbnField.getText().trim());
        selectedBook.setPublicationYear(Integer.parseInt(bookYearField.getText().trim()));

        if (bookDAO.updateBook(selectedBook)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book updated successfully.");
            clearBookFields();
            loadBooks();
            loadBooksForComboBox();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the book.");
        }
    }

    private void handleDeleteBook(ActionEvent event) {
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a book to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the book: " + selectedBook.getTitle() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (bookDAO.deleteBook(selectedBook.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Book deleted successfully.");
                clearBookFields();
                loadBooks();
                loadBooksForComboBox();
                loadLoans();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the book.");
            }
        }
    }

    private void handleSearchBook(ActionEvent event) {
        String keyword = bookSearchField.getText().trim();

        if (keyword.isEmpty()) {
            loadBooks();
            return;
        }

        List<Book> searchResults = bookDAO.searchBooks(keyword);
        booksList.clear();
        booksList.addAll(searchResults);
        booksTable.setItems(booksList);
    }

    // Event handlers for Members tab
    private void handleAddMember(ActionEvent event) {
        if (!validateMemberInput(true)) return;

        if (memberDAO.usernameExists(memberUsernameField.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists. Please choose a different username.");
            return;
        }

        Member member = new Member();
        member.setUsername(memberUsernameField.getText().trim());
        member.setPassword(memberPasswordField.getText().trim());
        member.setName(memberNameField.getText().trim());
        member.setSurname(memberSurnameField.getText().trim());
        member.setEmail(memberEmailField.getText().trim());
        member.setPhone(memberPhoneField.getText().trim());
        member.setAdmin(memberAdminCheckbox.isSelected());

        if (memberDAO.addMember(member)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member added successfully.");
            clearMemberFields();
            loadMembers();
            loadMembersForComboBox();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while adding the member.");
        }
    }

    private void handleUpdateMember(ActionEvent event) {
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to update.");
            return;
        }

        if (!validateMemberInput(false)) return;

        String username = memberUsernameField.getText().trim();
        if (!username.equals(selectedMember.getUsername()) && memberDAO.usernameExists(username)) {
            showAlert(Alert.AlertType.ERROR, "Error", "Username already exists. Please choose a different username.");
            return;
        }

        selectedMember.setUsername(username);
        selectedMember.setName(memberNameField.getText().trim());
        selectedMember.setSurname(memberSurnameField.getText().trim());
        selectedMember.setEmail(memberEmailField.getText().trim());
        selectedMember.setPhone(memberPhoneField.getText().trim());
        selectedMember.setAdmin(memberAdminCheckbox.isSelected());

        String password = memberPasswordField.getText().trim();
        if (!password.isEmpty()) {
            selectedMember.setPassword(password);
        }

        if (memberDAO.updateMember(selectedMember)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Member updated successfully.");
            clearMemberFields();
            loadMembers();
            loadMembersForComboBox();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while updating the member.");
        }
    }

    private void handleDeleteMember(ActionEvent event) {
        if (selectedMember == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a member to delete.");
            return;
        }

        Member currentMember = SessionManager.getInstance().getCurrentMember();
        if (currentMember != null && currentMember.getId() == selectedMember.getId()) {
            showAlert(Alert.AlertType.ERROR, "Error", "You cannot delete your own account.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete the member: " + selectedMember.getFullName() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (memberDAO.deleteMember(selectedMember.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Member deleted successfully.");
                clearMemberFields();
                loadMembers();
                loadMembersForComboBox();
                loadLoans();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the member.");
            }
        }
    }

    private void handleSearchMember(ActionEvent event) {
        String keyword = memberSearchField.getText().trim();

        if (keyword.isEmpty()) {
            loadMembers();
            return;
        }

        List<Member> searchResults = memberDAO.searchMembers(keyword);
        membersList.clear();
        membersList.addAll(searchResults);
        membersTable.setItems(membersList);
    }

    // Event handlers for Loans tab
    private void handleAddLoan(ActionEvent event) {
        Book book = loanBookComboBox.getValue();
        Member member = loanMemberComboBox.getValue();
        Integer duration = loanDurationComboBox.getValue();

        if (book == null || member == null || duration == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please select a book, member, and loan duration.");
            return;
        }

        LocalDateTime dueDate = LocalDateTime.now().plusDays(duration);
        Loan loan = new Loan(book.getId(), member.getId(), dueDate);

        if (loanDAO.addLoan(loan)) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Loan created successfully.");
            clearLoanFields();
            loadLoans();
            loadBooks();
            loadBooksForComboBox();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while creating the loan.");
        }
    }

    private void handleReturnLoan(ActionEvent event) {
        if (selectedLoan == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a loan to return.");
            return;
        }

        if (selectedLoan.isReturned()) {
            showAlert(Alert.AlertType.WARNING, "Already Returned", "This book has already been returned.");
            return;
        }

        if (loanDAO.returnBook(selectedLoan.getId())) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Book returned successfully.");
            loadLoans();
            loadBooks();
            loadBooksForComboBox();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while returning the book.");
        }
    }

    private void handleDeleteLoan(ActionEvent event) {
        if (selectedLoan == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a loan to delete.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to delete this loan record?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (loanDAO.deleteLoan(selectedLoan.getId())) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Loan deleted successfully.");
                loadLoans();
                loadBooks();
                loadBooksForComboBox();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "An error occurred while deleting the loan.");
            }
        }
    }

    // Validation methods
    private boolean validateBookInput() {
        String title = bookTitleField.getText().trim();
        String author = bookAuthorField.getText().trim();
        String isbn = bookIsbnField.getText().trim();
        String yearStr = bookYearField.getText().trim();

        if (title.isEmpty() || author.isEmpty() || isbn.isEmpty() || yearStr.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "All fields are required.");
            return false;
        }

        try {
            int year = Integer.parseInt(yearStr);
            if (year < 1000 || year > LocalDateTime.now().getYear()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter a valid publication year.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Publication year must be a number.");
            return false;
        }

        return true;
    }

    private boolean validateMemberInput(boolean isNewMember) {
        String username = memberUsernameField.getText().trim();
        String password = memberPasswordField.getText().trim();
        String name = memberNameField.getText().trim();
        String surname = memberSurnameField.getText().trim();

        if (username.isEmpty() || name.isEmpty() || surname.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Username, name, and surname are required.");
            return false;
        }

        if (isNewMember && password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password is required for new members.");
            return false;
        }

        if (!password.isEmpty() && password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Password must be at least 6 characters long.");
            return false;
        }

        return true;
    }

    // Utility methods
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void handleLogout(ActionEvent event) {
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
}
