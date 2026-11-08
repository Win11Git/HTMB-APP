
package com.htmb.backend.Service;

import com.htmb.backend.Model.RegisterRequest;
import com.htmb.backend.Model.User;
import com.htmb.backend.Repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RegisterService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public RegisterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public String register(RegisterRequest request) {
        if (request == null
                || request.name() == null
                || request.mobileNumber() == null
                || request.pin() == null) {
            throw new IllegalArgumentException(
                    "All fields are required.");
        }

        String name = request.name().trim();
        String mobile = request.mobileNumber().trim();
        String pin = request.pin().trim();

        if (name.isBlank() || mobile.isBlank() || pin.isBlank()) {
            throw new IllegalArgumentException(
                    "All fields are required.");
        }

        if (!mobile.matches("^09\\d{9}$")) {
            throw new IllegalArgumentException(
                    "Use a valid mobile number: 09XXXXXXXXX.");
        }

        if (!pin.matches("\\d{4}")) {
            throw new IllegalArgumentException(
                    "PIN must contain exactly 4 digits.");
        }

        if (userRepository.existsByMobileNumber(mobile)) {
            throw new IllegalArgumentException(
                    "Mobile number already exists.");
        }

        long userId = userRepository.generateUserId();
        String hashedPin = passwordEncoder.encode(pin);

        User user = new User(
                name,
                mobile,
                hashedPin,
                userId
        );

        user.setBalance(BigDecimal.ZERO);
        userRepository.save(user);

        return "Registration successful.";
    }
}
