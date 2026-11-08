package com.htmb.backend.Controller;

import com.htmb.backend.Model.TransactionHistoryResponse;
import com.htmb.backend.Service.TransactionHistoryService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transaction-history")
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    public TransactionHistoryController(
            TransactionHistoryService transactionHistoryService
    ) {
        this.transactionHistoryService = transactionHistoryService;
    }

    @PostMapping
    public ResponseEntity<?> getHistory(Authentication authentication) {
        try {
            long userId = Long.parseLong(authentication.getName());

            TransactionHistoryResponse response =
                    transactionHistoryService.getHistory(userId);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}