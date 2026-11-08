
package com.htmb.backend.Service;

import com.htmb.backend.Model.CashOutRequest;
import com.htmb.backend.Model.CashOutResponse;
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
public class CashOutService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public CashOutService(
            UserRepository userRepository,
            TransactionRepository transactionRepository
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public CashOutResponse cashout(long userId, CashOutRequest request) {

        // 1. Validate request
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        if (request == null || request.amount() == null) {
            throw new IllegalArgumentException("Cash-Out amount is required.");
        }

        BigDecimal amount = request.amount();

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Invalid Cash-Out amount. Enter a positive value."
            );
        }

        if (amount.scale() > 2 || amount.precision() > 15) {
            throw new IllegalArgumentException(
                    "Invalid Cash-Out amount. Maximum two decimal places allowed."
            );
        }

        // 2. Find account
        Optional<User> result = userRepository.findByUserId(userId);

        if (result.isEmpty()) {
            throw new IllegalArgumentException("Account not found.");
        }

        User user = result.get();

        // 3. Generate reference number and date
        String referenceNumber =
                "HTMB" + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        LocalDateTime datetime = LocalDateTime.now();

        // 4. Cash-Out only if sufficient balance exists
        boolean cashedout= userRepository.cashoutBalance(
                user.getUserId(),
                amount
        );

        if (!cashedout) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        // 5. Save transaction history
        transactionRepository.saveCashout(
                referenceNumber,
                user.getUserId(),
                amount,
                "Cashed-Out Successful"
        );

        // 6. Retrieve updated account balance
        User updatedUser = userRepository
                .findByUserId(user.getUserId())
                .orElseThrow(() ->
                        new IllegalStateException("Account not found.")
                );

        // 7. Return receipt information
        return new CashOutResponse(
                "Cashed-Out Successful.",
                referenceNumber,
                updatedUser.getUserId(),
                updatedUser.getName(),
                amount,
                updatedUser.getBalance(),
                datetime
        );
    }
}
