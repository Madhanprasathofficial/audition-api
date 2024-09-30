package com.audition.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Interceptor for logging HTTP requests and responses made through RestTemplate.
 * This class implements ClientHttpRequestInterceptor to log details
 * such as URI, method, headers, request body, and response status.
 */
@Component
@Slf4j
public class RestTemplateRequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final transient boolean logRequest;  // Flag to enable/disable request logging
    private final transient boolean logResponse; // Flag to enable/disable response logging

    private final transient AuditionLogger logger; // Custom logger for logging messages

    /**
     * Constructor for RestTemplateRequestResponseLoggingInterceptor.
     *
     * @param logRequest  Flag to indicate whether to log requests
     * @param logResponse Flag to indicate whether to log responses
     * @param logger      Custom logger for logging purposes
     */
    public RestTemplateRequestResponseLoggingInterceptor(
            @Value("${application.config.interceptor.logRequest}") final boolean logRequest,
            @Value("${application.config.interceptor.logResponse}") final boolean logResponse,
            final AuditionLogger logger) {
        this.logRequest = logRequest;
        this.logResponse = logResponse;
        this.logger = logger;
    }

    /**
     * Intercepts the HTTP request and logs details if enabled.
     *
     * @param request   The HTTP request to be executed
     * @param body      The body of the request
     * @param execution The execution context
     * @return The HTTP response from the executed request
     * @throws IOException If an I/O error occurs during the logging or execution
     */
    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body); // Log request details
        final var response = execution.execute(request, body); // Execute the request
        logResponseDetails(response); // Log response details
        return response; // Return the response
    }

    /**
     * Logs the details of the HTTP request.
     *
     * @param request The HTTP request
     * @param body    The body of the request
     */
    private void logRequestDetails(final HttpRequest request, final byte[] body) {
        if (logRequest) {
            final var message = "\n***** HttpRequest start ***** "
                   + "\nURI: " + request.getURI()
                   + "\nMethod: " + request.getMethod()
                   + "\nHeaders: " + request.getHeaders()
                   + "\nRequest body: " + new String(body, StandardCharsets.UTF_8)
                   + "\n***** HttpRequest end *****";
            logger.info(log, message); // Log request message
        }
    }

    /**
     * Logs the details of the HTTP response.
     *
     * @param response The HTTP response
     * @throws IOException If an I/O error occurs while reading the response body
     */
    private void logResponseDetails(final ClientHttpResponse response) throws IOException {
        if (logResponse) {
            final var message = "\n***** ClientHttpResponse start ***** "
                    + "\nStatus code: " + response.getStatusCode()
                    + "\nStatus text: " + response.getStatusText()
                    + "\nHeaders: " + response.getHeaders()
                    + "\nResponse body: " + StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8)
                    + "\n***** ClientHttpResponse end *****";
            logger.info(log, message); // Log response message
        }
    }
}
