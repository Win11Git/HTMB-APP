
package com.htmb.backend.Service;

import com.htmb.backend.Model.Transaction;
import com.htmb.backend.Model.TransactionHistoryResponse;
import com.htmb.backend.Model.TransactionHistoryResponse.TransactionItem;
import com.htmb.backend.Repository.UserRepository;
import com.htmb.backend.Repository.TransactionRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionHistoryService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public TransactionHistoryService(
            UserRepository userRepository,
            TransactionRepository transactionRepository
    ) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    public TransactionHistoryResponse getHistory(long userId) {

        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        // Check whether the account exists
        userRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Account not found."));

        // Retrieve the user's transactions
        List<Transaction> transactions =
                transactionRepository.findByUserId(userId);

        // Convert Transaction objects into response items
        List<TransactionItem> items = transactions.stream()
                .map(transaction -> new TransactionItem(
                        transaction.getReferenceNumber(),
                        transaction.getType(),
                        transaction.getAmount(),
                        transaction.getDetails(),
                        transaction.getDatetime()
                ))
                .toList();

        return new TransactionHistoryResponse(
                items.isEmpty()
                        ? "No transactions found."
                        : "Transaction history retrieved successfully.",
                userId,
                items
        );
    }
}
