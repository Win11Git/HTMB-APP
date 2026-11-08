package com.htmb.backend.Model;

import java.math.BigDecimal;

public record CashOutRequest(
        BigDecimal amount
) {
}