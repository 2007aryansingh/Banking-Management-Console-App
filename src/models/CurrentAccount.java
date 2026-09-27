package models;

import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;

/**
 * Inheritance + polymorphism: a current (checking) account allows a
 * negative balance up to an overdraft limit and charges a flat monthly
 * maintenance fee instead of paying interest.
 */
public class CurrentAccount extends Account {

    private static final double OVERDRAFT_LIMIT = -1_000.0;
    private static final double MONTHLY_MAINTENANCE_FEE = 15.0;

    public CurrentAccount(String ownerName, double openingBalance) {
        super(ownerName, openingBalance);
    }

    @Override
    public double getMinimumBalance() {
        return OVERDRAFT_LIMIT;
    }

    @Override
    public String getAccountType() {
        return "CURRENT";
    }

    @Override
    public double applyPeriodicAdjustment() {
        double fee = -MONTHLY_MAINTENANCE_FEE;
        recordAdjustment(models.Transaction.Type.FEE, fee, "Monthly maintenance fee");
        return fee;
    }

    /**
     * Overridden to demonstrate polymorphic behaviour: current accounts
     * are allowed to dip into the overdraft, so the withdraw rule differs
     * from the generic Account rule only in threshold (handled via
     * getMinimumBalance()), but we override here to add an overdraft
     * notice to the transaction description.
     */
    @Override
    public void withdraw(double amount) throws InvalidAmountException, InsufficientFundsException {
        boolean willOverdraw = getBalance() - amount < 0;
        super.withdraw(amount);
        if (willOverdraw) {
            System.out.println("Notice: account is now in overdraft.");
        }
    }
}
