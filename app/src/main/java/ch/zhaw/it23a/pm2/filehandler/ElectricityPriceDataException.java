package ch.zhaw.it23a.pm2.filehandler;

/**
 * Exception thrown when an error occurs while reading the electricity price data.
 */
public class ElectricityPriceDataException extends Exception {

    /**
     * Constructs a new ElectricityPriceDataException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public ElectricityPriceDataException(String message) {
        super(message);
    }

    /**
     * Constructs a new ElectricityPriceDataException with the specified detail message and cause.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     * @param cause the cause. The cause is saved for later retrieval by the {@link #getCause()} method.
     */
    public ElectricityPriceDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
