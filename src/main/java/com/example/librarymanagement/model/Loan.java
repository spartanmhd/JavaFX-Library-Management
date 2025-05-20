package com.example.librarymanagement.model;

import java.time.LocalDateTime;

/**
 * Model class representing a book loan in the library
 */
public class Loan {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDateTime loanDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    
    // For displaying in UI
    private Book book;
    private Member member;

    // Default constructor
    public Loan() {
    }

    // Constructor with essential fields
    public Loan(int bookId, int memberId, LocalDateTime dueDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = LocalDateTime.now();
        this.dueDate = dueDate;
    }

    // Constructor with all fields except id
    public Loan(int bookId, int memberId, LocalDateTime loanDate, LocalDateTime dueDate, LocalDateTime returnDate) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    // Constructor with all fields
    public Loan(int id, int bookId, int memberId, LocalDateTime loanDate, LocalDateTime dueDate, LocalDateTime returnDate) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDateTime loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    // Check if the book has been returned
    public boolean isReturned() {
        return returnDate != null;
    }

    // Check if the loan is overdue
    public boolean isOverdue() {
        if (isReturned()) {
            return false;
        }
        return LocalDateTime.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return "Loan{" +
                "id=" + id +
                ", bookId=" + bookId +
                ", memberId=" + memberId +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", returnDate=" + returnDate +
                '}';
    }
}