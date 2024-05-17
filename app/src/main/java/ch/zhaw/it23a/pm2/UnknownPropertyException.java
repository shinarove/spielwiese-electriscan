package ch.zhaw.it23a.pm2;

/**
 * Exception thrown when an unknown property is encountered.
 * This exception is used when a property name does not match any of the known property values in the application.
 * It extends the RuntimeException class, meaning it is an unchecked exception.
 */
public class UnknownPropertyException extends RuntimeException{

    /**
     * Constructs a new UnknownPropertyException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the Throwable.getMessage() method.
     */
    public UnknownPropertyException(String message) {
        super(message);
    }
}
