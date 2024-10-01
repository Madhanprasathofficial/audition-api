package com.audition;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
public class BaseIntegrationTest { // Changed from abstract class to regular class

    @Autowired
    protected MockMvc mockMvc;

    @Value("${spring.security.user.name}")
    protected transient String username;

    @Value("${spring.security.user.password}")
    protected transient String password;

    /**
     * Generates the Basic Auth header.
     *
     * @return Basic Auth header string
     */
    protected String getBasicAuthHeader() {
        final String basicAuth = username + ":" + password;
        return "Basic " + java.util.Base64.getEncoder().encodeToString(basicAuth.getBytes());
    }
}
