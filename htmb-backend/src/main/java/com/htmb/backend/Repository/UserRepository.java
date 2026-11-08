
package com.htmb.backend.Repository;

import com.htmb.backend.Model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;

import java.math.BigDecimal;
import java.util.Optional;


@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean existsByMobileNumber(String mobileNumber) {
        String sql = """
                SELECT COUNT(*)
                FROM accounts
                WHERE mobile_number = ?
                """;

        Integer count = jdbcTemplate.queryForObject(
                sql, Integer.class, mobileNumber);

        return count != null && count > 0;
    }

    public long generateUserId() {
        String sql = """
                SELECT COALESCE(MAX(user_id), 7284776600000000) + 1
                FROM accounts
                """;

        Long id = jdbcTemplate.queryForObject(sql, Long.class);

        if (id == null) {
            throw new IllegalStateException("Could not generate user ID.");
        }

        return id;
    }

    public void save(User user) {
        String sql = """
                INSERT INTO accounts
                    (user_id, full_name, mobile_number, pin, balance)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                user.getUserId(),
                user.getName(),
                user.getMobileNumber(),
                user.getPin(),
                user.getBalance()
        );
    }

    // Find user using User ID
    public Optional<User> findByUserId(long userId) {
        String sql = """
                SELECT user_id, full_name, mobile_number, pin, balance
                FROM accounts
                WHERE user_id = ?
                """;

        try {
            User user = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> {
                        User result = new User(
                                rs.getString("full_name"),
                                rs.getString("mobile_number"),
                                rs.getString("pin"),
                                rs.getLong("user_id")
                        );

                        result.setBalance(rs.getBigDecimal("balance"));
                        return result;
                    },
                    userId
            );

            return Optional.ofNullable(user);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    // Find user using mobile number
    public Optional<User> findByMobileNumber(String mobileNumber) {
        String sql = """
                SELECT user_id, full_name, mobile_number, pin, balance
                FROM accounts
                WHERE mobile_number = ?
                """;

        try {
            User user = jdbcTemplate.queryForObject(
                    sql,
                    (rs, rowNum) -> {
                        User result = new User(
                                rs.getString("full_name"),
                                rs.getString("mobile_number"),
                                rs.getString("pin"),
                                rs.getLong("user_id")
                        );

                        result.setBalance(rs.getBigDecimal("balance"));
                        return result;
                    },
                    mobileNumber
            );

            return Optional.ofNullable(user);

        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public void cashInBalance(long userId, BigDecimal amount) {

        String sql = """
            UPDATE accounts
            SET balance = balance + ?
            WHERE user_id = ?
            """;

        int rowsUpdated = jdbcTemplate.update(sql, amount, userId);

        if (rowsUpdated != 1) {
            throw new IllegalArgumentException("Account not found.");
        }
    }
    public boolean cashoutBalance(long userId, BigDecimal amount) {

        String sql = """
            UPDATE accounts
            SET balance = balance - ?
            WHERE user_id = ? AND balance >= ?
            """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                amount,
                userId,
                amount
        );

        return rowsUpdated == 1;
    }
    public boolean transferOut(long userId, BigDecimal amount) {

        String sql = """
            UPDATE accounts
            SET balance = balance - ?
            WHERE user_id = ? AND balance >= ?
            """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                amount,
                userId,
                amount
        );

        return rowsUpdated == 1;
    }

    public void transferIn(long userId, BigDecimal amount) {

        String sql = """
            UPDATE accounts
            SET balance = balance + ?
            WHERE user_id = ?
            """;

        int rowsUpdated = jdbcTemplate.update(
                sql,
                amount,
                userId
        );

        if (rowsUpdated != 1) {
            throw new IllegalArgumentException("Recipient account not found.");
        }
    }












}
