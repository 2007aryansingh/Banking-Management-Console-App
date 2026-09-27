package models;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for all account types.
 *
 * Encapsulation: balance and account details are private, mutated only
 * through validated methods.
 * Abstraction: subclasses must define their own minimum-balance rule,
 * interest/fee behaviour, and a human-readable account type label.
 */
public abstract class Account {

    private static int nextAccountNumberSeed = 100000;

    private final String accountNumber;
    private final String ownerName;
    private double balance;
    private final List<Transaction> history = new ArrayList<>();

    protected Account(String ownerName, double openingBalance) {
        this.accountNumber = "ACC" + (nextAccountNumberSeed++);
        this.ownerName = ownerName;
        this.balance = openingBalance;
    }

    // ---- Abstraction: subclasses must supply these ----
    public abstract double getMinimumBalance();
    public abstract String getAccountType();
    /** Applied by the bank during a periodic maintenance cycle (interest or fee). */
    public abstract double applyPeriodicAdjustment();

    // ---- Shared, concrete behaviour ----

    public final String getAccountNumber() {
        return accountNumber;
    }

    public final String getOwnerName() {
        return ownerName;
    }

    public final double getBalance() {
        return balance;
    }

    public final List<Transaction> getHistory() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Multi-tier deposit handling: validates the amount, classifies it into
     * a DepositTier, deducts any tier fee, and records the transaction.
     */
    public final void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit amount must be positive.");
        }
        DepositTier tier = DepositTier.classify(amount);
        double fee = tier.getFlatFee();
        double net = amount - fee;

        balance += net;
        history.add(new Transaction(Transaction.Type.DEPOSIT, amount, balance,
                tier == DepositTier.STANDARD
                        ? "Standard deposit"
                        : String.format("%s deposit (fee: %.2f)", tier, fee)));

        if (fee > 0) {
            history.add(new Transaction(Transaction.Type.FEE, fee, balance,
                    "Deposit tier fee (" + tier + ")"));
        }
    }

    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal amount must be positive.");
        }
        if (balance - amount < getMinimumBalance()) {
            throw new InsufficientFundsException(String.format(
                    "Withdrawal of %.2f would breach minimum balance of %.2f for %s.",
                    amount, getMinimumBalance(), getAccountType()));
        }
        balance -= amount;
        history.add(new Transaction(Transaction.Type.WITHDRAWAL, amount, balance, "Withdrawal"));
    }

    protected final void recordAdjustment(Transaction.Type type, double amount, String description) {
        balance += amount;
        history.add(new Transaction(type, amount, balance, description));
    }

    @Override
    public String toString() {
        return String.format("%-9s | %-6s | %-15s | Balance: %10.2f",
                accountNumber, getAccountType(), ownerName, balance);
    }
}
