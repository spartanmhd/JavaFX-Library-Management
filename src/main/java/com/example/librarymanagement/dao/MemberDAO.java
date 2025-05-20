package com.example.librarymanagement.dao;

import com.example.librarymanagement.model.Member;
import com.example.librarymanagement.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Member entity
 */
public class MemberDAO {
    
    /**
     * Get all members from the database
     * @return List of all members
     */
    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM members";
        
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                Member member = mapResultSetToMember(rs);
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all members: " + e.getMessage());
        }
        
        return members;
    }
    
    /**
     * Get a member by ID
     * @param id Member ID
     * @return Member object if found, null otherwise
     */
    public Member getMemberById(int id) {
        String query = "SELECT * FROM members WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member by ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get a member by username
     * @param username Member username
     * @return Member object if found, null otherwise
     */
    public Member getMemberByUsername(String username) {
        String query = "SELECT * FROM members WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member by username: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Authenticate a member by username and password
     * @param username Member username
     * @param password Member password (plain text)
     * @return Member object if authentication successful, null otherwise
     */
    public Member authenticate(String username, String password) {
        String query = "SELECT * FROM members WHERE username = ? AND password = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToMember(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating member: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Search for members by keyword in name, surname, or username
     * @param keyword Search keyword
     * @return List of members matching the search criteria
     */
    public List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM members WHERE name LIKE ? OR surname LIKE ? OR username LIKE ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            String searchTerm = "%" + keyword + "%";
            pstmt.setString(1, searchTerm);
            pstmt.setString(2, searchTerm);
            pstmt.setString(3, searchTerm);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Member member = mapResultSetToMember(rs);
                    members.add(member);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
        }
        
        return members;
    }
    
    /**
     * Add a new member to the database
     * @param member Member to add
     * @return true if successful, false otherwise
     */
    public boolean addMember(Member member) {
        String query = "INSERT INTO members (username, password, name, surname, email, phone, is_admin) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, member.getUsername());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getSurname());
            pstmt.setString(5, member.getEmail());
            pstmt.setString(6, member.getPhone());
            pstmt.setBoolean(7, member.isAdmin());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        member.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Update an existing member in the database
     * @param member Member to update
     * @return true if successful, false otherwise
     */
    public boolean updateMember(Member member) {
        String query = "UPDATE members SET username = ?, password = ?, name = ?, surname = ?, email = ?, phone = ?, is_admin = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, member.getUsername());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getName());
            pstmt.setString(4, member.getSurname());
            pstmt.setString(5, member.getEmail());
            pstmt.setString(6, member.getPhone());
            pstmt.setBoolean(7, member.isAdmin());
            pstmt.setInt(8, member.getId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Delete a member from the database
     * @param id ID of the member to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteMember(int id) {
        String query = "DELETE FROM members WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Check if a username is already taken
     * @param username Username to check
     * @return true if username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM members WHERE username = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if username exists: " + e.getMessage());
        }
        
        return false;
    }
    
    /**
     * Map a ResultSet row to a Member object
     * @param rs ResultSet containing member data
     * @return Member object
     * @throws SQLException if a database access error occurs
     */
    private Member mapResultSetToMember(ResultSet rs) throws SQLException {
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setUsername(rs.getString("username"));
        member.setPassword(rs.getString("password"));
        member.setName(rs.getString("name"));
        member.setSurname(rs.getString("surname"));
        member.setEmail(rs.getString("email"));
        member.setPhone(rs.getString("phone"));
        member.setAdmin(rs.getBoolean("is_admin"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            member.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        return member;
    }
}