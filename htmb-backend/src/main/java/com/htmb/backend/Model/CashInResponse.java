
package com.htmb.backend.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CashInResponse(
        String message,
        String referenceNumber,
        long userId,
        String name,
        BigDecimal amount,
        BigDecimal balance,
        LocalDateTime datetime
) {
}
