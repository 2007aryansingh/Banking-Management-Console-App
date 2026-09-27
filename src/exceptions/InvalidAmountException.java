package exceptions;

/**
 * Thrown when a deposit/withdrawal amount fails validation
 * (negative, zero, exceeds tier limits, etc.).
 */
public class InvalidAmountException extends Exception {

    public InvalidAmountException(String message) {
        super(message);
    }
}
