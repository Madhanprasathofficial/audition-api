package com.audition;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.audition")
public class AuditionApplication {

    public static void main(final String[] args) {
        SpringApplication.run(AuditionApplication.class, args);
    }

}
