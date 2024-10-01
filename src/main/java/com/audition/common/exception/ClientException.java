package com.audition.common.exception;

import java.io.Serial;

/**
 * Exception class that represents client errors in the system.
 * This exception extends the SystemException class and includes
 * an error code and detail message.
 */
public class ClientException extends SystemException {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String TITLE = "Client error";

    /**
     * Constructs a new ClientException with the specified detail message
     * and error code.
     *
     * @param detail    the detail message explaining the exception
     * @param errorCode the error code associated with the exception
     */
    public ClientException(final String detail, final Integer errorCode) {
        super(detail, TITLE, errorCode);
    }
}
