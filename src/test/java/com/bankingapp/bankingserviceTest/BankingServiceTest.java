package com.bankingapp.bankingserviceTest;

import com.bankingapp.exception.InvalidAmountException;
import com.bankingapp.model.Account;
import com.bankingapp.repository.AccountRepository;
import com.bankingapp.service.BankingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankingServiceTest {

    private final AccountRepository accountRepository =
            new AccountRepository();

    private final BankingService bankingService =
            new BankingService(accountRepository);

    @Test
    void shouldCreateAccount() {

        Account account = bankingService.createAccount(
                "ACC001",
                new BigDecimal("1000")
        );

        assertEquals("ACC001", account.getId());
        assertEquals(
                new BigDecimal("1000"),
                account.getBalance()
        );
    }
    @Test
    void shouldRejectDuplicateAccountId() {

        bankingService.createAccount(
                "ACC001",
                new BigDecimal("1000")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> bankingService.createAccount(
                        "ACC001",
                        new BigDecimal("500")
                )
        );
    }
    @Test
    void shouldRejectNegativeInitialBalance() {

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.createAccount(
                        "ACC002",
                        new BigDecimal("-100")
                )
        );
    }

    @Test
    void shouldDepositMoney() {

        bankingService.createAccount(
                "ACC003",
                new BigDecimal("1000")
        );

        bankingService.deposit(
                "ACC003",
                new BigDecimal("500")
        );

        assertEquals(
                new BigDecimal("1500"),
                bankingService.getBalance("ACC003")
        );
    }
}
