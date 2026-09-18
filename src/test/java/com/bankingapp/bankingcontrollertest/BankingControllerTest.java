package com.bankingapp.bankingcontrollertest;


import com.bankingapp.controller.BankingController;
import com.bankingapp.exception.AccountNotFoundException;
import com.bankingapp.exception.InsufficientBalanceException;
import com.bankingapp.exception.InvalidAmountException;
import com.bankingapp.model.Account;
import com.bankingapp.model.Transaction;
import com.bankingapp.model.TransactionType;
import com.bankingapp.service.BankingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankingController.class)
class BankingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankingService bankingService;

    @Test
    void shouldCreateAccount() throws Exception {

        Account account = new Account(
                "ACC100",
                new BigDecimal("1000")
        );

        when(bankingService.createAccount(
                eq("ACC100"),
                eq(new BigDecimal("1000"))
        )).thenReturn(account);

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType("application/json")
                                .content("""
                                {
                                    "accountId": "ACC100",
                                    "initialBalance": 1000
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("ACC100"))
                .andExpect(jsonPath("$.balance").value(1000));
    }
    @Test
    void shouldGetAccountBalance() throws Exception {

        when(bankingService.getBalance("ACC100"))
                .thenReturn(new BigDecimal("1000"));

        mockMvc.perform(
                        get("/api/accounts/ACC100/balance")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("1000"));
    }
    @Test
    void shouldGetTransactionHistory() throws Exception {

        Transaction transaction =
                new Transaction(
                        TransactionType.DEPOSIT,
                        new BigDecimal("500")
                );

        when(bankingService.getTransactionHistory("ACC100"))
                .thenReturn(List.of(transaction));

        mockMvc.perform(
                        get("/api/accounts/ACC100/transactions")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                .andExpect(jsonPath("$[0].amount").value(500));
    }
    @Test
    void shouldDepositMoney() throws Exception {

        mockMvc.perform(
                        post("/api/accounts/ACC100/deposit")
                                .contentType("application/json")
                                .content("""
                            {
                                "amount": 500
                            }
                            """)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldWithdrawMoney() throws Exception {

        mockMvc.perform(
                        post("/api/accounts/ACC100/withdraw")
                                .contentType("application/json")
                                .content("""
                            {
                                "amount": 200
                            }
                            """)
                )
                .andExpect(status().isOk());
    }

    @Test
    void shouldTransferMoney() throws Exception {

        mockMvc.perform(
                        post("/api/transfers")
                                .contentType("application/json")
                                .content("""
                            {
                                "fromAccountId": "ACC100",
                                "toAccountId": "ACC200",
                                "amount": 300
                            }
                            """)
                )
                .andExpect(status().isOk());
    }
    @Test
    void shouldReturn404WhenAccountNotFound() throws Exception {

        when(bankingService.getBalance("UNKNOWN"))
                .thenThrow(new AccountNotFoundException("UNKNOWN"));

        mockMvc.perform(
                        get("/api/accounts/UNKNOWN/balance")
                )
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found: UNKNOWN"));
    }
    @Test
    void shouldReturn400WhenDepositAmountIsInvalid() throws Exception {

        doThrow(new InvalidAmountException())
                .when(bankingService)
                .deposit("ACC100", new BigDecimal("-100"));

        mockMvc.perform(
                        post("/api/accounts/ACC100/deposit")
                                .contentType("application/json")
                                .content("""
                            {
                                "amount": -100
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Transaction amount must be greater than zero"
                ));
    }

    @Test
    void shouldReturn400WhenBalanceIsInsufficient() throws Exception {

        doThrow(new InsufficientBalanceException())
                .when(bankingService)
                .withdraw("ACC100", new BigDecimal("5000"));

        mockMvc.perform(
                        post("/api/accounts/ACC100/withdraw")
                                .contentType("application/json")
                                .content("""
                            {
                                "amount": 5000
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Insufficient balance"));
    }
}