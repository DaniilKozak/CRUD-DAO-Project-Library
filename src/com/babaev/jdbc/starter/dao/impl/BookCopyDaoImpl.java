package com.babaev.jdbc.starter.dao.impl;

import com.babaev.jdbc.starter.dao.BookCopyDao;
import com.babaev.jdbc.starter.entity.Book;
import com.babaev.jdbc.starter.entity.BookCopy;
import com.babaev.jdbc.starter.enumeration.Status;
import com.babaev.jdbc.starter.exception.DaoException;
import com.babaev.jdbc.starter.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookCopyDaoImpl implements BookCopyDao {
    private static final BookDaoImpl bookDao = new BookDaoImpl();

    private static final String FIND_BY_BOOK_ID_SQL = """
            SELECT * FROM book_copy
            WHERE book_id = ? AND status = ?
            LIMIT 1
            """;

    private static final String FIND_IS_AVAILABLE_BOOK_COPY_BY_BOOK_ID_SQL = """
            SELECT EXISTS(SELECT 1 FROM book_copy
                         WHERE book_id = ? AND status = ?)
            """;

    private static final String FIND_IS_AVAILABLE_BOOK_COPY_BY_BOOK_TITLE_SQL = """
            SELECT EXISTS(SELECT 1 FROM book_copy bk
            JOIN book b ON b.id = bk.book_id
            WHERE b.title = ? AND bk.status = ?)
            """;

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

    public Optional<BookCopy> findByBookId(Long bookId) {
        BookCopy bookCopy = null;
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(FIND_BY_BOOK_ID_SQL)) {
            stmt.setLong(1, bookId);
            stmt.setString(2, Status.AVAILABLE.name());
            try (var resultSet = stmt.executeQuery()) {
                if(resultSet.next()){
                    bookCopy = map(resultSet);
                }
                return Optional.ofNullable(bookCopy);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean isAvailableBookCopyById(Long id) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(FIND_IS_AVAILABLE_BOOK_COPY_BY_BOOK_ID_SQL)) {
            stmt.setLong(1, id);
            stmt.setString(2, Status.AVAILABLE.name());
            try (var rs = stmt.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean isAvailableBookCopyByBookTitle(String bookTitle) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(FIND_IS_AVAILABLE_BOOK_COPY_BY_BOOK_TITLE_SQL)) {
            stmt.setString(1, bookTitle);
            stmt.setString(2, Status.AVAILABLE.name());
            try (var rs = stmt.executeQuery()) {
                rs.next();
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

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

    public Optional<BookCopy> findById(Long id, Connection connection) {
        try (var stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
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
        try (var connection = ConnectionManager.get()) {
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

