package exceptions;

/**
 * Thrown when a withdrawal or transfer would cause an account to violate
 * its minimum balance rule, or when funds simply aren't available.
 */
public class InsufficientFundsException extends Exception {

    public InsufficientFundsException(String message) {
        super(message);
    }
}
