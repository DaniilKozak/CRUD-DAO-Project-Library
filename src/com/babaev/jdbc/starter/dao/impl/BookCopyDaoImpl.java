package com.babaev.jdbc.starter.dao.impl;

import com.babaev.jdbc.starter.dao.BookCopyDao;
import com.babaev.jdbc.starter.entity.Book;
import com.babaev.jdbc.starter.entity.BookCopy;
import com.babaev.jdbc.starter.enumeration.Status;
import com.babaev.jdbc.starter.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookCopyDaoImpl implements BookCopyDao {
    private static final BookDaoImpl bookDao = new BookDaoImpl();

    private static final String INSERT_SQL = """
        INSERT INTO book_copy (book_id, inventory_number, status)
        VALUES (?, ?, ?)
        """;

    private static final String FIND_BY_ID_SQL = """
        SELECT * FROM book_copy WHERE id = ?
        """;

    private static final String FIND_ALL_SQL = """
        SELECT * FROM book_copy
        """;

    private static final String UPDATE_SQL = """
        UPDATE book_copy
        SET book_id = ?, inventory_number = ?, status = ?
        WHERE id = ?
        """;

    private static final String DELETE_SQL = """
        DELETE FROM book_copy WHERE id = ?
        """;

    @Override
    public BookCopy save(BookCopy copy) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, copy.getBook().getId());
            stmt.setString(2, copy.getInventoryNumber());
            stmt.setString(3, copy.getStatus().name());
            stmt.executeUpdate();

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    copy.setId(keys.getLong(1));
                }
            }

            return copy;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save book copy", e);
        }
    }

    public Optional<BookCopy> findById(Long id, Connection connection){
        try(var stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setLong(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find book copy", e);
        }
    }

    @Override
    public Optional<BookCopy> findById(Long id) {
        try (var connection = ConnectionManager.get()){
            return findById(id, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find book copy", e);
        }
    }

    @Override
    public List<BookCopy> findAll() {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(FIND_ALL_SQL);
             var rs = stmt.executeQuery()) {

            List<BookCopy> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all book copies", e);
        }
    }

    @Override
    public boolean update(BookCopy copy) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(UPDATE_SQL)) {

            stmt.setLong(1, copy.getBook().getId());
            stmt.setString(2, copy.getInventoryNumber());
            stmt.setString(3, copy.getStatus().name());
            stmt.setLong(4, copy.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update book copy", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete book copy", e);
        }
    }

    private BookCopy map(ResultSet rs) throws SQLException {
        Book book = bookDao.findById(rs.getLong("id"), rs.getStatement().getConnection()).orElse(null);
        Status status = Status.fromString(rs.getString("status"));
        return new BookCopy(
                rs.getLong("id"),
                book,
                rs.getString("inventory_number"),
                status
        );
    }
}

