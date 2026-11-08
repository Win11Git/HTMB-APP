
package com.htmb.backend.Model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class User {

    private final String name;
    private final String pin;
    private final String mobileNumber;
    private final long userId;
    private BigDecimal balance;

    public User(String name, String mobileNumber, String pin, long userId) {
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.pin = pin;
        this.userId = userId;
        this.balance = BigDecimal.ZERO;
    }

    public String getName() {
        return name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getPin() {
        return pin;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public long getUserId() {
        return userId;
    }

    public void setBalance(BigDecimal balance) {
        if (balance == null || balance.signum() < 0) {
            throw new IllegalArgumentException("Invalid balance.");
        }

        this.balance = balance;
    }

}
