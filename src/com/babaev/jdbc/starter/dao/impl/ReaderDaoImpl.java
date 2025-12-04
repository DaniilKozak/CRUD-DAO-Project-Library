package com.babaev.jdbc.starter.dao.impl;

import com.babaev.jdbc.starter.dao.ReaderDao;
import com.babaev.jdbc.starter.entity.Reader;
import com.babaev.jdbc.starter.exception.DaoException;
import com.babaev.jdbc.starter.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReaderDaoImpl implements ReaderDao {

    private static final String DETERMINE_BLACKLIST_SQL = """
            SELECT EXISTS(SELECT 1 FROM loan l
                          JOIN reader r ON l.reader_id = r.id
                          WHERE l.date_due > now() AND l.date_returned IS NULL AND r.id = ?)
            """;

    private static final String GET_BLACKLIST_SQL = """
            SELECT
                bk.id,
                b.title,
                l.date_due
            FROM loan l
            LEFT JOIN reader r ON l.id=r.id
            LEFT JOIN book_copy bk on bk.id = l.copy_id
            LEFT JOIN book b on b.id = bk.book_id
            WHERE l.date_due > now() AND l.date_returned IS NULL AND r.id = ?
            """;

    private static final String IS_BLACK_LISTED_SQL = """
            SELECT black_listed FROM reader
            WHERE id = ?
            """;

    private static final String INSERT_SQL = """
            INSERT INTO reader (first_name, last_name, email, phone_number, registration_date, black_listed)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

    private static final String FIND_BY_ID_SQL = """
            SELECT * FROM reader WHERE id = ?
            """;

    private static final String FIND_ALL_SQL = """
            SELECT * FROM reader
            """;

    private static final String UPDATE_SQL = """
            UPDATE reader
            SET first_name = ?, last_name = ?, email = ?, phone_number = ?, black_listed = ?
            WHERE id = ?
            """;

    private static final String DELETE_SQL = """
            DELETE FROM reader WHERE id = ?
            """;

    public boolean determineBlacklist(Reader reader) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(DETERMINE_BLACKLIST_SQL)) {
            stmt.setLong(1, reader.getId());
            try (var resultSet = stmt.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public List<String> getListDebts(Reader reader) {
        List<String> list = new ArrayList<>();
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(GET_BLACKLIST_SQL)) {
            stmt.setLong(1, reader.getId());
            try (var resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    StringBuilder sb = new StringBuilder()
                            .append("Book id is " + resultSet.getLong("id") + ", ")
                            .append("title of book is " + resultSet.getString("title") + ", ")
                            .append("due date is " + resultSet.getDate("date_due"));
                    list.add(sb.toString());
                }
                return list;
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    public boolean isBlackListed(Reader reader) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(IS_BLACK_LISTED_SQL)) {
            stmt.setLong(1, reader.getId());
            try (var resultSet = stmt.executeQuery()) {
                resultSet.next();
                return resultSet.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Reader save(Reader reader) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, reader.getFirstName());
            stmt.setString(2, reader.getLastName());
            stmt.setString(3, reader.getEmail());
            stmt.setString(4, reader.getPhoneNumber());
            stmt.setDate(5, Date.valueOf(reader.getRegistrationDate()));
            stmt.setBoolean(6, reader.isBlackListed());

            stmt.executeUpdate();

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    reader.setId(keys.getLong(1));
                }
            }

            return reader;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save reader", e);
        }
    }

    public Optional<Reader> findById(Long id, Connection connection) {
        try (var stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setLong(1, id);

            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find reader by id", e);
        }
    }

    @Override
    public Optional<Reader> findById(Long id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find reader by id", e);
        }
    }

    @Override
    public List<Reader> findAll() {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(FIND_ALL_SQL);
             var rs = stmt.executeQuery()) {

            List<Reader> list = new ArrayList<>();
            while (rs.next()) {
                list.add(map(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all readers", e);
        }
    }

    @Override
    public boolean update(Reader reader) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(UPDATE_SQL)) {

            stmt.setString(1, reader.getFirstName());
            stmt.setString(2, reader.getLastName());
            stmt.setString(3, reader.getEmail());
            stmt.setString(4, reader.getPhoneNumber());
            stmt.setBoolean(5, reader.isBlackListed());
            stmt.setLong(6, reader.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update reader", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete reader", e);
        }
    }

    private Reader map(ResultSet rs) throws SQLException {
        Reader r = new Reader();
        r.setId(rs.getLong("id"));
        r.setFirstName(rs.getString("first_name"));
        r.setLastName(rs.getString("last_name"));
        r.setEmail(rs.getString("email"));
        r.setPhoneNumber(rs.getString("phone_number"));
        r.setRegistrationDate(rs.getDate("registration_date").toLocalDate());
        r.setBlackListed(rs.getBoolean("black_listed"));
        return r;
    }
}

