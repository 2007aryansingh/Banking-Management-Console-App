package services;

import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import models.Account;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Central service that owns the account store and coordinates operations
 * that touch more than one account (e.g. transfers). Uses a Map for O(1)
 * account lookups by account number.
 */
public class Bank {

    private final Map<String, Account> accounts = new LinkedHashMap<>();

    public Account openAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
        return account;
    }

    public Account findAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException("No account found with number: " + accountNumber);
        }
        return account;
    }

    public void deposit(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException {
        findAccount(accountNumber).deposit(amount);
    }

    public void withdraw(String accountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        findAccount(accountNumber).withdraw(amount);
    }

    /**
     * Transfers funds between two accounts. Rolls back the withdrawal if
     * the deposit side somehow fails, keeping the ledger consistent.
     */
    public void transfer(String fromAccountNumber, String toAccountNumber, double amount)
            throws AccountNotFoundException, InvalidAmountException, InsufficientFundsException {
        Account from = findAccount(fromAccountNumber);
        Account to = findAccount(toAccountNumber);

        from.withdraw(amount);
        try {
            to.deposit(amount);
        } catch (InvalidAmountException e) {
            // Roll back the debit so money is never lost mid-transfer.
            try {
                from.deposit(amount);
            } catch (InvalidAmountException rollbackFailure) {
                throw new IllegalStateException("Critical: rollback failed during transfer.", rollbackFailure);
            }
            throw e;
        }
    }

    /** Runs interest/fee adjustments across every account (e.g. end-of-month job). */
    public void runPeriodicMaintenance() {
        for (Account account : accounts.values()) {
            account.applyPeriodicAdjustment();
        }
    }

    public Collection<Account> getAllAccounts() {
        return accounts.values();
    }

    public boolean isEmpty() {
        return accounts.isEmpty();
    }
}
