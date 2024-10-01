package com.audition.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test") //
class IntegrationUrlServiceTest {

    @Value("${application.config.baseUrl}")
    private transient String baseUrl;

    @Autowired
    private final transient IntegrationUrlService urlService;

    // Constants
    private static final int DEFAULT_USER_ID = 1;
    private static final int DEFAULT_POST_ID = 1;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 10;
    private static final String POSTS_PATH = "/posts";
    private static final String COMMENTS_PATH = "/comments";
    private static final String QUERY_START_PARAM = "?&_start=";
    private static final String QUERY_LIMIT_PARAM = "&_limit=";
    private static final String USER_ID_PARAM = "&userId=";

    // Constructor to initialize final fields
    @Autowired
    public IntegrationUrlServiceTest(final IntegrationUrlService urlService) {
        this.urlService = urlService;
    }

    @Test
    void testGetPostsUrlWithUserIdAndPagination() {
        final String expectedUrl = baseUrl + POSTS_PATH + QUERY_START_PARAM + DEFAULT_PAGE + QUERY_LIMIT_PARAM + DEFAULT_SIZE + USER_ID_PARAM + DEFAULT_USER_ID;

        final String actualUrl = urlService.getPostsUrl(DEFAULT_USER_ID, DEFAULT_PAGE, DEFAULT_SIZE); // Use constants

        assertThat(actualUrl).isEqualTo(expectedUrl);
    }

    @Test
    void testGetPostsUrlWithoutUserIdAndPagination() {
        final String expectedUrl = baseUrl + POSTS_PATH + "?";

        final String actualUrl = urlService.getPostsUrl(null, null, null); // Use constants

        assertThat(actualUrl).isEqualTo(expectedUrl);
    }

    @Test
    void testGetPostByIdUrl() {
        final String expectedUrl = baseUrl + POSTS_PATH + "/" + DEFAULT_POST_ID; // Use constant

        final String actualUrl = urlService.getPostByIdUrl(DEFAULT_POST_ID); // Use constant

        assertThat(actualUrl).isEqualTo(expectedUrl);
    }

    @Test
    void testGetCommentsUrlWithPagination() {
        final String expectedUrl = baseUrl + COMMENTS_PATH + QUERY_START_PARAM + DEFAULT_PAGE + QUERY_LIMIT_PARAM + DEFAULT_SIZE + "&postId=" + DEFAULT_POST_ID;

        final String actualUrl = urlService.getCommentsUrl(DEFAULT_POST_ID, DEFAULT_PAGE, DEFAULT_SIZE); // Use constants

        assertThat(actualUrl).isEqualTo(expectedUrl);
    }

    @Test
    void testGetCommentUrl() {
        final String expectedUrl = baseUrl + COMMENTS_PATH + "/" + DEFAULT_POST_ID; // Use constant

        final String actualUrl = urlService.getCommentUrl(DEFAULT_POST_ID); // Use constant

        assertThat(actualUrl).isEqualTo(expectedUrl);
    }
}
