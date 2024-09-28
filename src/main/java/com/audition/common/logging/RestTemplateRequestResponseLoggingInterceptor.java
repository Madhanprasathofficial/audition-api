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

@Component
@Slf4j
public class RestTemplateRequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final transient boolean logRequest;
    private final transient boolean logResponse;

    private final transient AuditionLogger logger;

    public RestTemplateRequestResponseLoggingInterceptor(
            @Value("${application.config.interceptor.logRequest:true}") final boolean logRequest,
            @Value("${application.config.interceptor.logResponse:true}") final boolean logResponse,
            final AuditionLogger logger) {
        this.logRequest = logRequest;
        this.logResponse = logResponse;
        this.logger = logger;
    }

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
                                        final ClientHttpRequestExecution execution) throws IOException {

        log(request, body);
        final var response = execution.execute(request, body);
        log(response);
        return response;
    }

    private void log(final HttpRequest request, final byte[] body) {
        if (logRequest) {
            final var message = "\n***** HttpRequest start ***** "
                    + "\nURI: " + request.getURI()
                    + "\nMethod: " + request.getMethod()
                    + "\nHeaders: " + request.getHeaders()
                    + "\nRequest body: " + new String(body, StandardCharsets.UTF_8)
                    + "\n***** HttpRequest end *****";
            logger.info(log, message);
        }
    }

    private void log(final ClientHttpResponse response) throws IOException {
        if (logResponse) {
            final var message = "\n***** ClientHttpResponse start ***** "
                    + "\nStatus code: " + response.getStatusCode()
                    + "\nStatus text: " + response.getStatusText()
                    + "\nHeaders: " + response.getHeaders()
                    + "\nResponse body: " + StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8)
                    + "\n***** ClientHttpResponse end *****";
            logger.info(log, message);
        }
    }
}