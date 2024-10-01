package com.audition.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for password encoding.
 * This class defines a bean for encoding passwords using BCrypt.
 */
@Configuration
public class PasswordEncoderConfig {

    /**
     * Provides a PasswordEncoder bean using BCrypt for password hashing.
     *
     * @return a PasswordEncoder instance for encoding passwords
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Return a new BCryptPasswordEncoder instance
    }
}
