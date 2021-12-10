package com.example.labsocialnetworkv2.validator.exception;

/**
 * Exception to be thrown if id is equal to zero
 */
public class IdZeroException extends ValidationException {
    /**
     * Empty constructor
     */
    public IdZeroException() {
    }

    /**
     * Constructor with message
     * @param message error message
     */
    public IdZeroException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * @param message error message
     * @param cause error cause
     */
    public IdZeroException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause
     * @param cause error cause
     */
    public IdZeroException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with message cause and additional options
     * @param message error message
     * @param cause error cause
     * @param enableSuppression suppression modifier
     * @param writableStackTrace stack trace modifier
     */
    public IdZeroException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
