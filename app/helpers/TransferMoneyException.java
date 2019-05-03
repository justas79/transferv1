package helpers;

/**
 * Exception wrapper to identify and classify errors
 */
public class TransferMoneyException extends RuntimeException {
    public TransferMoneyException(String s) {
        super(s);
    }
}
