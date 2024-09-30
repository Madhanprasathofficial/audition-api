package com.audition.configuration;

import com.audition.BaseTest;
import com.audition.common.exception.ClientException;
import com.audition.common.exception.IntegrationException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RestTemplateResponseErrorHandlerTest extends BaseTest {
    private transient RestTemplateResponseErrorHandler errorHandler;

    @BeforeEach
    void init() {
        errorHandler = new RestTemplateResponseErrorHandler();
    }

    @Test
    @SneakyThrows
    void shouldHandleErrorIntegrationException() {
        //given
        final var statusText = randomString();
        final var response = mock(ClientHttpResponse.class);
        when(response.getStatusText()).thenReturn(statusText);
        when(response.getStatusCode()).thenReturn(HttpStatus.BAD_GATEWAY);

        //when
        final var exception = assertThrows(IntegrationException.class, () -> {
            errorHandler.handleError(response);
        });

        //then
        assertEquals(statusText, exception.getDetail());
        assertEquals(HttpStatus.BAD_GATEWAY.value(), exception.getStatusCode());
    }

    @Test
    @SneakyThrows
    void shouldHandleErrorClientException() {
        //given
        final var statusText = randomString();
        final var response = mock(ClientHttpResponse.class);
        when(response.getStatusText()).thenReturn(statusText);
        when(response.getStatusCode()).thenReturn(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);

        //when
        final var exception = assertThrows(ClientException.class, () -> {
            errorHandler.handleError(response);
        });

        //then
        assertEquals(statusText, exception.getDetail());
        assertEquals(HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS.value(), exception.getStatusCode());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("itShouldStateIsAnErrorProvider")
    void itShouldStateIsAnError(final HttpStatusCode httpStatusCode, final boolean expected) {
        //given
        final var response = mock(ClientHttpResponse.class);
        when(response.getStatusCode()).thenReturn(httpStatusCode);

        //when
        final var res = errorHandler.hasError(response);

        //then
        assertEquals(expected, res);
    }

    private static Stream<Arguments> itShouldStateIsAnErrorProvider() {
        return Stream.of(
                Arguments.of(HttpStatus.OK, false),
                Arguments.of(HttpStatus.ACCEPTED, false),
                Arguments.of(HttpStatus.NO_CONTENT, false),              //  ... more http 2xx
                // 5xx
                Arguments.of(HttpStatus.INTERNAL_SERVER_ERROR, true),
                Arguments.of(HttpStatus.NOT_IMPLEMENTED, true),
                Arguments.of(HttpStatus.BAD_GATEWAY, true),
                Arguments.of(HttpStatus.SERVICE_UNAVAILABLE, true),
                Arguments.of(HttpStatus.GATEWAY_TIMEOUT, true),         //  ... more http 5xx
                // 4xx
                Arguments.of(HttpStatus.NOT_ACCEPTABLE, true),
                Arguments.of(HttpStatus.REQUEST_TIMEOUT, true),
                Arguments.of(HttpStatus.CONFLICT, true)                 //  ... more http 4xx
        );
    }
}