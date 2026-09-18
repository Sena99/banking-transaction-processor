package com.bankingapp.accountrepositoryTest;

import com.bankingapp.model.Account;
import com.bankingapp.repository.AccountRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private final AccountRepository repository = new AccountRepository();

    @Test
    void shouldSaveAndFindAccount() {

        Account account =
                new Account("ACC001", new BigDecimal("1000"));

        repository.save(account);

        Optional<Account> result =
                repository.findById("ACC001");

        assertTrue(result.isPresent());
        assertEquals("ACC001", result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenAccountDoesNotExist() {

        Optional<Account> result =
                repository.findById("UNKNOWN");

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldCheckIfAccountExists() {

        Account account =
                new Account("ACC001", new BigDecimal("1000"));

        repository.save(account);

        assertTrue(repository.existsById("ACC001"));
        assertFalse(repository.existsById("ACC002"));
    }
}