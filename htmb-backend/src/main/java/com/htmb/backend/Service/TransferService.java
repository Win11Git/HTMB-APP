
package com.htmb.backend.Service;

import com.htmb.backend.Model.TransferRequest;
import com.htmb.backend.Model.TransferResponse;
import com.htmb.backend.Model.User;
import com.htmb.backend.Repository.UserRepository;
import com.htmb.backend.Repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TransferService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransferService(
            UserRepository userRepository,
            TransactionRepository transactionRepository
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @Transactional
    public TransferResponse transfer(long senderUserId, TransferRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Transfer request is required.");
        }

        if (senderUserId <= 0) {
            throw new IllegalArgumentException("Invalid sender account.");
        }

        if (request.recipientMobileNumber() == null ||
                !request.recipientMobileNumber().matches("^09\\d{9}$")) {
            throw new IllegalArgumentException("Invalid recipient mobile number.");
        }

        BigDecimal amount = request.amount();

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid transfer amount.");
        }

        if (amount.scale() > 2 || amount.precision() > 15) {
            throw new IllegalArgumentException("Invalid transfer amount.");
        }

        User sender = userRepository.findByUserId(senderUserId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Sender account not found."));

        User recipient = userRepository
                .findByMobileNumber(request.recipientMobileNumber())
                .orElseThrow(() ->
                        new IllegalArgumentException("Recipient not found."));

        if (sender.getUserId() == recipient.getUserId()) {
            throw new IllegalArgumentException(
                    "You cannot transfer money to yourself."
            );
        }

        String referenceNumber = "HTMB" +
                UUID.randomUUID().toString()
                        .substring(0, 8)
                        .toUpperCase();

        LocalDateTime datetime = LocalDateTime.now();

        // 1. Deduct money from sender
        boolean deducted = userRepository.transferOut(
                sender.getUserId(), amount
        );

        if (!deducted) {
            throw new IllegalArgumentException("Insufficient balance.");
        }

        // 2. Add money to recipient
        userRepository.transferIn(
                recipient.getUserId(), amount
        );

        // 3. Save sender transaction
        transactionRepository.saveTransferSent(
                referenceNumber,
                sender.getUserId(),
                amount,
                "Recipient account: " + recipient.getMobileNumber()
        );

        // 4. Save recipient transaction
        transactionRepository.saveTransferReceived(
                referenceNumber + "R",
                recipient.getUserId(),
                amount,
                "Sender account: " + sender.getMobileNumber()
        );

        // 5. Retrieve updated balances
        User updatedSender = userRepository
                .findByUserId(sender.getUserId())
                .orElseThrow();

        User updatedRecipient = userRepository
                .findByUserId(recipient.getUserId())
                .orElseThrow();

        // 6. Return receipt information
        return new TransferResponse(
                "Transfer successful.",
                referenceNumber,
                updatedSender.getUserId(),
                updatedSender.getName(),
                updatedSender.getMobileNumber(),
                updatedRecipient.getUserId(),
                updatedRecipient.getName(),
                updatedRecipient.getMobileNumber(),
                amount,
                updatedSender.getBalance(),
                datetime
        );
    }
}
