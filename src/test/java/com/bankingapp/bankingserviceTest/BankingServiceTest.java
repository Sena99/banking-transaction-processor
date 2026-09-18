package com.bankingapp.bankingserviceTest;

import com.bankingapp.exception.AccountNotFoundException;
import com.bankingapp.exception.InsufficientBalanceException;
import com.bankingapp.exception.InvalidAmountException;
import com.bankingapp.model.Account;
import com.bankingapp.model.Transaction;
import com.bankingapp.model.TransactionType;
import com.bankingapp.repository.AccountRepository;
import com.bankingapp.service.BankingService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BankingServiceTest {

    private final AccountRepository accountRepository =
            new AccountRepository();

    private final BankingService bankingService =
            new BankingService(accountRepository);

    @Test
    void createAccount() {

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
    void rejectDuplicateAccountId() {

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
    void rejectNegativeInitialBalance() {

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.createAccount(
                        "ACC002",
                        new BigDecimal("-100")
                )
        );
    }

    @Test
    void depositMoney() {

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

    @Test
    void shouldRejectZeroDeposit() {
        bankingService.createAccount("ACC004", new BigDecimal("1000"));

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit("ACC004", BigDecimal.ZERO)
        );
    }

    @Test
    void shouldRejectNegativeDeposit() {
        bankingService.createAccount("ACC005", new BigDecimal("1000"));

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.deposit("ACC005", new BigDecimal("-100"))
        );
    }

    @Test
    void shouldRejectDepositForUnknownAccount() {
        assertThrows(
                AccountNotFoundException.class,
                () -> bankingService.deposit("UNKNOWN", new BigDecimal("500"))
        );
    }

    @Test
    void shouldRecordDepositTransaction() {
        bankingService.createAccount("ACC006", new BigDecimal("1000"));

        bankingService.deposit("ACC006", new BigDecimal("500"));

        Account account = accountRepository.findById("ACC006").orElseThrow();

        assertEquals(1, account.getTransactions().size());

        Transaction transaction = account.getTransactions().get(0);

        assertEquals(TransactionType.DEPOSIT, transaction.getType());
        assertEquals(new BigDecimal("500"), transaction.getAmount());
        assertNotNull(transaction.getTransactionId());
        assertNotNull(transaction.getTimestamp());
    }
    @Test
    void shouldWithdrawMoney() {
        bankingService.createAccount("ACC007", new BigDecimal("1000"));

        bankingService.deposit("ACC007", new BigDecimal("500"));
        bankingService.withdraw("ACC007", new BigDecimal("300"));

        assertEquals(
                new BigDecimal("1200"),
                bankingService.getBalance("ACC007")
        );
    }
    @Test
    void shouldRejectWithdrawalWhenBalanceIsInsufficient() {
        bankingService.createAccount("ACC008", new BigDecimal("1000"));

        assertThrows(
                InsufficientBalanceException.class,
                () -> bankingService.withdraw("ACC008", new BigDecimal("1500"))
        );
    }

    @Test
    void shouldRejectZeroWithdrawal() {
        bankingService.createAccount("ACC009", new BigDecimal("1000"));

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.withdraw("ACC009", BigDecimal.ZERO)
        );
    }
    @Test
    void shouldRejectNegativeWithdrawal() {
        bankingService.createAccount("ACC010", new BigDecimal("1000"));

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.withdraw("ACC010", new BigDecimal("-100"))
        );
    }
    @Test
    void shouldRejectWithdrawalForUnknownAccount() {
        assertThrows(
                AccountNotFoundException.class,
                () -> bankingService.withdraw("UNKNOWN", new BigDecimal("500"))
        );
    }
    @Test
    void shouldRecordWithdrawalTransaction() {
        bankingService.createAccount("ACC011", new BigDecimal("1000"));

        bankingService.withdraw("ACC011", new BigDecimal("300"));

        Account account = accountRepository.findById("ACC011").orElseThrow();

        assertEquals(1, account.getTransactions().size());

        Transaction transaction = account.getTransactions().get(0);

        assertEquals(TransactionType.WITHDRAWAL, transaction.getType());
        assertEquals(new BigDecimal("300"), transaction.getAmount());
        assertNotNull(transaction.getTransactionId());
        assertNotNull(transaction.getTimestamp());
    }

    @Test
    void shouldTransferMoneyBetweenAccounts() {
        bankingService.createAccount("ACC012", new BigDecimal("1000"));
        bankingService.createAccount("ACC013", new BigDecimal("500"));

        bankingService.transfer(
                "ACC012",
                "ACC013",
                new BigDecimal("300")
        );

        assertEquals(
                new BigDecimal("700"),
                bankingService.getBalance("ACC012")
        );

        assertEquals(
                new BigDecimal("800"),
                bankingService.getBalance("ACC013")
        );
    }

    @Test
    void shouldRejectTransferWhenBalanceIsInsufficient() {
        bankingService.createAccount("ACC014", new BigDecimal("1000"));
        bankingService.createAccount("ACC015", new BigDecimal("500"));

        assertThrows(
                InsufficientBalanceException.class,
                () -> bankingService.transfer(
                        "ACC014",
                        "ACC015",
                        new BigDecimal("1500")
                )
        );

        assertEquals(
                new BigDecimal("1000"),
                bankingService.getBalance("ACC014")
        );

        assertEquals(
                new BigDecimal("500"),
                bankingService.getBalance("ACC015")
        );
    }

    @Test
    void shouldRejectZeroTransfer() {
        bankingService.createAccount("ACC016", new BigDecimal("1000"));
        bankingService.createAccount("ACC017", new BigDecimal("500"));

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.transfer(
                        "ACC016",
                        "ACC017",
                        BigDecimal.ZERO
                )
        );
    }

    @Test
    void shouldRejectNegativeTransfer() {
        bankingService.createAccount("ACC018", new BigDecimal("1000"));
        bankingService.createAccount("ACC019", new BigDecimal("500"));

        assertThrows(
                InvalidAmountException.class,
                () -> bankingService.transfer(
                        "ACC018",
                        "ACC019",
                        new BigDecimal("-100")
                )
        );
    }
    @Test
    void shouldRejectTransferWhenSourceAccountDoesNotExist() {
        bankingService.createAccount("ACC020", new BigDecimal("500"));

        assertThrows(
                AccountNotFoundException.class,
                () -> bankingService.transfer(
                        "UNKNOWN",
                        "ACC020",
                        new BigDecimal("100")
                )
        );
    }
    @Test
    void shouldRejectTransferWhenDestinationAccountDoesNotExist() {
        bankingService.createAccount("ACC021", new BigDecimal("1000"));

        assertThrows(
                AccountNotFoundException.class,
                () -> bankingService.transfer(
                        "ACC021",
                        "UNKNOWN",
                        new BigDecimal("100")
                )
        );
    }
    @Test
    void shouldRecordTransferTransactions() {
        bankingService.createAccount("ACC022", new BigDecimal("1000"));
        bankingService.createAccount("ACC023", new BigDecimal("500"));

        bankingService.transfer(
                "ACC022",
                "ACC023",
                new BigDecimal("300")
        );

        Account sourceAccount =
                accountRepository.findById("ACC022").orElseThrow();

        Account destinationAccount =
                accountRepository.findById("ACC023").orElseThrow();

        assertEquals(1, sourceAccount.getTransactions().size());
        assertEquals(1, destinationAccount.getTransactions().size());

        Transaction sourceTransaction =
                sourceAccount.getTransactions().get(0);

        Transaction destinationTransaction =
                destinationAccount.getTransactions().get(0);

        assertEquals(TransactionType.TRANSFER, sourceTransaction.getType());
        assertEquals(TransactionType.TRANSFER, destinationTransaction.getType());

        assertEquals(new BigDecimal("300"), sourceTransaction.getAmount());
        assertEquals(new BigDecimal("300"), destinationTransaction.getAmount());

        assertNotNull(sourceTransaction.getTransactionId());
        assertNotNull(destinationTransaction.getTransactionId());

        assertNotNull(sourceTransaction.getTimestamp());
        assertNotNull(destinationTransaction.getTimestamp());
    }
    @Test
    void shouldReturnTransactionHistory() {
        bankingService.createAccount("ACC024", new BigDecimal("1000"));

        bankingService.deposit("ACC024", new BigDecimal("500"));
        bankingService.withdraw("ACC024", new BigDecimal("200"));

        List<Transaction> transactions =
                bankingService.getTransactionHistory("ACC024");

        assertEquals(2, transactions.size());

        assertEquals(
                TransactionType.DEPOSIT,
                transactions.get(0).getType()
        );

        assertEquals(
                new BigDecimal("500"),
                transactions.get(0).getAmount()
        );

        assertEquals(
                TransactionType.WITHDRAWAL,
                transactions.get(1).getType()
        );

        assertEquals(
                new BigDecimal("200"),
                transactions.get(1).getAmount()
        );
    }
    @Test
    void shouldRejectTransactionHistoryForUnknownAccount() {
        assertThrows(
                AccountNotFoundException.class,
                () -> bankingService.getTransactionHistory("UNKNOWN")
        );
    }
}


