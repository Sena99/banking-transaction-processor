package com.bankingapp.repository;

import com.bankingapp.model.Account;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AccountRepository {

    private final Map<String, Account> accounts = new HashMap<>();

    public Account save(Account account) {
        accounts.put(account.getId(), account);
        return account;
    }

    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    public boolean existsById(String accountId) {
        return accounts.containsKey(accountId);
    }
}