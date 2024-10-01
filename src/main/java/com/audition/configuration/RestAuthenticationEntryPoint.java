package com.audition.configuration;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom implementation of the AuthenticationEntryPoint interface.
 * This class handles unauthorized access attempts by returning a 401 Unauthorized response.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Called when an AuthenticationException is raised.
     * This method sends a 401 Unauthorized error response to the client.
     *
     * @param request       the HttpServletRequest that resulted in an AuthenticationException
     * @param response      the HttpServletResponse so that the user can be notified of the error
     * @param authException the exception that caused the invocation
     * @throws IOException      if an input or output error is detected when the servlet handles the request
     * @throws ServletException if the request for the GET could not be handled
     */
    @Override
    public void commence(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException authException)
            throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}
