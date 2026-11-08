package com.htmb.backend.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
        String message,
        String referenceNumber,
        long senderUserId,
        String senderName,
        String senderMobileNumber,
        long recipientUserId,
        String recipientName,
        String recipientMobileNumber,
        BigDecimal amount,
        BigDecimal senderBalance,
        LocalDateTime datetime
) {}