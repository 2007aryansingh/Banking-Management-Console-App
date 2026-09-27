package models;

/**
 * Inheritance + polymorphism: overrides the abstract rules from Account
 * with savings-specific behaviour (minimum balance, monthly interest).
 */
public class SavingsAccount extends Account {

    private static final double MIN_BALANCE = 500.0;
    private static final double MONTHLY_INTEREST_RATE = 0.035 / 12; // 3.5% APR

    public SavingsAccount(String ownerName, double openingBalance) {
        super(ownerName, openingBalance);
    }

    @Override
    public double getMinimumBalance() {
        return MIN_BALANCE;
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    @Override
    public double applyPeriodicAdjustment() {
        double interest = getBalance() * MONTHLY_INTEREST_RATE;
        recordAdjustment(models.Transaction.Type.INTEREST, interest, "Monthly interest credited");
        return interest;
    }
}
