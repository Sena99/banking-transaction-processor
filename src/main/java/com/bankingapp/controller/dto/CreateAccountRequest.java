package com.bankingapp.controller.dto;

import java.math.BigDecimal;

public class CreateAccountRequest {

    private String accountId;
    private BigDecimal initialBalance;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}