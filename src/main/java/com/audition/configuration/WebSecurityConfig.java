package com.audition.configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * This class configures the security settings using Spring Security.
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    /**
     * Configures the security filter chain for the application.
     *
     * @param http the HttpSecurity object to configure
     * @return a SecurityFilterChain configured with the specified settings
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        http.cors().disable()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/management/health").hasRole("ADMIN") // Protect the health endpoint
                .requestMatchers("/management/**").hasRole("ADMIN") // Protect all management endpoints
                .anyRequest().permitAll() // Allow all other requests
                .and()
                .httpBasic(); // Enable basic authentication

        return http.build();
    }
}
