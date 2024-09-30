package com.audition.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter that injects OpenTelemetry trace and span IDs into the response headers.
 * This filter is executed once per request to ensure the headers are set appropriately.
 */
@Component
public class ResponseHeaderInjector extends OncePerRequestFilter {

    private static final String TRACE_ID = "X-OpenTelemetry-TraceId";
    private static final String SPAN_ID = "X-OpenTelemetry-SpanId";


    /**
     * This method is invoked for each request to set the OpenTelemetry headers.
     *
     * @param request  the current HTTP request
     * @param response the current HTTP response
     * @param chain    the filter chain to execute
     * @throws IOException      if an input or output exception occurs
     * @throws ServletException if the request processing fails
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
                                    final FilterChain chain) throws IOException, ServletException {
        // Set default headers with placeholder values
        response.setHeader(TRACE_ID, "-");
        response.setHeader(SPAN_ID, "-");

        // Continue with the rest of the filter chain
        chain.doFilter(request, response);
    }
}
