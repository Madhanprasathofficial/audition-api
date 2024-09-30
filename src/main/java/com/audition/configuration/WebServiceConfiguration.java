package com.audition.configuration;

import com.audition.common.logging.RestTemplateRequestResponseLoggingInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Locale;

/**
 * Configuration for web services.
 * This class configures the RestTemplate and ObjectMapper used in the application.
 */
@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    private final transient RestTemplateRequestResponseLoggingInterceptor loggingInterceptor;
    private final transient RestTemplateResponseErrorHandler responseErrorHandler;

    /**
     * Constructor for WebServiceConfiguration.
     *
     * @param loggingInterceptor    the logging interceptor for RestTemplate
     * @param responseErrorHandler   the error handler for RestTemplate responses
     */
    public WebServiceConfiguration(final RestTemplateRequestResponseLoggingInterceptor loggingInterceptor,
                                   final RestTemplateResponseErrorHandler responseErrorHandler) {
        this.loggingInterceptor = loggingInterceptor;
        this.responseErrorHandler = responseErrorHandler;
    }

    /**
     * Configures the ObjectMapper for JSON serialization and deserialization.
     *
     * @return a configured ObjectMapper instance
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.getDefault()))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    /**
     * Configures the RestTemplate with custom message converters, interceptors, and error handlers.
     *
     * @return a configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(createClientFactory()));

        final MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setPrettyPrint(true);
        messageConverter.setObjectMapper(objectMapper());
        messageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Replace default Jackson message converter with the custom one
        restTemplate.getMessageConverters().removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        restTemplate.getMessageConverters().add(messageConverter);

        // Set interceptors and error handler
        restTemplate.setInterceptors(Collections.singletonList(loggingInterceptor));
        restTemplate.setErrorHandler(responseErrorHandler);

        return restTemplate;
    }

    /**
     * Creates a SimpleClientHttpRequestFactory with streaming disabled.
     *
     * @return a configured SimpleClientHttpRequestFactory instance
     */
    private SimpleClientHttpRequestFactory createClientFactory() {
        final SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }
}
