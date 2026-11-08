package com.htmb.backend.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record TransactionHistoryResponse(
        String message,
        long userId,
        List<TransactionItem> transactions
) {
    public record TransactionItem(
            String referenceNumber,
            String type,
            BigDecimal amount,
            String details,
            LocalDateTime datetime
    ) {}
}