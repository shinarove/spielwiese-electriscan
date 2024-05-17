package ch.nfr.filehandler;

/**
 * This class is responsible for handling exceptions that occur when the weather archive is read.
 */
public class WeatherArchiveException extends Exception{

    /**
     * Constructs a new WeatherArchiveException with the specified detail message.
     * @param message the detail message
     */
    public WeatherArchiveException(String message) {
        super(message);
    }

    /**
     * Constructs a new WeatherArchiveException with the specified detail message and cause.
     * @param message the detail message
     * @param cause the cause
     */
    public WeatherArchiveException(String message, Throwable cause) {
        super(message, cause);
    }
}
