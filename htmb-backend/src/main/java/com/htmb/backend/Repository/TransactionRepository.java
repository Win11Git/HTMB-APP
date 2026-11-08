
package com.htmb.backend.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import com.htmb.backend.Model.Transaction;
import java.util.List;

@Repository
public class TransactionRepository {

    private final JdbcTemplate jdbcTemplate;

    public TransactionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void saveCashin(
            String referenceNumber,
            long userId,
            BigDecimal amount,
            String details
    ) {

        String sql = """
                INSERT INTO transactions
                    (reference_number, user_id, transaction_type, amount, details)
                VALUES (?, ?, ?, ?, ?)
                """;

        jdbcTemplate.update(
                sql,
                referenceNumber,
                userId,
                "CASHIN",
                amount,
                details
        );
    }
    public void saveCashout(
            String referenceNumber,
            long userId,
            BigDecimal amount,
            String details
    ) {

        String sql = """
            INSERT INTO transactions
                (reference_number, user_id, transaction_type, amount, details)
            VALUES (?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(
                sql,
                referenceNumber,
                userId,
                "CASHOUT",
                amount,
                details
        );
    }
    public void saveTransferSent(
            String referenceNumber,
            long userId,
            BigDecimal amount,
            String details
    ) {
        saveTransfer(referenceNumber, userId, "TRANSFER SENT", amount, details);
    }

    public void saveTransferReceived(
            String referenceNumber,
            long userId,
            BigDecimal amount,
            String details
    ) {
        saveTransfer(referenceNumber, userId, "TRANSFER RECEIVED", amount, details);
    }

    private void saveTransfer(
            String referenceNumber,
            long userId,
            String type,
            BigDecimal amount,
            String details
    ) {
        String sql = """
            INSERT INTO transactions
                (reference_number, user_id, transaction_type, amount, details)
            VALUES (?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(sql, referenceNumber, userId, type, amount, details);
    }
    public List<Transaction> findByUserId(long userId) {

        String sql = """
            SELECT
                CASE
                    WHEN transaction_type = 'TRANSFER RECEIVED'
                    THEN LEFT(reference_number, CHAR_LENGTH(reference_number) - 1)
                    ELSE reference_number
                END AS display_reference,
                transaction_type,
                amount,
                details,
                transaction_date
            FROM transactions
            WHERE user_id = ?
            ORDER BY transaction_date DESC
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) ->
                        new Transaction(
                                rs.getString("display_reference"),
                                rs.getString("transaction_type"),
                                rs.getBigDecimal("amount"),
                                rs.getString("details"),
                                rs.getTimestamp("transaction_date").toLocalDateTime()
                        ),
                userId
        );
    }

}
