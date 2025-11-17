package com.babaev.jdbc.starter.entity;

public class BookCopy {
    private Long id;
    private Book book;
    private String inventoryNumber;
    private String status; // AVAILABLE, TAKEN, LOST etc.

    public BookCopy() {}

    public BookCopy(Long id, Book book, String inventoryNumber, String status) {
        this.id = id;
        this.book = book;
        this.inventoryNumber = inventoryNumber;
        this.status = status;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Book getBook() { return book; }
    public void setBook(Book book) { this.book = book; }

    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "BookCopy{" +
               "id=" + id +
               ", book=" + book +
               ", inventoryNumber='" + inventoryNumber + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
}

