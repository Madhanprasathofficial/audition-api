package com.audition.common.logging;

import org.slf4j.Logger;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;

/**
 * Custom logger class for handling logging operations with different log levels.
 * This class encapsulates the SLF4J logger and provides methods for logging
 * messages, errors, and standard problem details.
 */
@Component
public class AuditionLogger {

    /**
     * Logs an info message if the logger is enabled for info level.
     *
     * @param logger  The SLF4J logger instance
     * @param message The message to log
     */
    public void info(final Logger logger, final String message) {
        if (logger.isInfoEnabled()) {
            logger.info(message);
        }
    }

    /**
     * Logs an info message with an associated object if the logger is enabled for info level.
     *
     * @param logger  The SLF4J logger instance
     * @param message The message to log
     * @param object  The object to log with the message
     */
    public void info(final Logger logger, final String message, final Object object) {
        if (logger.isInfoEnabled()) {
            logger.info(message, object);
        }
    }

    /**
     * Logs a debug message if the logger is enabled for debug level.
     *
     * @param logger  The SLF4J logger instance
     * @param message The message to log
     */
    public void debug(final Logger logger, final String message) {
        if (logger.isDebugEnabled()) {
            logger.debug(message);
        }
    }

    /**
     * Logs a warning message with associated parameters if the logger is enabled for warn level.
     *
     * @param logger  The SLF4J logger instance
     * @param message The message to log
     * @param params  The parameters to log with the message
     */
    public void warn(final Logger logger, final String message, final Object... params) {
        if (logger.isWarnEnabled()) {
            logger.warn(message, params);
        }
    }

    /**
     * Logs an error message if the logger is enabled for error level.
     *
     * @param logger  The SLF4J logger instance
     * @param message The message to log
     */
    public void error(final Logger logger, final String message) {
        if (logger.isErrorEnabled()) {
            logger.error(message);
        }
    }

    /**
     * Logs an error message with an associated exception if the logger is enabled for error level.
     *
     * @param logger  The SLF4J logger instance
     * @param message The message to log
     * @param e       The exception to log with the message
     */
    public void logErrorWithException(final Logger logger, final String message, final Exception e) {
        if (logger.isErrorEnabled()) {
            logger.error(message, e);
        }
    }

    /**
     * Logs a standard problem detail message with an associated exception if the logger is enabled for error level.
     *
     * @param logger        The SLF4J logger instance
     * @param problemDetail The problem detail to log
     * @param e            The exception to log with the message
     */
    public void logStandardProblemDetail(final Logger logger, final ProblemDetail problemDetail, final Exception e) {
        if (logger.isErrorEnabled()) {
            final var message = createStandardProblemDetailMessage(problemDetail);
            logger.error(message, e);
        }
    }

    /**
     * Logs an error message with an HTTP status code if the logger is enabled for error level.
     *
     * @param logger    The SLF4J logger instance
     * @param message   The message to log
     * @param errorCode The HTTP error code to log
     */
    public void logHttpStatusCodeError(final Logger logger, final String message, final Integer errorCode) {
        if (logger.isErrorEnabled()) {
            logger.error(createBasicErrorResponseMessage(errorCode, message));
        }
    }

    /**
     * Creates a standard problem detail message from the given ProblemDetail.
     *
     * @param standardProblemDetail The ProblemDetail object to convert to a string
     * @return The string representation of the ProblemDetail
     */
    private String createStandardProblemDetailMessage(final ProblemDetail standardProblemDetail) {
        return standardProblemDetail.toString();
    }

    /**
     * Creates a basic error response message using the provided error code and message.
     *
     * @param errorCode The error code to include in the message
     * @param message   The message to include in the error response
     * @return A formatted string containing the error code and message
     */
    private String createBasicErrorResponseMessage(final Integer errorCode, final String message) {
        return String.format("%d - %s", errorCode, message);
    }
}
