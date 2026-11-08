
package com.htmb.backend.Controller;

import com.htmb.backend.Model.CashInRequest;
import com.htmb.backend.Model.CashInResponse;
import com.htmb.backend.Service.CashInService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cashin")
public class CashInController {

    private final CashInService cashInService;

    public CashInController(CashInService cashInService) {
        this.cashInService = cashInService;
    }

    @PostMapping
    public ResponseEntity<?> cashin(
            @RequestBody CashInRequest request,
            Authentication authentication
    ) {
        try {
            long userId = Long.parseLong(authentication.getName());

            CashInResponse response =
                    cashInService.cashin(userId, request);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
