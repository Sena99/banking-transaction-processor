package com.bankingapp.service;

import com.bankingapp.exception.InvalidAmountException;
import com.bankingapp.model.Account;
import com.bankingapp.repository.AccountRepository;

import java.math.BigDecimal;

public class BankingService {
    private AccountRepository accountRepository;
    public BankingService(AccountRepository accountRepository) {
      this.accountRepository = accountRepository;
    }

    public Account createAccount(String accountId, BigDecimal initialBalance) {

        if (accountRepository.existsById(accountId)) {
            throw new IllegalArgumentException(
                    "Account already exists: " + accountId
            );
        }
        if (initialBalance == null || initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException();
        }
        Account account = new Account(accountId, initialBalance);

        return accountRepository.save(account);
    }
}
