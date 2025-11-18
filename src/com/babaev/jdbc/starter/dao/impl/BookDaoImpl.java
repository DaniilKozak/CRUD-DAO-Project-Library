package com.babaev.jdbc.starter.dao.impl;

import com.babaev.jdbc.starter.dao.BookDao;
import com.babaev.jdbc.starter.entity.Book;
import com.babaev.jdbc.starter.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDaoImpl implements BookDao {

    private static final String INSERT_SQL = """
        INSERT INTO book (title, author, isbn)
        VALUES (?, ?, ?)
        """;

    private static final String FIND_BY_ID_SQL = """
        SELECT * FROM book WHERE id = ?
        """;

    private static final String FIND_ALL_SQL = """
        SELECT * FROM book
        """;

    private static final String UPDATE_SQL = """
        UPDATE book
        SET title = ?, author = ?, isbn = ?
        WHERE id = ?
        """;

    private static final String DELETE_SQL = """
        DELETE FROM book WHERE id = ?
        """;

    @Override
    public Book save(Book book) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.executeUpdate();

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    book.setId(keys.getLong(1));
                }
            }
            return book;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save book", e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {

            stmt.setLong(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find book by id", e);
        }
    }

    @Override
    public List<Book> findAll() {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(FIND_ALL_SQL);
             var rs = stmt.executeQuery()) {

            List<Book> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all books", e);
        }
    }

    @Override
    public boolean update(Book book) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setString(3, book.getIsbn());
            stmt.setLong(4, book.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    private Book map(ResultSet rs) throws SQLException {
        return new Book(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("isbn")
        );
    }
}

