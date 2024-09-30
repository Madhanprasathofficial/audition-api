package com.audition.configuration;

import com.audition.common.exception.ClientException;
import com.audition.common.exception.IntegrationException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

/**
 * A custom error handler for handling errors encountered during REST template calls.
 * This class implements the ResponseErrorHandler interface to provide specific
 * error handling for client and server errors.
 */
@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    /**
     * Determines if the given response has an error status code.
     *
     * @param response the ClientHttpResponse to check
     * @return true if the response has a 4xx or 5xx status code, false otherwise
     * @throws IOException if an I/O error occurs while accessing the response
     */
    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        // Check for client or server errors
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    /**
     * Handles the error response by throwing the appropriate exception
     * based on the status code of the response.
     *
     * @param response the ClientHttpResponse to handle
     * @throws IOException if an I/O error occurs while accessing the response
     * @throws IntegrationException if the response has a 5xx server error
     * @throws ClientException if the response has a 4xx client error
     */
    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        // Handle 5xx Server Errors
        if (response.getStatusCode().is5xxServerError()) {
            throw new IntegrationException(response.getStatusText(), response.getStatusCode().value());
        }

        // Handle 4xx Client Errors
        if (response.getStatusCode().is4xxClientError()) {
            throw new ClientException(response.getStatusText(), response.getStatusCode().value());
        }
    }
}
