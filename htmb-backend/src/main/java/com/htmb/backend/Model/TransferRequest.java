package com.htmb.backend.Model;

import java.math.BigDecimal;

public record TransferRequest(
        String recipientMobileNumber,
        BigDecimal amount
) {}