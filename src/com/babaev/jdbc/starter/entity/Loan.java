package com.babaev.jdbc.starter.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Loan {
    private Long id;
    private Reader reader;
    private BookCopy copy;
    private LocalDateTime dateTaken;
    private LocalDate dateDue;
    private LocalDateTime dateReturned;

    public Loan() {
    }

    public Loan(Long id, Reader reader, BookCopy copy, LocalDateTime dateTaken,
                LocalDate dateDue, LocalDateTime dateReturned) {
        this.id = id;
        this.reader = reader;
        this.copy = copy;
        this.dateTaken = dateTaken;
        this.dateDue = dateDue;
        this.dateReturned = dateReturned;
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Reader getReader() { return reader; }
    public void setReader(Reader reader) { this.reader = reader; }

    public BookCopy getCopy() { return copy; }
    public void setCopy(BookCopy copy) { this.copy = copy; }

    public LocalDateTime getDateTaken() { return dateTaken; }
    public void setDateTaken(LocalDateTime dateTaken) { this.dateTaken = dateTaken; }

    public LocalDate getDateDue() { return dateDue; }
    public void setDateDue(LocalDate dateDue) { this.dateDue = dateDue; }

    public LocalDateTime getDateReturned() { return dateReturned; }
    public void setDateReturned(LocalDateTime dateReturned) { this.dateReturned = dateReturned; }

    @Override
    public String toString() {
        return "Loan{" +
               "id=" + id +
               ", reader=" + reader +
               ", copy=" + copy +
               ", dateTaken=" + dateTaken +
               ", dateDue=" + dateDue +
               ", dateReturned=" + dateReturned +
               '}';
    }
}

