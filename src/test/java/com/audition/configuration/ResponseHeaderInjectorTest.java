package com.audition.configuration;

import com.audition.BaseTest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


class ResponseHeaderInjectorTest extends BaseTest {

    private transient ResponseHeaderInjector headerInjector;

    public static final String OPEN_TELEMETRY_TRACE_ID = "X-OpenTelemetry-TraceId";
    public static final String OPEN_TELEMETRY_SPAN_ID = "X-OpenTelemetry-SpanId";

    @BeforeEach
    void init() {
        headerInjector = new ResponseHeaderInjector();
    }

    @Test
    @SneakyThrows
    void shouldInjectHeaders() {
        // given
        final var request = mock(HttpServletRequest.class);
        final var response = mock(HttpServletResponse.class);
        final var chain = mock(FilterChain.class);

        // when
        headerInjector.doFilterInternal(request, response, chain);

        // then
        // Use the constants for verification
        verify(response, times(1)).setHeader(OPEN_TELEMETRY_TRACE_ID, "-");
        verify(response, times(1)).setHeader(OPEN_TELEMETRY_SPAN_ID, "-");
        verify(chain, times(1)).doFilter(request, response);
    }
}
