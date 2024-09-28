package com.audition.integration;

import com.audition.BaseTest;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AuditionIntegrationPostsClientTest extends BaseTest {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com/posts/";
    private static final int DEFAULT_USER_ID = 10; // Default user ID for assertions
    private static final int DEFAULT_COMMENT_ID = 99; // Default comment ID for tests

    private AuditionIntegrationPostsClient client;
    private IIntegrationUrlService urlService;
    private IAuditionIntegrationCommentsClient commentsClient;

    @BeforeEach
    void init() {
        urlService = mock(IIntegrationUrlService.class);
        commentsClient = mock(IAuditionIntegrationCommentsClient.class);
        client = new AuditionIntegrationPostsClient(commentsClient, urlService);
    }

    // ### getPosts
    @Test
    void shouldGetPostsWhenResponseIs200() {
        // given
        int userId = DEFAULT_USER_ID;
        int page = randomInt();
        int size = randomInt();

         //when
        when(urlService.getPostsUrl(userId,page,size)).thenReturn(BASE_URL);


        List<AuditionPost> list = client.getPosts(userId, page, size);

        // then
        assertEquals(100, list.size());
    }

    @Test
    void shouldGetPostByIdWhenResponseIs200() {
        // given
        int id = DEFAULT_COMMENT_ID;
        String url = BASE_URL + id;

        when(urlService.getPostByIdUrl(id)).thenReturn(url);

        // when
        AuditionPost post = client.getPostById(id, false, null, null);

        // then
        assertEquals(id, post.getId());
        assertEquals(DEFAULT_USER_ID, post.getUserId());
    }

    @Test
    void shouldGetPostByIdLoadCommentsWhenResponseIs200() {
        // given
        int id = DEFAULT_COMMENT_ID;
        String url = BASE_URL + id;

        List<AuditionComment> comments = createSampleComments(id);

        when(urlService.getPostByIdUrl(id)).thenReturn(url);
        when(commentsClient.getComments(id, null, null)).thenReturn(comments);

        // when
        AuditionPost post = client.getPostById(id, true, null, null);

        // then
        assertEquals(id, post.getId());
        assertEquals(DEFAULT_USER_ID, post.getUserId());
        assertEquals(4, post.getAuditionComments().size());
    }

    private List<AuditionComment> createSampleComments(int postId) {
        List<AuditionComment> comments = new ArrayList<>();
        comments.add(new AuditionComment(1, postId, "John Doe", "john@example.com", "This is a great post!"));
        comments.add(new AuditionComment(2, postId, "Jane Smith", "jane@example.com", "I found this very helpful."));
        comments.add(new AuditionComment(3, postId, "Mike Johnson", "mike@example.com", "Interesting perspective!"));
        comments.add(new AuditionComment(4, postId, "Emily Davis", "emily@example.com", "I disagree with this."));
        return comments;
    }
}
