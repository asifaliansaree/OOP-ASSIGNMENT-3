package exceptionhandler;

/**
 * Custom exception for Rpms system errors, used in ChatServer, Client, and more.
 */
public class RpmsException extends Exception {
    private final String errorCode; // Error type code (e.g., "INVALID_INPUT")

    /**
     * Creates an RpmsException with an error code and message.
     * @param errorCode Error type (e.g., "SESSION_ERROR").
     * @param message What went wrong.
     */
    public RpmsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Creates an RpmsException with error code, message, and cause.
     * @param errorCode Error type.
     * @param message Error description.
     * @param cause Root cause.
     */
    public RpmsException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Creates an RpmsException with a cause.
     * @param cause Root cause.
     */
    public RpmsException(Throwable cause) {
        super(cause);
        this.errorCode = "GENERAL_ERROR";
    }

    /**
     * Creates a basic RpmsException.
     */
    public RpmsException() {
        super("Oops, Rpms hit a snag!");
        this.errorCode = "GENERAL_ERROR";
    }

    /**
     * Gets the error code.
     * @return Error code.
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Logs the error with a logger.
     * @param logger Logger to use.
     */
    public void log(java.util.logging.Logger logger) {
        String logMessage = String.format(
            "RpmsException [Code: %s]: %s%s",
            errorCode,
            getMessage(),
            getCause() != null ? ", Cause: " + getCause().getMessage() : ""
        );
        logger.severe(logMessage);
    }
}