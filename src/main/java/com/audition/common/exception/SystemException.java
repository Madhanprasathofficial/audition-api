package com.audition.common.exception;

import lombok.Getter;

/**
 * Base exception class that represents a system error.
 * This class extends RuntimeException and includes additional
 * fields for status code, title, and detail message.
 */
@Getter
public class SystemException extends RuntimeException {

    private static final long serialVersionUID = -5876728854007114881L;

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private Integer statusCode;
    private String title;
    private String detail;

    /**
     * Default constructor.
     */
    public SystemException() {
        super();
    }

    /**
     * Constructs a new SystemException with the specified message.
     *
     * @param message the detail message
     */
    public SystemException(final String message) {
        super(message);
        this.title = DEFAULT_TITLE;
    }

    /**
     * Constructs a new SystemException with the specified message and error code.
     *
     * @param message   the detail message
     * @param errorCode the error code associated with the exception
     */
    public SystemException(final String message, final Integer errorCode) {
        super(message);
        this.title = DEFAULT_TITLE;
        this.statusCode = errorCode;
    }

    /**
     * Constructs a new SystemException with the specified message and cause.
     *
     * @param message   the detail message
     * @param exception the cause of the exception
     */
    public SystemException(final String message, final Throwable exception) {
        super(message, exception);
        this.title = DEFAULT_TITLE;
    }

    /**
     * Constructs a new SystemException with the specified detail, title, and error code.
     *
     * @param detail    the detail message
     * @param title     the title of the exception
     * @param errorCode the error code associated with the exception
     */
    public SystemException(final String detail, final String title, final Integer errorCode) {
        super(detail);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }

    /**
     * Constructs a new SystemException with the specified detail, title, and cause.
     *
     * @param detail    the detail message
     * @param title     the title of the exception
     * @param exception the cause of the exception
     */
    public SystemException(final String detail, final String title, final Throwable exception) {
        super(detail, exception);
        this.title = title;
        this.statusCode = 500;
        this.detail = detail;
    }

    /**
     * Constructs a new SystemException with the specified detail, error code, and cause.
     *
     * @param detail    the detail message
     * @param errorCode the error code associated with the exception
     * @param exception the cause of the exception
     */
    public SystemException(final String detail, final Integer errorCode, final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = DEFAULT_TITLE;
        this.detail = detail;
    }

    /**
     * Constructs a new SystemException with the specified detail, title, error code, and cause.
     *
     * @param detail    the detail message
     * @param title     the title of the exception
     * @param errorCode the error code associated with the exception
     * @param exception the cause of the exception
     */
    public SystemException(final String detail, final String title, final Integer errorCode, final Throwable exception) {
        super(detail, exception);
        this.statusCode = errorCode;
        this.title = title;
        this.detail = detail;
    }
}
