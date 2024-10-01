package com.audition.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration for the application.
 * This class configures the security settings using Spring Security.
 */
@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    @Value("${spring.security.user.name}")
    private transient String username; // User's username from application properties

    @Value("${spring.security.user.password}")
    private transient String password; // User's password from application properties

    @Value("${spring.security.user.roles}")
    private transient String roles; // User roles from application properties

    private final transient RestAuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public WebSecurityConfig(final RestAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    /**
     * Configures the security filter chain for the application.
     *
     * @param http the HttpSecurity object to configure
     * @return a SecurityFilterChain configured with the specified settings
     * @throws Exception if an error occurs during configuration
     */
    @SuppressWarnings("PMD")
    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .requestMatchers("/public/**").permitAll() // Protect the health endpoint
                //.requestMatchers("/management/**").hasRole("ADMIN") // Protect all management endpoints
                .anyRequest().authenticated() // Allow all other requests
                .and()
                .httpBasic() // Enable basic authentication
                .authenticationEntryPoint(authenticationEntryPoint); // Set custom authentication entry point

        return http.build();
    }

    /**
     * Configures global security settings for the application.
     *
     * @param auth the AuthenticationManagerBuilder to configure
     * @param passwordEncoder the PasswordEncoder to use for encoding passwords
     * @throws Exception if an error occurs during configuration
     */
    @SuppressWarnings("PMD")
    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth, final PasswordEncoder passwordEncoder) throws Exception {
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder) // Use the provided PasswordEncoder
                .withUser(username) // Set the username
                .password(passwordEncoder.encode(password)) // Encode the password
                .roles(roles); // Set the user roles
    }
}
