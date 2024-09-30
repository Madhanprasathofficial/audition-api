package com.audition.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ResponseHeaderInjector extends OncePerRequestFilter {

    private static final String TRACE_ID = "X-OpenTelemetry-TraceId";
    private static final String SPAN_ID = "X-OpenTelemetry-SpanId";

    // TODO: Inject actual OpenTelemetry trace and span IDs into the response headers.

    @Override
    protected void doFilterInternal(final HttpServletRequest request,final HttpServletResponse response,
                                    final FilterChain chain) throws IOException, ServletException {
        // Set default headers with placeholder values
        response.setHeader(TRACE_ID, "-");
        response.setHeader(SPAN_ID, "-");

        // Continue with the rest of the filter chain
        chain.doFilter(request, response);
    }
}
