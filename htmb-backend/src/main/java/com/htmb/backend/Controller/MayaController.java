package com.htmb.backend.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/maya")
public class MayaController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/checkout")
    public ResponseEntity<?> createCheckout(
            @RequestBody CashInRequest request) {

        try {
            if (request.amount() == null
                    || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Invalid amount."));
            }

            String publicKey = System.getenv("MAYA_PUBLIC_KEY");

            if (publicKey == null || publicKey.isBlank()) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Maya API key is missing."));
            }

            String referenceNumber = UUID.randomUUID().toString();

            Map<String, Object> payload = Map.of(
                    "totalAmount", Map.of(
                            "value", request.amount(),
                            "currency", "PHP"
                    ),
                    "requestReferenceNumber", referenceNumber
            );

            String json = objectMapper.writeValueAsString(payload);

            URL url = new URL(
                    "https://pg-sandbox.paymaya.com/checkout/v1/checkouts"
            );

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            String credentials = publicKey + ":";
            String encodedCredentials = Base64.getEncoder().encodeToString(
                    credentials.getBytes(StandardCharsets.UTF_8)
            );

            connection.setRequestProperty(
                    "Authorization", "Basic " + encodedCredentials
            );

            connection.setDoOutput(true);

            try (var output = connection.getOutputStream()) {
                output.write(json.getBytes(StandardCharsets.UTF_8));
            }

            int statusCode = connection.getResponseCode();

            var input = statusCode >= 400
                    ? connection.getErrorStream()
                    : connection.getInputStream();

            String responseBody = "";

            if (input != null) {
                try (var reader = new java.io.BufferedReader(
                        new java.io.InputStreamReader(
                                input, StandardCharsets.UTF_8))) {

                    String line;
                    StringBuilder result = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    responseBody = result.toString();
                }
            }

            connection.disconnect();

            if (statusCode >= 400) {
                return ResponseEntity.status(statusCode)
                        .body(Map.of("message", responseBody));
            }

            Map<?, ?> mayaResponse =
                    objectMapper.readValue(responseBody, Map.class);

            return ResponseEntity.ok(mayaResponse);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Checkout failed: " + e.getMessage()));
        }
    }

    public record CashInRequest(BigDecimal amount) {
    }
}