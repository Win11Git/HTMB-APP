package com.htmb.backend.Model;

public record LoginRequest(
        String mobileNumber,
        String pin,
        String deviceId
) {
}