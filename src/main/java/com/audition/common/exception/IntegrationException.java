package com.audition.common.exception;

import java.io.Serial;

/**
 * Exception class that represents integration errors in the system.
 * This exception extends the SystemException class and includes
 * an error code and detail message.
 */
public class IntegrationException extends SystemException {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String TITLE = "Server error";

    /**
     * Constructs a new IntegrationException with the specified detail message
     * and error code.
     *
     * @param detail    the detail message explaining the exception
     * @param errorCode the error code associated with the exception
     */
    public IntegrationException(final String detail, final Integer errorCode) {
        super(detail, TITLE, errorCode);
    }
}
