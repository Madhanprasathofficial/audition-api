package com.audition.integration;

import com.audition.BaseTest;
import com.audition.model.AuditionComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class AuditionIntegrationCommentsClientTest extends BaseTest {

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
        int postId = randomInt();
        int page = randomInt();
        int size = randomInt();

        when(urlService.getCommentsUrl(postId, page, size)).thenReturn(BASE_URL);

        // When
        List<AuditionComment> list = client.getComments(postId, page, size);

        // Then
        assertEquals(EXPECTED_COMMENTS_SIZE, list.size());
    }

    /**
     * Tests the retrieval of a specific comment by its ID.
     */
    @Test
    void shouldGetComment() {
        // Given
        int id = 1;
        String email = "Eliseo@gardner.biz";
        String name = "id labore ex et quam laborum";
        String url = BASE_URL + "/" + id;

        when(urlService.getCommentUrl(id)).thenReturn(url);

        // When
        AuditionComment auditCommentResponse = client.getComment(id);

        // Then
        assertEquals(id, auditCommentResponse.getId());
        assertEquals(name, auditCommentResponse.getName());
        assertEquals(email, auditCommentResponse.getEmail());
    }
}
