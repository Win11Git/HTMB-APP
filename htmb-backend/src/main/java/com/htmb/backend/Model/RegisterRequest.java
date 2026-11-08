
package com.htmb.backend.Model;

public record RegisterRequest(
        String name,
        String mobileNumber,
        String pin
) {
}
