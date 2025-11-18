package com.babaev.jdbc.starter.dao.impl;

import com.babaev.jdbc.starter.dao.LoanDao;
import com.babaev.jdbc.starter.entity.BookCopy;
import com.babaev.jdbc.starter.entity.Loan;
import com.babaev.jdbc.starter.entity.Reader;
import com.babaev.jdbc.starter.util.ConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanDaoImpl implements LoanDao {
    private static final ReaderDaoImpl readerDao = new ReaderDaoImpl();
    private static final BookCopyDaoImpl bookCopyDao = new BookCopyDaoImpl();

    private static final String INSERT_SQL = """
        INSERT INTO loan (reader_id, copy_id, date_taken, date_due, date_returned)
        VALUES (?, ?, ?, ?, ?)
        """;

    private static final String FIND_BY_ID_SQL = """
        SELECT * FROM loan WHERE id = ?
        """;

    private static final String FIND_ALL_SQL = """
        SELECT * FROM loan
        """;

    private static final String UPDATE_SQL = """
        UPDATE loan
        SET reader_id = ?, copy_id = ?, date_due = ?, date_returned = ?
        WHERE id = ?
        """;

    private static final String DELETE_SQL = """
        DELETE FROM loan WHERE id = ?
        """;

    @Override
    public Loan save(Loan loan) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, loan.getReader().getId());
            stmt.setLong(2, loan.getCopy().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(loan.getDateTaken()));
            stmt.setDate(4, Date.valueOf(loan.getDateDue()));
            stmt.setTimestamp(5, loan.getDateReturned() == null ? null : Timestamp.valueOf(loan.getDateReturned()));

            stmt.executeUpdate();

            try (var keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    loan.setId(keys.getLong(1));
                }
            }
            return loan;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to save loan", e);
        }
    }

    @Override
    public Optional<Loan> findById(Long id) {
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
            throw new RuntimeException("Failed to find loan", e);
        }
    }

    @Override
    public List<Loan> findAll() {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(FIND_ALL_SQL);
             var rs = stmt.executeQuery()) {

            List<Loan> result = new ArrayList<>();
            while (rs.next()) {
                result.add(map(rs));
            }
            return result;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all loans", e);
        }
    }

    @Override
    public boolean update(Loan loan) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(UPDATE_SQL)) {

            stmt.setLong(1, loan.getReader().getId());
            stmt.setLong(2, loan.getCopy().getId());
            stmt.setDate(3, Date.valueOf(loan.getDateDue()));
            stmt.setTimestamp(4, loan.getDateReturned() == null ? null : Timestamp.valueOf(loan.getDateReturned()));
            stmt.setLong(5, loan.getId());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to update loan", e);
        }
    }

    @Override
    public boolean delete(Long id) {
        try (var connection = ConnectionManager.get();
             var stmt = connection.prepareStatement(DELETE_SQL)) {

            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete loan", e);
        }
    }

    private Loan map(ResultSet rs) throws SQLException {
        Reader reader = readerDao.findById(rs.getLong("id"), rs.getStatement().getConnection()).orElse(null);
        BookCopy copy = bookCopyDao.findById(rs.getLong("copy_id"), rs.getStatement().getConnection()).orElse(null);
        return new Loan(
                rs.getLong("id"),
                reader,
                copy,
                rs.getTimestamp("date_taken").toLocalDateTime(),
                rs.getDate("date_due").toLocalDate(),
                rs.getTimestamp("date_returned") == null ? null :
                        rs.getTimestamp("date_returned").toLocalDateTime()
        );
    }
}

