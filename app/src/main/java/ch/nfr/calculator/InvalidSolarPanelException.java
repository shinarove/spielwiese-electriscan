package ch.nfr.calculator;

/**
 * Exception thrown when a solar panel is invalid.
 */
public class InvalidSolarPanelException extends RuntimeException {

    /**
     * Constructor for the InvalidSolarPanelException.
     * @param message the message to be displayed when the exception is thrown.
     */
    public InvalidSolarPanelException(String message) {
        super(message);
    }
}
