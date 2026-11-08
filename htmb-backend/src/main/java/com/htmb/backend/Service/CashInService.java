
package com.htmb.backend.Service;

import com.htmb.backend.Model.CashInRequest;
import com.htmb.backend.Model.CashInResponse;
import com.htmb.backend.Model.User;
import com.htmb.backend.Repository.UserRepository;
import com.htmb.backend.Repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class CashInService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public CashInService(
            UserRepository userRepository,
            TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public CashInResponse cashin(long userId, CashInRequest request) {

        if (request == null || request.amount() == null) {
            throw new IllegalArgumentException("Amount is required.");
        }

        BigDecimal amount = request.amount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (amount.scale() > 2 || amount.precision() > 15) {
            throw new IllegalArgumentException("Invalid amount.");
        }

        Optional<User> result = userRepository.findByUserId(userId);

        if (result.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = result.get();

        String referenceNumber = "HTMB" +
                UUID.randomUUID().toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        LocalDateTime datetime = LocalDateTime.now();

        userRepository.cashInBalance(userId, amount);

        transactionRepository.saveCashin(
                referenceNumber,
                userId,
                amount,
                "Cash-In Succesful"
        );

        User updatedUser = userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        return new CashInResponse(
                "Cash-In Successful.",
                referenceNumber,
                updatedUser.getUserId(),
                updatedUser.getName(),
                amount,
                updatedUser.getBalance(),
                datetime
        );
    }
}
