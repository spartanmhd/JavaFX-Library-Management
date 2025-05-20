package com.example.librarymanagement.dao;

import com.example.librarymanagement.model.Book;
import com.example.librarymanagement.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Book entity
 */
public class BookDAO {
    
    /**
     * Get all books from the database
     * @return List of all books
     */
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Book book = mapResultSetToBook(rs);
                books.add(book);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Get a book by its ID
     * @param id Book ID
     * @return Book object if found, null otherwise
     */
    public Book getBookById(int id) {
        String query = "SELECT * FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get a book by its ISBN
     * @param isbn Book ISBN
     * @return Book object if found, null otherwise
     */
    public Book getBookByIsbn(String isbn) {
        String query = "SELECT * FROM books WHERE isbn = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, isbn);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBook(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting book by ISBN: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Search for books by keyword in title, author, or ISBN
     * @param keyword Search keyword
     * @return List of books matching the search criteria
     */
    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT * FROM books WHERE title LIKE ? OR author LIKE ? OR isbn LIKE ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Book book = mapResultSetToBook(rs);
                    books.add(book);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching books: " + e.getMessage());
        }
        
        return books;
    }
    
    /**
     * Add a new book to the database
     * @param book Book to add
     * @return true if successful, false otherwise
     */
    public boolean addBook(Book book) {
        String query = "INSERT INTO books (title, author, isbn, publication_year, available) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getPublicationYear());
            pstmt.setBoolean(5, book.isAvailable());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        book.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update an existing book in the database
     * @param book Book to update
     * @return true if successful, false otherwise
     */
    public boolean updateBook(Book book) {
        String query = "UPDATE books SET title = ?, author = ?, isbn = ?, publication_year = ?, available = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, book.getTitle());
            pstmt.setString(2, book.getAuthor());
            pstmt.setString(3, book.getIsbn());
            pstmt.setInt(4, book.getPublicationYear());
            pstmt.setBoolean(5, book.isAvailable());
            pstmt.setInt(6, book.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete a book from the database
     * @param id ID of the book to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteBook(int id) {
        String query = "DELETE FROM books WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting book: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update the availability status of a book
     * @param bookId ID of the book
     * @param available New availability status
     * @return true if successful, false otherwise
     */
    public boolean updateBookAvailability(int bookId, boolean available) {
        String query = "UPDATE books SET available = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setBoolean(1, available);
            pstmt.setInt(2, bookId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating book availability: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Map a ResultSet row to a Book object
     * @param rs ResultSet containing book data
     * @return Book object
     * @throws SQLException if a database access error occurs
     */
    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        Book book = new Book();
        book.setId(rs.getInt("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setIsbn(rs.getString("isbn"));
        book.setPublicationYear(rs.getInt("publication_year"));
        book.setAvailable(rs.getBoolean("available"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            book.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return book;
    }
}