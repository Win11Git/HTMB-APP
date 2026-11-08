
package com.htmb.backend.Model;

import java.math.BigDecimal;

public record LoginResponse(
        String message,
        long userId,
        String name,
        String mobileNumber,
        BigDecimal balance,
        String token
) {
}
