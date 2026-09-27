import exceptions.AccountNotFoundException;
import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import models.Account;
import models.CurrentAccount;
import models.SavingsAccount;
import models.Transaction;
import services.Bank;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Console entry point. Handles all user I/O and input validation,
 * delegating actual banking logic to the Bank service and Account
 * hierarchy so this class stays a thin presentation layer.
 */
public class BankingApp {

    private final Bank bank = new Bank();
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new BankingApp().run();
    }

    private void run() {
        System.out.println("=========================================");
        System.out.println("   Welcome to the Console Banking System");
        System.out.println("=========================================");

        boolean running = true;
        while (running) {
            printMenu();
            switch (readInt("Choose an option: ")) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> transfer();
                case 5 -> checkBalance();
                case 6 -> printStatement();
                case 7 -> listAllAccounts();
                case 8 -> runMaintenance();
                case 0 -> {
                    running = false;
                    System.out.println("Thank you for banking with us. Goodbye!");
                }
                default -> System.out.println("Invalid option. Please choose a number from the menu.");
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n----------------- MENU -----------------");
        System.out.println("1. Open a new account");
        System.out.println("2. Deposit funds");
        System.out.println("3. Withdraw funds");
        System.out.println("4. Transfer funds");
        System.out.println("5. Check balance");
        System.out.println("6. Print mini statement");
        System.out.println("7. List all accounts");
        System.out.println("8. Run monthly maintenance (interest/fees)");
        System.out.println("0. Exit");
        System.out.println("-----------------------------------------");
    }

    private void createAccount() {
        System.out.print("Enter account holder name: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.println("Account type: 1) Savings  2) Current");
        int type = readInt("Choose type: ");

        double openingBalance = readDouble("Enter opening deposit amount: ");
        if (openingBalance < 0) {
            System.out.println("Opening balance cannot be negative.");
            return;
        }

        Account account = (type == 2)
                ? new CurrentAccount(name, openingBalance)
                : new SavingsAccount(name, openingBalance);

        bank.openAccount(account);
        System.out.println("Account created successfully!");
        System.out.println(account);
    }

    private void deposit() {
        String accNum = readAccountNumber();
        double amount = readDouble("Enter deposit amount: ");
        try {
            bank.deposit(accNum, amount);
            System.out.println("Deposit successful. New balance: "
                    + bank.findAccount(accNum).getBalance());
        } catch (AccountNotFoundException | InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void withdraw() {
        String accNum = readAccountNumber();
        double amount = readDouble("Enter withdrawal amount: ");
        try {
            bank.withdraw(accNum, amount);
            System.out.println("Withdrawal successful. New balance: "
                    + bank.findAccount(accNum).getBalance());
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void transfer() {
        System.out.print("From account number: ");
        String from = scanner.nextLine().trim();
        System.out.print("To account number: ");
        String to = scanner.nextLine().trim();
        double amount = readDouble("Enter transfer amount: ");
        try {
            bank.transfer(from, to, amount);
            System.out.println("Transfer completed successfully.");
        } catch (AccountNotFoundException | InvalidAmountException | InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void checkBalance() {
        String accNum = readAccountNumber();
        try {
            Account account = bank.findAccount(accNum);
            System.out.printf("Balance for %s: %.2f%n", accNum, account.getBalance());
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void printStatement() {
        String accNum = readAccountNumber();
        try {
            Account account = bank.findAccount(accNum);
            System.out.println("Statement for " + accNum + " (" + account.getOwnerName() + "):");
            if (account.getHistory().isEmpty()) {
                System.out.println("  No transactions yet.");
            } else {
                for (Transaction t : account.getHistory()) {
                    System.out.println("  " + t);
                }
            }
        } catch (AccountNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listAllAccounts() {
        if (bank.isEmpty()) {
            System.out.println("No accounts have been opened yet.");
            return;
        }
        System.out.println("All accounts:");
        for (Account account : bank.getAllAccounts()) {
            System.out.println("  " + account);
        }
    }

    private void runMaintenance() {
        bank.runPeriodicMaintenance();
        System.out.println("Monthly maintenance applied (interest credited / fees charged).");
    }

    // ---- Input helpers: defensive programming against malformed input ----

    private String readAccountNumber() {
        System.out.print("Enter account number: ");
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = scanner.nextLine().trim();
            try {
                double value = Double.parseDouble(line);
                if (Double.isNaN(value) || Double.isInfinite(value)) {
                    System.out.println("Please enter a finite number.");
                    continue;
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number (e.g. 250.00).");
            }
        }
    }
}
