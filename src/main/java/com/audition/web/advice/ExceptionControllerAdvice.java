package com.audition.web.advice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

import com.audition.common.exception.SystemException;
import com.audition.common.logging.AuditionLogger;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global exception handler for the application that intercepts exceptions thrown by
 * controllers and returns appropriate HTTP response details.
 */
@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {

    public static final String DEFAULT_TITLE = "API Error Occurred";
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionControllerAdvice.class);
    private static final String ERROR_MESSAGE = "Error Code from Exception could not be mapped to a valid HttpStatus Code - ";
    private static final String DEFAULT_MESSAGE = "API Error occurred. Please contact support or administrator.";

    private final transient AuditionLogger logger;

    /**
     * Constructs a new ExceptionControllerAdvice with the specified AuditionLogger.
     *
     * @param logger the AuditionLogger to be used for logging exceptions
     */
    public ExceptionControllerAdvice(final AuditionLogger logger) {
        super();
        this.logger = logger;
    }

    /**
     * Handles HttpClientErrorException and returns a ProblemDetail with the appropriate status code.
     *
     * @param e the HttpClientErrorException to handle
     * @return a ProblemDetail containing error information
     */
    @ExceptionHandler(HttpClientErrorException.class)
    ProblemDetail handleHttpClientException(final HttpClientErrorException e) {
        return createProblemDetail(e, e.getStatusCode());
    }

    /**
     * Handles general exceptions and ConstraintViolationException, returning a ProblemDetail
     * with the appropriate status code.
     *
     * @param e the Exception to handle
     * @return a ProblemDetail containing error information
     */
    @ExceptionHandler({Exception.class, ConstraintViolationException.class})
    ProblemDetail handleMainException(final Exception e) {
        final HttpStatusCode status = getHttpStatusCodeFromException(e);
        return createProblemDetail(e, status);
    }

    /**
     * Handles SystemException and returns a ProblemDetail with the appropriate status code.
     *
     * @param e the SystemException to handle
     * @return a ProblemDetail containing error information
     */
    @ExceptionHandler(SystemException.class)
    ProblemDetail handleSystemException(final SystemException e) {
        final HttpStatusCode status = getHttpStatusCodeFromSystemException(e);
        return createProblemDetail(e, status);
    }

    /**
     * Creates a ProblemDetail instance based on the given exception and status code.
     *
     * @param exception the exception for which to create the ProblemDetail
     * @param statusCode the HTTP status code to set in the ProblemDetail
     * @return a ProblemDetail populated with error information
     */
    private ProblemDetail createProblemDetail(final Exception exception, final HttpStatusCode statusCode) {
        final ProblemDetail problemDetail = ProblemDetail.forStatus(statusCode);
        problemDetail.setDetail(getMessageFromException(exception));

        if (exception instanceof SystemException) {
            problemDetail.setTitle(((SystemException) exception).getTitle());
        } else if (exception instanceof ConstraintViolationException) {
            problemDetail.setStatus(HttpStatus.BAD_REQUEST);
            problemDetail.setDetail(exception.getMessage());
            problemDetail.setTitle(HttpStatus.BAD_REQUEST.name());
        } else if (exception instanceof RuntimeException) {
            problemDetail.setTitle("Internal Server Error");
            problemDetail.setDetail("An unexpected error occurred");
            problemDetail.setStatus(INTERNAL_SERVER_ERROR);
        } else {
            problemDetail.setTitle(DEFAULT_TITLE);
        }
        return problemDetail;
    }

    /**
     * Extracts the error message from the given exception.
     *
     * @param exception the exception from which to extract the message
     * @return the error message, or a default message if none exists
     */
    private String getMessageFromException(final Exception exception) {
        if (StringUtils.isNotBlank(exception.getMessage())) {
            return exception.getMessage();
        }
        return DEFAULT_MESSAGE;
    }

    /**
     * Retrieves the appropriate HTTP status code from the given SystemException.
     *
     * @param exception the SystemException from which to get the status code
     * @return the corresponding HttpStatusCode
     */
    private HttpStatusCode getHttpStatusCodeFromSystemException(final SystemException exception) {
        try {
            return HttpStatusCode.valueOf(exception.getStatusCode());
        } catch (final IllegalArgumentException iae) {
            logger.info(LOG, ERROR_MESSAGE + exception.getStatusCode());
            return INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Determines the appropriate HTTP status code from the given exception.
     *
     * @param exception the Exception from which to determine the status code
     * @return the corresponding HttpStatusCode
     */
    private HttpStatusCode getHttpStatusCodeFromException(final Exception exception) {
        if (exception instanceof HttpClientErrorException) {
            return ((HttpClientErrorException) exception).getStatusCode();
        } else if (exception instanceof HttpRequestMethodNotSupportedException) {
            return METHOD_NOT_ALLOWED;
        } else if (exception instanceof ConstraintViolationException) {
            return HttpStatus.BAD_REQUEST;
        }
        return INTERNAL_SERVER_ERROR;
    }
}
