package com.bankingapp.controller;

import com.bankingapp.controller.dto.CreateAccountRequest;
import com.bankingapp.controller.dto.DepositRequest;
import com.bankingapp.controller.dto.TransferRequest;
import com.bankingapp.controller.dto.WithdrawRequest;
import com.bankingapp.model.Account;
import com.bankingapp.model.Transaction;
import com.bankingapp.service.BankingService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
public class BankingController {

    private final BankingService bankingService;

    public BankingController(BankingService bankingService) {
        this.bankingService = bankingService;
    }

    @PostMapping("/api/accounts")
    public Account createAccount(@RequestBody CreateAccountRequest request) {

        return bankingService.createAccount(
                request.getAccountId(),
                request.getInitialBalance()
        );
    }

    @GetMapping("/api/accounts/{accountId}/balance")
    public BigDecimal getBalance(@PathVariable String accountId) {
        return bankingService.getBalance(accountId);
    }

    @GetMapping("/api/accounts/{accountId}/transactions")
    public List<Transaction> getTransactionHistory(@PathVariable String accountId) {
        return bankingService.getTransactionHistory(accountId);
    }

    @PostMapping("/api/accounts/{accountId}/deposit")
    public void deposit(
            @PathVariable String accountId,
            @RequestBody DepositRequest request) {

        bankingService.deposit(accountId, request.getAmount());
    }


    @PostMapping("/api/accounts/{accountId}/withdraw")
    public void withdraw(
            @PathVariable String accountId,
            @RequestBody WithdrawRequest request) {

        bankingService.withdraw(accountId, request.getAmount());
    }

    @PostMapping("/api/transfers")
    public void transfer(@RequestBody TransferRequest request) {

        bankingService.transfer(
                request.getFromAccountId(),
                request.getToAccountId(),
                request.getAmount()
        );
    }
}
