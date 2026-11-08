
package com.htmb.backend.Controller;

import com.htmb.backend.Model.CashOutRequest;
import com.htmb.backend.Model.CashOutResponse;
import com.htmb.backend.Service.CashOutService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cashout")
public class CashOutController {

    private final CashOutService cashOutService;

    public CashOutController(CashOutService cashOutService) {
        this.cashOutService = cashOutService;
    }

    @PostMapping
    public ResponseEntity<?> cashout(
            @RequestBody CashOutRequest request,
            Authentication authentication
    ) {
        try {
            long userId = Long.parseLong(authentication.getName());

            CashOutResponse response =
                    cashOutService.cashout(userId, request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            HttpStatus status = e.getMessage().equals("Account not found.")
                    ? HttpStatus.NOT_FOUND
                    : HttpStatus.BAD_REQUEST;

            return ResponseEntity
                    .status(status)
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
