package com.audition.service;

import com.audition.BaseTest;
import com.audition.common.exception.NoDataFoundException;
import com.audition.integration.IIntegrationUrlService;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuditionPostServiceImplTest extends BaseTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/posts/";
    private static final int DEFAULT_USER_ID = 10;
    private static final int DEFAULT_COMMENT_ID = 99;

    private transient AuditionPostServiceImpl client;
    private transient IIntegrationUrlService urlService;
    private transient IAuditionCommentsService commentsClient;

    @BeforeEach
    void init() {
        urlService = mock(IIntegrationUrlService.class);
        commentsClient = mock(IAuditionCommentsService.class);
        client = new AuditionPostServiceImpl(commentsClient, urlService);
    }

    @Test
    void shouldGetPostsWhenResponseIs200() {
        final int userId = DEFAULT_USER_ID;
        final int page = randomInt();
        final int size = randomInt();

        when(urlService.getPostsUrl(userId, page, size)).thenReturn(BASE_URL);

        final List<AuditionPost> list = client.getPosts(userId, page, size);
        assertEquals(100, list.size());
    }

    @Test
    void shouldThrowExceptionWhenRestClientExceptionOccurs() {
        final int userId = DEFAULT_USER_ID;
        final int page = randomInt();
        final int size = randomInt();

        when(urlService.getPostsUrl(userId, page, size)).thenThrow(new RestClientException("Network Error"));

        assertThrows(RestClientException.class, () -> client.getPosts(userId, page, size));
    }

    @Test
    void shouldGetPostByIdWhenResponseIs200() {
        final int id = DEFAULT_COMMENT_ID;
        final String url = BASE_URL + id;

        when(urlService.getPostByIdUrl(id)).thenReturn(url);

        final AuditionPost post = client.getPostById(id, false, null, null);
        assertEquals(id, post.getId());
        assertEquals(DEFAULT_USER_ID, post.getUserId());
    }

    @Test
    void shouldThrowNoDataFoundExceptionWhenPostIdIsInvalid() {
        final int id = -1;
        when(urlService.getPostByIdUrl(id)).thenReturn(BASE_URL + id);

        assertThrows(NoDataFoundException.class, () -> client.getPostById(id, false, null, null));
    }

    @Test
    void shouldGetPostByIdLoadCommentsWhenResponseIs200() {
        final int id = DEFAULT_COMMENT_ID;
        final String url = BASE_URL + id;
        final List<AuditionComment> comments = createSampleComments(id);

        when(urlService.getPostByIdUrl(id)).thenReturn(url);
        when(commentsClient.getComments(id, null, null)).thenReturn(comments);

        final AuditionPost post = client.getPostById(id, true, null, null);
        assertEquals(id, post.getId());
        assertEquals(DEFAULT_USER_ID, post.getUserId());
        assertEquals(4, post.getAuditionComments().size());
    }

    @Test
    void shouldThrowExceptionForServerError() {
        final int id = DEFAULT_COMMENT_ID;

        when(urlService.getPostByIdUrl(id)).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(HttpClientErrorException.class, () -> client.getPostById(id, false, null, null));
    }

    private List<AuditionComment> createSampleComments(final int postId) {
        final List<AuditionComment> comments = new ArrayList<>();
        comments.add(new AuditionComment(1, postId, "John Doe", "john@example.com", "This is a great post!"));
        comments.add(new AuditionComment(2, postId, "Jane Smith", "jane@example.com", "I found this very helpful."));
        comments.add(new AuditionComment(3, postId, "Mike Johnson", "mike@example.com", "Interesting perspective!"));
        comments.add(new AuditionComment(4, postId, "Emily Davis", "emily@example.com", "I disagree with this."));
        return comments;
    }
}
