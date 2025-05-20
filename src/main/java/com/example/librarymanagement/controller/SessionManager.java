package com.example.librarymanagement.controller;

import com.example.librarymanagement.model.Member;

/**
 * Singleton class to manage user session
 */
public class SessionManager {
    private static SessionManager instance;
    private Member currentMember;
    
    /**
     * Private constructor to prevent instantiation
     */
    private SessionManager() {
    }
    
    /**
     * Get the singleton instance
     * @return SessionManager instance
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Get the currently logged-in member
     * @return Current member
     */
    public Member getCurrentMember() {
        return currentMember;
    }
    
    /**
     * Set the currently logged-in member
     * @param member Member to set as current
     */
    public void setCurrentMember(Member member) {
        this.currentMember = member;
    }
    
    /**
     * Clear the current session
     */
    public void clearSession() {
        this.currentMember = null;
    }
    
    /**
     * Check if a user is logged in
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentMember != null;
    }
    
    /**
     * Check if the current user is an admin
     * @return true if the current user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return isLoggedIn() && currentMember.isAdmin();
    }
}