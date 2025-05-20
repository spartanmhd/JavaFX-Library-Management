package com.example.librarymanagement.dao;

import com.example.librarymanagement.model.Book;
import com.example.librarymanagement.model.Loan;
import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Loan entity
 */
public class LoanDAO {
    private BookDAO bookDAO = new BookDAO();
    private MemberDAO memberDAO = new MemberDAO();
    
    /**
     * Get all loans from the database
     * @return List of all loans
     */
    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all loans: " + e.getMessage());
        }
        
        return loans;
    }
    
    /**
     * Get a loan by its ID
     * @param id Loan ID
     * @return Loan object if found, null otherwise
     */
    public Loan getLoanById(int id) {
        String query = "SELECT * FROM loans WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToLoan(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting loan by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all loans for a specific member
     * @param memberId Member ID
     * @return List of loans for the member
     */
    public List<Loan> getLoansByMemberId(int memberId) {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE member_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = mapResultSetToLoan(rs);
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting loans by member ID: " + e.getMessage());
        }
        
        return loans;
    }
    
    /**
     * Get all loans for a specific book
     * @param bookId Book ID
     * @return List of loans for the book
     */
    public List<Loan> getLoansByBookId(int bookId) {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE book_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, bookId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = mapResultSetToLoan(rs);
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting loans by book ID: " + e.getMessage());
        }
        
        return loans;
    }
    
    /**
     * Get all active (not returned) loans
     * @return List of active loans
     */
    public List<Loan> getActiveLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE return_date IS NULL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting active loans: " + e.getMessage());
        }
        
        return loans;
    }
    
    /**
     * Get all active loans for a specific member
     * @param memberId Member ID
     * @return List of active loans for the member
     */
    public List<Loan> getActiveLoansByMemberId(int memberId) {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE member_id = ? AND return_date IS NULL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, memberId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = mapResultSetToLoan(rs);
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active loans by member ID: " + e.getMessage());
        }
        
        return loans;
    }
    
    /**
     * Get all overdue loans
     * @return List of overdue loans
     */
    public List<Loan> getOverdueLoans() {
        List<Loan> loans = new ArrayList<>();
        String query = "SELECT * FROM loans WHERE return_date IS NULL AND due_date < NOW()";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Loan loan = mapResultSetToLoan(rs);
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error getting overdue loans: " + e.getMessage());
        }
        
        return loans;
    }
    
    /**
     * Add a new loan to the database
     * @param loan Loan to add
     * @return true if successful, false otherwise
     */
    public boolean addLoan(Loan loan) {
        String query = "INSERT INTO loans (book_id, member_id, loan_date, due_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getMemberId());
            pstmt.setTimestamp(3, Timestamp.valueOf(loan.getLoanDate()));
            pstmt.setTimestamp(4, Timestamp.valueOf(loan.getDueDate()));
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        loan.setId(generatedKeys.getInt(1));
                        
                        // Update book availability
                        bookDAO.updateBookAvailability(loan.getBookId(), false);
                        
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding loan: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update an existing loan in the database
     * @param loan Loan to update
     * @return true if successful, false otherwise
     */
    public boolean updateLoan(Loan loan) {
        String query = "UPDATE loans SET book_id = ?, member_id = ?, loan_date = ?, due_date = ?, return_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, loan.getBookId());
            pstmt.setInt(2, loan.getMemberId());
            pstmt.setTimestamp(3, Timestamp.valueOf(loan.getLoanDate()));
            pstmt.setTimestamp(4, Timestamp.valueOf(loan.getDueDate()));
            
            if (loan.getReturnDate() != null) {
                pstmt.setTimestamp(5, Timestamp.valueOf(loan.getReturnDate()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }
            
            pstmt.setInt(6, loan.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating loan: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Mark a loan as returned
     * @param loanId Loan ID
     * @return true if successful, false otherwise
     */
    public boolean returnBook(int loanId) {
        String query = "UPDATE loans SET return_date = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setInt(2, loanId);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                // Get the loan to update book availability
                Loan loan = getLoanById(loanId);
                if (loan != null) {
                    // Update book availability
                    bookDAO.updateBookAvailability(loan.getBookId(), true);
                }
                
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete a loan from the database
     * @param id ID of the loan to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteLoan(int id) {
        String query = "DELETE FROM loans WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting loan: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Map a ResultSet row to a Loan object
     * @param rs ResultSet containing loan data
     * @return Loan object
     * @throws SQLException if a database access error occurs
     */
    private Loan mapResultSetToLoan(ResultSet rs) throws SQLException {
        Loan loan = new Loan();
        loan.setId(rs.getInt("id"));
        loan.setBookId(rs.getInt("book_id"));
        loan.setMemberId(rs.getInt("member_id"));
        
        Timestamp loanDate = rs.getTimestamp("loan_date");
        if (loanDate != null) {
            loan.setLoanDate(loanDate.toLocalDateTime());
        }
        
        Timestamp dueDate = rs.getTimestamp("due_date");
        if (dueDate != null) {
            loan.setDueDate(dueDate.toLocalDateTime());
        }
        
        Timestamp returnDate = rs.getTimestamp("return_date");
        if (returnDate != null) {
            loan.setReturnDate(returnDate.toLocalDateTime());
        }
        
        // Load associated book and member
        Book book = bookDAO.getBookById(loan.getBookId());
        if (book != null) {
            loan.setBook(book);
        }
        
        Member member = memberDAO.getMemberById(loan.getMemberId());
        if (member != null) {
            loan.setMember(member);
        }
        
        return loan;
    }
}