package com.htmb.backend.Repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class DeviceLoginAttemptRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeviceLoginAttemptRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getFailedAttempts(String deviceId) {
        String sql = """
                SELECT failed_attempts
                FROM device_login_attempts
                WHERE device_id = ?
                """;

        Integer attempts = jdbcTemplate.query(
                sql,
                rs -> rs.next() ? rs.getInt("failed_attempts") : 0,
                deviceId
        );

        return attempts;
    }

    public Optional<LocalDateTime> getLockedUntil(String deviceId) {
        String sql = """
                SELECT locked_until
                FROM device_login_attempts
                WHERE device_id = ?
                """;

        return jdbcTemplate.query(
                sql,
                rs -> {
                    if (!rs.next()) {
                        return Optional.empty();
                    }

                    Timestamp timestamp = rs.getTimestamp("locked_until");

                    return timestamp == null
                            ? Optional.empty()
                            : Optional.of(timestamp.toLocalDateTime());
                },
                deviceId
        );
    }

    public void incrementFailedAttempts(String deviceId) {
        String sql = """
                INSERT INTO device_login_attempts
                    (device_id, failed_attempts)
                VALUES (?, 1)
                ON DUPLICATE KEY UPDATE
                    failed_attempts = failed_attempts + 1
                """;

        jdbcTemplate.update(sql, deviceId);
    }

    public void lockDevice(String deviceId) {
        String sql = """
                UPDATE device_login_attempts
                SET locked_until = DATE_ADD(NOW(), INTERVAL 1 MINUTE)
                WHERE device_id = ?
                """;

        jdbcTemplate.update(sql, deviceId);
    }

    public void resetAttempts(String deviceId) {
        String sql = """
                UPDATE device_login_attempts
                SET failed_attempts = 0,
                    locked_until = NULL
                WHERE device_id = ?
                """;

        jdbcTemplate.update(sql, deviceId);
    }
}