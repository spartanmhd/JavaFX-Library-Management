package com.example.librarymanagement.util;

import com.example.librarymanagement.dao.BookDAO;
import com.example.librarymanagement.model.Book;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for database operations
 */
public class DatabaseUtil {
    // Database connection parameters
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "mehdi1234";
    
    /**
     * Get a connection to the database
     * @return Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Initialize the database by creating tables if they don't exist
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create members table
            String createMembersTable = "CREATE TABLE IF NOT EXISTS members (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," +
                    "password VARCHAR(50) NOT NULL," +  // Storing passwords in plain text as per requirements
                    "name VARCHAR(100) NOT NULL," +
                    "surname VARCHAR(100) NOT NULL," +
                    "email VARCHAR(100)," +
                    "phone VARCHAR(20)," +
                    "is_admin BOOLEAN DEFAULT FALSE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createMembersTable);
            
            // Create books table
            String createBooksTable = "CREATE TABLE IF NOT EXISTS books (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "title VARCHAR(255) NOT NULL," +
                    "author VARCHAR(255) NOT NULL," +
                    "isbn VARCHAR(20) UNIQUE NOT NULL," +
                    "publication_year INT," +
                    "available BOOLEAN DEFAULT TRUE," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.executeUpdate(createBooksTable);
            
            // Create loans table
            String createLoansTable = "CREATE TABLE IF NOT EXISTS loans (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "book_id INT NOT NULL," +
                    "member_id INT NOT NULL," +
                    "loan_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "due_date TIMESTAMP NOT NULL," +
                    "return_date TIMESTAMP NULL," +
                    "FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE" +
                    ")";
            stmt.executeUpdate(createLoansTable);
            
            // Create default admin user if not exists
            String createAdminUser = "INSERT IGNORE INTO members (username, password, name, surname, is_admin) " +
                    "VALUES ('admin', 'admin123', 'System', 'Administrator', TRUE)";
            stmt.executeUpdate(createAdminUser);
            
            System.out.println("Database initialized successfully");
            
            // Seed sample books
            seedSampleBooks();
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Close the database connection
     * @param conn Connection to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Seed the database with sample books
     */
    private static void seedSampleBooks() {
        BookDAO bookDAO = new BookDAO();
        List<Book> sampleBooks = createSampleBooks();
        
        // Check if books already exist
        if (bookDAO.getAllBooks().isEmpty()) {
            System.out.println("Adding sample books to the database...");
            int addedCount = 0;
            
            for (Book book : sampleBooks) {
                if (bookDAO.addBook(book)) {
                    addedCount++;
                }
            }
            
            System.out.println(addedCount + " sample books added successfully");
        } else {
            System.out.println("Books already exist in the database, skipping sample data");
        }
    }
    
