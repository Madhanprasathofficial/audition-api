package com.audition.configuration;

import com.audition.BaseTest;
import com.audition.common.logging.RestTemplateRequestResponseLoggingInterceptor;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class WebServiceConfigurationTest extends BaseTest {

    private transient WebServiceConfiguration configuration;
    private transient RestTemplateResponseErrorHandler errorHandler;
    private transient RestTemplateRequestResponseLoggingInterceptor interceptor;

    @BeforeEach
    void init() {
        interceptor = mock(RestTemplateRequestResponseLoggingInterceptor.class);
        errorHandler = mock(RestTemplateResponseErrorHandler.class);
        configuration = new WebServiceConfiguration(interceptor, errorHandler);
    }

    @Test
    void shouldCreateObjectMapper() {
        // given
        final var testDate = testDate();

        //when
        final var objectMapper = configuration.objectMapper();

        // then
        assertTrue(objectMapper.getDateFormat() instanceof SimpleDateFormat);

        assertEquals("2023-07-01", objectMapper.getDateFormat().format(testDate));
        assertEquals(PropertyNamingStrategies.LOWER_CAMEL_CASE, objectMapper.getPropertyNamingStrategy());
        assertEquals(JsonInclude.Include.NON_EMPTY, objectMapper.getSerializationConfig().getDefaultPropertyInclusion().getValueInclusion());
        assertFalse(objectMapper.getSerializationConfig().hasSerializationFeatures(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS.getMask()));
        assertFalse(objectMapper.getDeserializationConfig().hasDeserializationFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES.getMask()));
        assertFalse(objectMapper.getDeserializationConfig().hasDeserializationFeatures(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES.getMask()));
    }

    @Test
    void itShouldCreateRestTemplate() {
        // when
        final var restTemplate = configuration.restTemplate();

        // then
        assertTrue(restTemplate.getMessageConverters().size() > 0);
        assertEquals(errorHandler, restTemplate.getErrorHandler());
        assertEquals(interceptor, restTemplate.getInterceptors().get(0));
    }

    private Date testDate() {
        final var calendar = LocalDate.of(2023, 7, 1);
        return Date.from(calendar.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}