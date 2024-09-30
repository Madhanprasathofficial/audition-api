package com.audition.common.logging;

import com.audition.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class RestTemplateRequestResponseLoggingInterceptorTest extends BaseTest {

    private transient RestTemplateRequestResponseLoggingInterceptor interceptor;
    private transient AuditionLogger logger;
    private transient HttpRequest request;
    private transient byte[] body;
    private transient ClientHttpRequestExecution execution;
    private transient ClientHttpResponse response;

    @BeforeEach
    void init() {
        logger = mock(AuditionLogger.class);
        request = mock(HttpRequest.class);
        response = mock(ClientHttpResponse.class);
        body = "".getBytes(UTF_8);
        execution = mock(ClientHttpRequestExecution.class);
    }

    @Test
    void shouldLogRequest() throws IOException {
        // given
        final var messageCaptor = ArgumentCaptor.forClass(String.class);
        interceptor = new RestTemplateRequestResponseLoggingInterceptor(true, false, logger);

        // when
        interceptor.intercept(request, body, execution);

        // then
        verify(logger, times(1)).info(any(), messageCaptor.capture());
        verify(execution, times(1)).execute(any(), any());
        assertTrue(messageCaptor.getValue().contains("HttpRequest"));
    }

    @Test
    void shouldLogResponse() throws IOException {
        // given
        final var messageCaptor = ArgumentCaptor.forClass(String.class);
        when(execution.execute(any(), any())).thenReturn(response);
        interceptor = new RestTemplateRequestResponseLoggingInterceptor(false, true, logger);

        // when
        interceptor.intercept(request, body, execution);

        // then
        verify(logger, times(1)).info(any(), messageCaptor.capture());
        assertTrue(messageCaptor.getValue().contains("ClientHttpResponse"));
    }

    @Test
    void shouldLogRequestAndResponse() throws IOException {
        // given
        when(execution.execute(any(), any())).thenReturn(response);
        interceptor = new RestTemplateRequestResponseLoggingInterceptor(true, true, logger);
        final var messageCaptor = ArgumentCaptor.forClass(String.class);
        // when
        interceptor.intercept(request, body, execution);

        // then
        verify(logger, times(2)).info(any(), messageCaptor.capture());
        assertTrue(messageCaptor.getAllValues().get(0).contains("HttpRequest"));
        assertTrue(messageCaptor.getAllValues().get(1).contains("ClientHttpResponse"));
    }

}