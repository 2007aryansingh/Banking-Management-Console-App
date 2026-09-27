# Console Banking Management System

An enterprise-style console banking application built in Java, demonstrating
core OOP principles, custom exception handling, and layered architecture.

## Features

- **Account lifecycle**: open accounts, deposit, withdraw, transfer, check balance, view mini statements.
- **Multi-tier deposits**: deposits are classified into `STANDARD`, `HIGH_VALUE`, and `PREMIUM` tiers
  (`models/DepositTier.java`), each with its own transaction fee — mirroring real bank scrutiny on
  larger deposits.
- **Two account types**: `SavingsAccount` (minimum balance + monthly interest) and
  `CurrentAccount` (overdraft allowance + monthly maintenance fee).
- **Defensive programming**: every user-facing operation validates input and never lets the
  application crash on bad data (see `BankingApp`'s `readInt`/`readDouble` loops).

## OOP principles applied

| Principle       | Where |
|------------------|-------|
| Encapsulation    | `Account` keeps `balance` and transaction `history` private, exposed via controlled methods. |
| Abstraction      | `Account` is `abstract` with `getMinimumBalance()`, `getAccountType()`, `applyPeriodicAdjustment()` left for subclasses. |
| Inheritance      | `SavingsAccount` and `CurrentAccount` extend `Account`. |
| Polymorphism     | `Bank` and `BankingApp` operate on the `Account` type; each subclass's overridden methods run at call time (e.g. `CurrentAccount.withdraw` adds an overdraft notice, `applyPeriodicAdjustment` credits interest vs. charges a fee). |

## Exception handling

Three checked exceptions model real failure states instead of using generic
`RuntimeException` or return codes:

- `InvalidAmountException` — non-positive or malformed amounts.
- `InsufficientFundsException` — a withdrawal/transfer would breach an account's minimum balance rule.
- `AccountNotFoundException` — an operation references an account number that doesn't exist.

`Bank.transfer()` also demonstrates a compensating rollback: if the deposit leg of a transfer
fails after the withdrawal leg succeeded, the withdrawn amount is redeposited so funds are
never lost mid-transfer.

## Project structure

```
banking-system/
├── README.md
└── src/
    ├── BankingApp.java          # Console UI / entry point (main)
    ├── exceptions/
    │   ├── AccountNotFoundException.java
    │   ├── InsufficientFundsException.java
    │   └── InvalidAmountException.java
    ├── models/
    │   ├── Account.java         # abstract base class
    │   ├── SavingsAccount.java
    │   ├── CurrentAccount.java
    │   ├── DepositTier.java     # multi-tier deposit rules
    │   └── Transaction.java     # immutable transaction record
    └── services/
        └── Bank.java            # account store + cross-account operations
```

## How to compile and run

Requires a JDK (17+ recommended; the code uses Java 14+ switch expressions).

```bash
cd banking-system
mkdir -p bin
javac -d bin $(find src -name "*.java")
java -cp bin BankingApp
```

## Example session

```
1. Open a new account          -> creates a SavingsAccount or CurrentAccount
2. Deposit funds                -> try depositing 25000 to see the PREMIUM tier fee applied
3. Withdraw funds                -> try over-withdrawing to see InsufficientFundsException handled
4. Transfer funds                -> move money between two account numbers
6. Print mini statement          -> see the full transaction ledger for an account
8. Run monthly maintenance       -> credits interest on savings, charges fees on current accounts
```

> **Note:** This code was written and reviewed carefully for correctness, but this sandbox
> environment doesn't have a JDK (only a JRE) available to actually run `javac` and confirm a
> clean compile. Please compile it in your own environment and let me know if anything needs
> a fix.
