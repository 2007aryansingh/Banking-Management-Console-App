package models;

/**
 * Represents multi-tier deposit rules. Each tier defines a maximum
 * single-transaction deposit limit and a per-transaction fee that
 * applies once a deposit crosses into a higher tier. This models
 * real-world banking rules where larger deposits face more scrutiny
 * (and sometimes fees / hold periods).
 */
public enum DepositTier {

    STANDARD(0.0, 5_000.0, 0.0),
    HIGH_VALUE(5_000.0, 20_000.0, 2.50),
    PREMIUM(20_000.0, Double.MAX_VALUE, 10.00);

    private final double lowerBound;   // inclusive
    private final double upperBound;   // exclusive (or MAX_VALUE for top tier)
    private final double flatFee;

    DepositTier(double lowerBound, double upperBound, double flatFee) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.flatFee = flatFee;
    }

    public double getFlatFee() {
        return flatFee;
    }

    /**
     * Determines which tier a given deposit amount falls into.
     */
    public static DepositTier classify(double amount) {
        for (DepositTier tier : values()) {
            if (amount >= tier.lowerBound && amount < tier.upperBound) {
                return tier;
            }
        }
        return PREMIUM;
    }
}