    /**
     * Create a list of sample books
     * @return List of sample Book objects
     */
    private static List<Book> createSampleBooks() {
        List<Book> books = new ArrayList<>();
        
        // Classic Literature
        books.add(new Book("Pride and Prejudice", "Jane Austen", "9780141439518", 1813, true));
        books.add(new Book("To Kill a Mockingbird", "Harper Lee", "9780060935467", 1960, true));
        books.add(new Book("1984", "George Orwell", "9780451524935", 1949, true));
        books.add(new Book("The Great Gatsby", "F. Scott Fitzgerald", "9780743273565", 1925, true));
        books.add(new Book("Moby Dick", "Herman Melville", "9780142437247", 1851, true));
        books.add(new Book("War and Peace", "Leo Tolstoy", "9781400079988", 1869, true));
        books.add(new Book("The Odyssey", "Homer", "9780140268867", -800, true));
        books.add(new Book("Crime and Punishment", "Fyodor Dostoevsky", "9780679734505", 1866, true));
        books.add(new Book("Jane Eyre", "Charlotte Brontë", "9780141441146", 1847, true));
        books.add(new Book("Wuthering Heights", "Emily Brontë", "9780141439556", 1847, true));
        
        // Modern Fiction
        books.add(new Book("Harry Potter and the Philosopher's Stone", "J.K. Rowling", "9780747532743", 1997, true));
        books.add(new Book("The Lord of the Rings", "J.R.R. Tolkien", "9780618640157", 1954, true));
        books.add(new Book("The Hunger Games", "Suzanne Collins", "9780439023481", 2008, true));
        books.add(new Book("The Da Vinci Code", "Dan Brown", "9780307474278", 2003, true));
        books.add(new Book("The Alchemist", "Paulo Coelho", "9780062315007", 1988, true));
        books.add(new Book("The Kite Runner", "Khaled Hosseini", "9781594631931", 2003, true));
        books.add(new Book("Life of Pi", "Yann Martel", "9780156027328", 2001, true));
        books.add(new Book("The Girl with the Dragon Tattoo", "Stieg Larsson", "9780307454546", 2005, true));
        books.add(new Book("Gone Girl", "Gillian Flynn", "9780307588371", 2012, true));
        books.add(new Book("The Fault in Our Stars", "John Green", "9780142424179", 2012, true));
        
        // Non-Fiction
        books.add(new Book("A Brief History of Time", "Stephen Hawking", "9780553380163", 1988, true));
        books.add(new Book("Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "9780062316097", 2011, true));
        books.add(new Book("Thinking, Fast and Slow", "Daniel Kahneman", "9780374533557", 2011, true));
        books.add(new Book("The Immortal Life of Henrietta Lacks", "Rebecca Skloot", "9781400052189", 2010, true));
        books.add(new Book("In Cold Blood", "Truman Capote", "9780679745587", 1966, true));
        books.add(new Book("Educated", "Tara Westover", "9780399590504", 2018, true));
        books.add(new Book("Becoming", "Michelle Obama", "9781524763138", 2018, true));
        books.add(new Book("The Diary of a Young Girl", "Anne Frank", "9780553577129", 1947, true));
        books.add(new Book("Silent Spring", "Rachel Carson", "9780618249060", 1962, true));
        books.add(new Book("Man's Search for Meaning", "Viktor E. Frankl", "9780807014295", 1946, true));
        
        // Science Fiction
        books.add(new Book("Dune", "Frank Herbert", "9780441172719", 1965, true));
        books.add(new Book("Foundation", "Isaac Asimov", "9780553293357", 1951, true));
        books.add(new Book("Neuromancer", "William Gibson", "9780441569595", 1984, true));
        books.add(new Book("Brave New World", "Aldous Huxley", "9780060850524", 1932, true));
        books.add(new Book("The Martian", "Andy Weir", "9780553418026", 2011, true));
        books.add(new Book("Ready Player One", "Ernest Cline", "9780307887443", 2011, true));
        books.add(new Book("Ender's Game", "Orson Scott Card", "9780812550702", 1985, true));
        books.add(new Book("The Hitchhiker's Guide to the Galaxy", "Douglas Adams", "9780345391803", 1979, true));
        books.add(new Book("Snow Crash", "Neal Stephenson", "9780553380958", 1992, true));
        books.add(new Book("The Three-Body Problem", "Liu Cixin", "9780765382030", 2008, true));
        
        // Mystery and Thriller
        books.add(new Book("And Then There Were None", "Agatha Christie", "9780062073488", 1939, true));
        books.add(new Book("The Silence of the Lambs", "Thomas Harris", "9780312924584", 1988, true));
        books.add(new Book("The Hound of the Baskervilles", "Arthur Conan Doyle", "9780451528018", 1902, true));
        books.add(new Book("The Girl on the Train", "Paula Hawkins", "9781594634024", 2015, true));
        books.add(new Book("Rebecca", "Daphne du Maurier", "9780380730407", 1938, true));
        
        return books;
    }
}