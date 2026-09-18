package com.bankingapp.service;

import com.bankingapp.exception.AccountNotFoundException;
import com.bankingapp.exception.InsufficientBalanceException;
import com.bankingapp.exception.InvalidAmountException;
import com.bankingapp.model.Account;
import com.bankingapp.model.Transaction;
import com.bankingapp.model.TransactionType;
import com.bankingapp.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
@Service
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
    public void withdraw(String accountId, BigDecimal amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        account.withdraw(amount);

        Transaction transaction = new Transaction(
                TransactionType.WITHDRAWAL,
                amount
        );

        account.addTransaction(transaction);
    }

    public void transfer(String fromAccountId,
                         String toAccountId,
                         BigDecimal amount) {

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException(fromAccountId));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException(toAccountId));

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException();
        }

        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        Transaction withdrawalTransaction =
                new Transaction(TransactionType.TRANSFER, amount);

        Transaction depositTransaction =
                new Transaction(TransactionType.TRANSFER, amount);

        fromAccount.addTransaction(withdrawalTransaction);
        toAccount.addTransaction(depositTransaction);
    }

    public List<Transaction> getTransactionHistory(String accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        return account.getTransactions();
    }
}
