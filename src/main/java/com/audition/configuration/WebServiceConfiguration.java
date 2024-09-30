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

@Configuration
public class WebServiceConfiguration implements WebMvcConfigurer {

    private static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";

    private final transient RestTemplateRequestResponseLoggingInterceptor loggingInterceptor;
    private final transient RestTemplateResponseErrorHandler responseErrorHandler;

    public WebServiceConfiguration(final RestTemplateRequestResponseLoggingInterceptor loggingInterceptor,
                                   final RestTemplateResponseErrorHandler responseErrorHandler) {
        this.loggingInterceptor = loggingInterceptor;
        this.responseErrorHandler = responseErrorHandler;
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setDateFormat(new SimpleDateFormat(YEAR_MONTH_DAY_PATTERN, Locale.getDefault()))
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = new RestTemplate(
                new BufferingClientHttpRequestFactory(createClientFactory()));

        final MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        messageConverter.setPrettyPrint(true);
        messageConverter.setObjectMapper(objectMapper());
        messageConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));

        // Replace default Jackson message converter with custom one
        restTemplate.getMessageConverters().removeIf(converter -> converter instanceof MappingJackson2HttpMessageConverter);
        restTemplate.getMessageConverters().add(messageConverter);

        // Set interceptors and error handler
        restTemplate.setInterceptors(Collections.singletonList(loggingInterceptor));
        restTemplate.setErrorHandler(responseErrorHandler);

        return restTemplate;
    }

    private SimpleClientHttpRequestFactory createClientFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        return requestFactory;
    }
}
