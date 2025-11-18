package com.babaev.jdbc.starter.dao.impl;

import com.babaev.jdbc.starter.dao.ReaderDao;
import com.babaev.jdbc.starter.entity.Reader;
import com.babaev.jdbc.starter.util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReaderDaoImpl implements ReaderDao {

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

    @Override
    public Optional<Reader> findById(Long id) {
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

