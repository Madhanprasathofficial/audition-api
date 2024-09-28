package com.audition.service;

import com.audition.integration.IAuditionIntegrationCommentsClient;
import com.audition.model.AuditionComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuditionCommentsServiceTest {

    private transient AuditionCommentsServiceImpl auditionCommentsService;
    private transient IAuditionIntegrationCommentsClient auditionIntegrationCommentsClient;

    @BeforeEach
    void init() {
        auditionIntegrationCommentsClient = mock(IAuditionIntegrationCommentsClient.class);
        auditionCommentsService = new AuditionCommentsServiceImpl(auditionIntegrationCommentsClient);
    }

    @Test
    void shouldGetListOfCommentsByPostId() {
        // given
        final var postIdCaptor = ArgumentCaptor.forClass(Integer.class);
        final var pageCaptor = ArgumentCaptor.forClass(Integer.class);
        final var sizeCaptor = ArgumentCaptor.forClass(Integer.class);
        final var postId = 1;  // example postId
        final var page = 0;
        final var size = 10;

        // when
        auditionCommentsService.getComments(postId, page, size);

        // then
        verify(auditionIntegrationCommentsClient).getComments(postIdCaptor.capture(), pageCaptor.capture(), sizeCaptor.capture());
        assertEquals(postId, postIdCaptor.getValue());
        assertEquals(page, pageCaptor.getValue());
        assertEquals(size, sizeCaptor.getValue());
    }

    @Test
    void shouldGetCommentById() {
        // given
        final var commentIdCaptor = ArgumentCaptor.forClass(Integer.class);
        final var commentId = 1;  // example commentId

        // when
        auditionCommentsService.getComment(commentId);

        // then
        verify(auditionIntegrationCommentsClient).getComment(commentIdCaptor.capture());
        assertEquals(commentId, commentIdCaptor.getValue());
    }

    @Test
    void shouldReturnEmptyListWhenNoCommentsExist() {
        // given
        final var postId = 1;
        final var page = 0;
        final var size = 10;
        when(auditionIntegrationCommentsClient.getComments(postId, page, size)).thenReturn(Collections.emptyList());

        // when
        List<AuditionComment> comments = auditionCommentsService.getComments(postId, page, size);

        // then
        assertEquals(Collections.emptyList(), comments);
    }
}
