
package com.htmb.backend.Model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Transaction {

    private String referenceNumber;
    private String type;
    private BigDecimal amount;
    private String details;
    private LocalDateTime datetime;

    public Transaction(String referenceNumber, String type,
                       BigDecimal amount, String details,
                       LocalDateTime datetime) {

        setReferenceNumber(referenceNumber);
        setType(type);
        setAmount(amount);
        setDetails(details);
        setDatetime(datetime);
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        if (referenceNumber == null || referenceNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Reference number is required.");
        }

        this.referenceNumber = referenceNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException(
                    "Transaction type is required.");
        }

        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Transaction amount must be positive.");
        }

        this.amount = amount;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = Objects.requireNonNull(
                datetime,
                "Transaction date and time cannot be null.");
    }
}
