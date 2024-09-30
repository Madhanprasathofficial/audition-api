package com.audition.common.logging;

import com.audition.BaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class AuditionLoggerTest extends BaseTest {

    private transient AuditionLogger auditionLogger;
    @SuppressWarnings("PMD")
    private transient Logger mockLogger;

    @BeforeEach
    void init() {
        mockLogger = mock(Logger.class);
        auditionLogger = new AuditionLogger();
    }

    @Test
    void shouldNotLogInfo() {
        //given
        when(mockLogger.isInfoEnabled()).thenReturn(false);

        //when
        auditionLogger.info(mockLogger, "some");

        //then
        verify(mockLogger, times(0)).info(any());
    }

    @Test
    void shouldLogInfo() {
        //given
        when(mockLogger.isInfoEnabled()).thenReturn(true);

        //when
        auditionLogger.info(mockLogger, "some");

        //then
        verify(mockLogger, times(1)).info(any());
    }
    
    @Test
    void shouldLogStandardProblemDetail() {
        //given
        final var problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setDetail("forbidden");
        problemDetail.setInstance(URI.create("http://instance#"));
        problemDetail.setType(URI.create("http://type#"));
        final var exception = new Exception(randomString());
        final var messageCaptor = ArgumentCaptor.forClass(String.class);
        final var throwableCaptor = ArgumentCaptor.forClass(Throwable.class);

        when(mockLogger.isErrorEnabled()).thenReturn(true);

        //when
        auditionLogger.logStandardProblemDetail(mockLogger, problemDetail, exception);

        //then
        verify(mockLogger, times(1)).error(messageCaptor.capture(), throwableCaptor.capture());
        assertThat(messageCaptor.getValue()).isEqualTo("ProblemDetail[type='http://type#', title='Forbidden', status=403, detail='forbidden', instance='http://instance#', properties='null']");
        assertThat(throwableCaptor.getValue().getMessage()).isEqualTo(exception.getMessage());
    }
}