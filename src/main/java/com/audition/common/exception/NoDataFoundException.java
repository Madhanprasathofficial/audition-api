package com.audition.common.exception;

import java.io.Serial;

/**
 * Exception class that represents a situation where no data is found in the system.
 * This exception extends the SystemException class and includes an error code
 * and detail message.
 */
public class NoDataFoundException extends SystemException {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String TITLE = "Data error";

    /**
     * Constructs a new NoDataFoundException with the specified detail message
     * and error code.
     *
     * @param detail    the detail message explaining the exception
     * @param errorCode the error code associated with the exception
     */
    public NoDataFoundException(final String detail, final Integer errorCode) {
        super(detail, TITLE, errorCode);
    }
}
