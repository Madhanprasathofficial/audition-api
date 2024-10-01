package com.audition.service;

import com.audition.BaseTest;
import com.audition.integration.IIntegrationUrlService;
import com.audition.model.AuditionComment;
import com.audition.common.exception.NoDataFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuditionCommentsServiceImplTest extends BaseTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/comments";
    private transient AuditionCommentsServiceImpl client;
    private transient IIntegrationUrlService urlService;

    @BeforeEach
    void init() {
        urlService = mock(IIntegrationUrlService.class);
        client = new AuditionCommentsServiceImpl(urlService);
    }

    @Test
    void shouldGetComments() {
        // Given
        final int postId = randomInt();
        final int page = randomInt();
        final int size = randomInt();

        when(urlService.getCommentsUrl(postId, page, size)).thenReturn(BASE_URL);

        // When
        final List<AuditionComment> list = client.getComments(postId, page, size);

        // Then
        assertEquals(500, list.size());
    }

    @Test
    void shouldGetComment() {
        // Given
        final int id = 1;
        final String email = "Eliseo@gardner.biz";
        final String name = "id labore ex et quam laborum";
        final String url = BASE_URL + "/" + id;

        when(urlService.getCommentUrl(id)).thenReturn(url);

        // When
        final AuditionComment auditCommentResponse = client.getComment(id);

        // Then
        assertEquals(id, auditCommentResponse.getId());
        assertEquals(name, auditCommentResponse.getName());
        assertEquals(email, auditCommentResponse.getEmail());
    }

    @Test
    void shouldThrowNoDataFoundExceptionWhenNoCommentsFound() {
        // Given
        final int postId = 1999;
        final int page = randomInt();
        final int size = randomInt();
        final String url = BASE_URL + "/" + postId;
        when(urlService.getCommentsUrl(postId, page, size)).thenReturn(url);

        // When & Then
        final NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () -> {
            client.getComments(postId, page, size);
        });
        assertEquals("No comments found for post ID: " + postId, thrown.getMessage());
    }

    @Test
    void shouldThrowNoDataFoundExceptionWhenCommentNotFound() {
        // Given
        final int commentId = 9999;
        final String url = BASE_URL + "/" + commentId;

        when(urlService.getCommentUrl(commentId)).thenReturn(url);

        // When & Then
        final NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () -> {
            client.getComment(commentId);
        });
        assertEquals("No data found for comment ID: " + commentId, thrown.getMessage());
    }


    @Test
    void shouldUseDefaultPaginationWhenNullValuesProvided() {
        // Given
        final int postId = randomInt();
        when(urlService.getCommentsUrl(postId, null, null)).thenReturn(BASE_URL);

        // When
        final List<AuditionComment> list = client.getComments(postId, null, null);

        // Then
        assertEquals(500, list.size());
    }

    @Test
    void shouldThrowIllegalArgumentExceptionForInvalidParameters() {
        // Given
        final int invalidPostId = -1; // Invalid postId

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            client.getComments(invalidPostId, null, null);
        });
    }
}
