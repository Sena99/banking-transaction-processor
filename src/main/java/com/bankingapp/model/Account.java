package com.bankingapp.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Account {

    private final String id;
    private BigDecimal balance;
    private final List<Transaction> transactions;

    public Account(String id, BigDecimal initialBalance) {
        this.id = id;
        this.balance = initialBalance;
        this.transactions = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }
}