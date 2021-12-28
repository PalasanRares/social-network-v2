package com.example.labsocialnetworkv2.validator.exception;

public class InvalidPasswordException extends ValidationException{
    /**
     * Empty constructor
     */
    public InvalidPasswordException() {
    }

    /**
     * Constructor with message
     * @param message error message
     */
    public InvalidPasswordException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause
     * @param message error message
     * @param cause error cause
     */
    public InvalidPasswordException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with cause
     * @param cause error cause
     */
    public InvalidPasswordException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with message cause and additional options
     * @param message error message
     * @param cause error cause
     * @param enableSuppression suppression modifier
     * @param writableStackTrace stack trace modifier
     */
    public InvalidPasswordException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
