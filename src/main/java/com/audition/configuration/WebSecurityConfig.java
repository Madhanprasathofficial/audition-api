package com.audition.configuration;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Slf4j
public class WebSecurityConfig {

    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(final HttpSecurity http) {
        http.cors().disable()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/management/health").hasRole("ADMIN") // Protect the health endpoint
                .requestMatchers("/management/**").hasRole("ADMIN") // Protect all management endpoints
                .anyRequest().permitAll()
                .and()
                .httpBasic(); // Enable basic authentication
        return http.build();
    }

}
