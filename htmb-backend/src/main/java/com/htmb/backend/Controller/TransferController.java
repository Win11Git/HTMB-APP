package com.htmb.backend.Controller;

import com.htmb.backend.Model.TransferRequest;
import com.htmb.backend.Model.TransferResponse;
import com.htmb.backend.Service.TransferService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/transfer")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<?> transfer(
            @RequestBody TransferRequest request,
            Authentication authentication
    ) {
        try {
            long senderUserId = Long.parseLong(authentication.getName());

            TransferResponse response =
                    transferService.transfer(senderUserId, request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}