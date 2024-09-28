package com.audition.configuration;

import com.audition.common.exception.ClientException;
import com.audition.common.exception.IntegrationException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Component
public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(final ClientHttpResponse response)
            throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final ClientHttpResponse response)
            throws IOException {
        if (response.getStatusCode().is5xxServerError()) {
            throw new IntegrationException(response.getStatusText(), response.getStatusCode().value());
        } else if (response.getStatusCode().is4xxClientError()) {
            throw new ClientException(response.getStatusText(), response.getStatusCode().value());
        }
    }
}