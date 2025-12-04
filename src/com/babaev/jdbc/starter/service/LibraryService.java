package com.babaev.jdbc.starter.service;

import com.babaev.jdbc.starter.dao.impl.BookCopyDaoImpl;
import com.babaev.jdbc.starter.dao.impl.BookDaoImpl;
import com.babaev.jdbc.starter.dao.impl.LoanDaoImpl;
import com.babaev.jdbc.starter.dao.impl.ReaderDaoImpl;
import com.babaev.jdbc.starter.entity.Book;
import com.babaev.jdbc.starter.entity.Loan;
import com.babaev.jdbc.starter.entity.Reader;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LibraryService {
    private static final BookCopyDaoImpl bookCopyDao = new BookCopyDaoImpl();
    private static final ReaderDaoImpl readerDao = new ReaderDaoImpl();
    private static final BookDaoImpl bookDao = new BookDaoImpl();
    private static final LoanDaoImpl loanDao = new LoanDaoImpl();

    public boolean isBookAvailable(Book book) {
        return bookCopyDao.isAvailableBookCopyById(book.getId());
    }

    public boolean isBookAvailable(String bookTitle) {
        return bookCopyDao.isAvailableBookCopyByBookTitle(bookTitle);
    }

    public void checkoutToRoom(Reader reader, Book book) {
        checkToUpdateBlackList(reader);
        validateReader(reader);
        validateBook(book);
        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setCopy(bookCopyDao.findByBookId(book.getId()).orElse(null));
        loan.setDateTaken(LocalDateTime.now());
        loan.setDateDue(LocalDate.now());
        loanDao.save(loan);
        System.out.println("You've received a book in the reading room." +
                           " The reading room is open until 6:00 PM," +
                           " so please remember to return your book before closing.");
    }

    public void checkout(Reader reader, Book book, LocalDate dateDue){
        checkToUpdateBlackList(reader);
        validateReader(reader);
        validateBook(book);
        Loan loan = new Loan();
        loan.setReader(reader);
        loan.setCopy(bookCopyDao.findByBookId(book.getId()).orElse(null));
        loan.setDateTaken(LocalDateTime.now());
        loan.setDateDue(dateDue);
        loanDao.save(loan);
        System.out.println("You have been issued a book for loan until the date " + 
                           dateDue +" , please remember to return the book before the date ends.");
    }

    private void checkToUpdateBlackList(Reader reader) {
        if(readerDao.determineBlacklist(reader)){
            reader.setBlackListed(true);
            readerDao.update(reader);
        }
    }

    private void validateBook(Book book){
        if(!isBookAvailable(book)) {
            System.out.println("Book is not available");
            return;
        }
    }

    private void validateReader(Reader reader) {
        if(readerDao.isBlackListed(reader)) {
            System.out.println("The reader " + reader.getFirstName() +
                               " " + reader.getLastName() + " is blacklisted.");
            return;
        }
    }




}
