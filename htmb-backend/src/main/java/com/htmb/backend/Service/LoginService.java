
package com.htmb.backend.Service;

import com.htmb.backend.Model.LoginRequest;
import com.htmb.backend.Model.LoginResponse;
import com.htmb.backend.Model.User;
import com.htmb.backend.Repository.UserRepository;
import com.htmb.backend.Repository.DeviceLoginAttemptRepository;
import com.htmb.backend.Util.JwtService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LoginService {

    private final UserRepository userRepository;
    private final DeviceLoginAttemptRepository deviceRepository;
    private final JwtService jwtService;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    public LoginService(
            UserRepository userRepository,
            DeviceLoginAttemptRepository deviceRepository,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.deviceRepository = deviceRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        if (request == null
                || request.deviceId() == null
                || request.deviceId().isBlank()) {
            throw new IllegalArgumentException("Device ID is required.");
        }

        String deviceId = request.deviceId().trim();

        // Check whether this device is locked
        Optional<LocalDateTime> lockedUntil =
                deviceRepository.getLockedUntil(deviceId);

        if (lockedUntil.isPresent()) {

            if (LocalDateTime.now().isBefore(lockedUntil.get())) {
                throw new IllegalArgumentException(
                        "This device is temporarily locked. Try again later."
                );
            }

            // Lock period has expired
            deviceRepository.resetAttempts(deviceId);
        }

        // Validate credentials
        if (request.mobileNumber() == null
                || request.pin() == null
                || request.mobileNumber().isBlank()
                || request.pin().isBlank()) {

            return failedLogin(deviceId);
        }

        String mobile = request.mobileNumber().trim();
        String pin = request.pin();

        if (!mobile.matches("^09\\d{9}$")) {
            return failedLogin(deviceId);
        }

        Optional<User> result =
                userRepository.findByMobileNumber(mobile);

        if (result.isEmpty()) {
            return failedLogin(deviceId);
        }

        User user = result.get();

        // Verify PIN
        if (!passwordEncoder.matches(pin, user.getPin())) {
            return failedLogin(deviceId);
        }

        // Successful login: reset this device's attempts
        deviceRepository.resetAttempts(deviceId);

        long userId = user.getUserId();

        String token = jwtService.generateToken(userId);

        return new LoginResponse(
                "Login successful.",
                userId,
                user.getName(),
                user.getMobileNumber(),
                user.getBalance(),
                token
        );
    }

    private LoginResponse failedLogin(String deviceId) {

        deviceRepository.incrementFailedAttempts(deviceId);

        int attempts =
                deviceRepository.getFailedAttempts(deviceId);

        if (attempts >= 3) {

            deviceRepository.lockDevice(deviceId);

            throw new IllegalArgumentException(
                    "Too many failed attempts. This device is locked for 1 minute."
            );
        }

        int remainingAttempts = 3 - attempts;

        throw new IllegalArgumentException(
                "Invalid mobile number or PIN. "
                        + remainingAttempts
                        + " attempt(s) remaining."
        );
    }
}
