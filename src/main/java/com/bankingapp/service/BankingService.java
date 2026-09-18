package com.bankingapp.service;

import com.bankingapp.exception.AccountNotFoundException;
import com.bankingapp.exception.InvalidAmountException;
import com.bankingapp.model.Account;
import com.bankingapp.model.Transaction;
import com.bankingapp.model.TransactionType;
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

    public void deposit(String accountId, BigDecimal amount) {

        Account account=accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException(accountId));

        if (account == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
        account.deposit(amount);

        Transaction transaction=new Transaction(
                TransactionType.DEPOSIT,amount
        );
        account.addTransaction(transaction);
    }

    public BigDecimal getBalance(String accountId) {
        Account account=accountRepository.findById(accountId)
                .orElseThrow(()-> new AccountNotFoundException(accountId));
        return account.getBalance();
    }
}
