package com.audition.configuration;

import com.audition.common.logging.RestTemplateRequestResponseLoggingInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.util.Collections;


@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private final transient RestTemplateRequestResponseLoggingInterceptor loggingInterceptor;

    private final transient RestTemplateResponseErrorHandler responseErrorHandler;

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    public WebServiceConfiguration(final RestTemplateRequestResponseLoggingInterceptor loggingInterceptor,
                                   final RestTemplateResponseErrorHandler responseErrorHandler) {
        this.loggingInterceptor = loggingInterceptor;
        this.responseErrorHandler = responseErrorHandler;
    }

    @Bean
    public ObjectMapper objectMapper() {
        // TODO configure Jackson Object mapper that
        //  1. allows for date format as yyyy-MM-dd
        //  2. Does not fail on unknown properties
        //  3. maps to camelCase
        //  4. Does not include null values or empty values
        //  5. does not write datas as timestamps.
        return new ObjectMapper();
    }

    @Bean
    public RestTemplate restTemplate() {
        // Create the RestTemplate with a BufferingClientHttpRequestFactory for enhanced request logging
        RestTemplate restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(createClientFactory()));

        // Configure a custom MappingJackson2HttpMessageConverter
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setPrettyPrint(true);
        messageConverter.setObjectMapper(objectMapper());
        messageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Replace the existing Jackson message converter with the custom one
        restTemplate.getMessageConverters().removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        restTemplate.getMessageConverters().add(messageConverter);

        // Set logging interceptor and error handler
        restTemplate.setInterceptors(Collections.singletonList(loggingInterceptor));
        restTemplate.setErrorHandler(responseErrorHandler);

        return restTemplate;
    }


    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }
}
