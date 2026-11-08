
package com.htmb.backend.Controller;

import com.htmb.backend.Model.RegisterRequest;
import com.htmb.backend.Service.RegisterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> register(
            @RequestBody RegisterRequest request) {

        try {
            String message = registerService.register(request);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", message));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }
}
