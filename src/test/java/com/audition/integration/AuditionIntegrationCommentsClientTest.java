package com.audition.integration;

import com.audition.BaseTest;
import com.audition.model.AuditionComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


 class AuditionIntegrationCommentsClientTest extends BaseTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/comments";
    private static final int EXPECTED_COMMENTS_SIZE = 500;
    private transient AuditionIntegrationCommentsClient client;
    private transient IIntegrationUrlService urlService;

    /**
     * Sets up the test environment by initializing mocks before each test.
     */
    @BeforeEach
    void init() {
        urlService = mock(IIntegrationUrlService.class);
        client = new AuditionIntegrationCommentsClient(urlService);
    }

    /**
     * Tests the retrieval of comments for a given post.
     */
    @Test
    void shouldGetCommentsGetComments() {
        // Given
        final int postId = randomInt();
        final int page = randomInt();
        final int size = randomInt();

        when(urlService.getCommentsUrl(postId, page, size)).thenReturn(BASE_URL);

        // When
        final List<AuditionComment> list = client.getComments(postId, page, size);

        // Then
        assertEquals(EXPECTED_COMMENTS_SIZE, list.size());
    }

    /**
     * Tests the retrieval of a specific comment by its ID.
     */
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
}
