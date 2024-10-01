package com.audition.web;

import com.audition.BaseIntegrationTest;
import com.audition.model.AuditionPost;
import com.audition.service.IAuditionPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("PMD")
@WebMvcTest(controllers = AuditionPostsController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
public class AuditionPostByIdControllerTest extends BaseIntegrationTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private IAuditionPostService auditionPostService;

    private static final String POSTS_URL = "/posts";
    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String APPLICATION_PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 100;

    // Constants for repeated strings
    private static final String POST_NOT_FOUND_MESSAGE = "Post not found";
    private static final String PAGE_NOT_FOUND_MESSAGE = "Page not found";
    private static final String INVALID_ID_MESSAGE = "Failed to convert 'id' with value: '%s'";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred";
    private static final String BAD_REQUEST_MESSAGE = "Bad Request";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String SAMPLE_POST_TITLE = "Sample Post";
    private static final String SAMPLE_POST_BODY = "Random body";
    private static final String SAMPLE_TITLE = "Sample Title";
    private static final String COMMENT_PATH = "/comments";
    private static final String ID_PATH_VARIABLE = "{id}";
    private static final String INVALID_ID_FORMAT = "invalidId";
    private static final int NON_EXISTENT_POST_ID = 999;

    // New Constants
    private static final String POST_ID_MUST_BE_GREATER_THAN_ZERO = "getPostWithComments.postId: must be greater than 0";
    private static final String INTERNAL_SERVER_ERROR_TITLE = "Internal Server Error";

    // JSON Path Constants
    private static final String JSON_PATH_TITLE = "$.title";
    private static final String JSON_PATH_STATUS = "$.status";
    private static final String JSON_PATH_DETAIL = "$.detail";
    private static final String JSON_PATH_BODY = "$.body";
    private static final String JSON_PATH_ID = "$.id";
    private static final String JSON_PATH_MESSAGE = "$.message";

    private AuditionPost createSamplePost(final int id, final String title, final String body) {
        final AuditionPost post = new AuditionPost();
        post.setId(id);
        post.setTitle(title);
        post.setUserId(1); // Assume a user ID for testing
        post.setBody(body);
        post.setAuditionComments(new ArrayList<>());
        return post;
    }

    @Test
    void shouldReturnPostWhenValidIdIsProvided() throws Exception {
        final AuditionPost post = createSamplePost(1, SAMPLE_POST_TITLE, SAMPLE_POST_BODY);

        when(auditionPostService.getPostById(1, false, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)).thenReturn(post);

        mockMvc.perform(get(POSTS_URL + "/1")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(jsonPath(JSON_PATH_ID).value(1))
                .andExpect(jsonPath(JSON_PATH_TITLE).value(SAMPLE_POST_TITLE))
                .andExpect(jsonPath(JSON_PATH_BODY).value(SAMPLE_POST_BODY))
                .andExpect(status().isOk());
    }

    @Test
     void shouldReturn204WhenPostNotFound() throws Exception {
        when(auditionPostService.getPostById(NON_EXISTENT_POST_ID, false, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)).thenReturn(null);

        mockMvc.perform(get(POSTS_URL + "/" + NON_EXISTENT_POST_ID)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isNoContent())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(PAGE_NOT_FOUND_MESSAGE));
    }

    @Test
    public void shouldReturn400WhenInvalidIdFormatIsProvided() throws Exception {
        mockMvc.perform(get(POSTS_URL + "/abc")
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(JSON_PATH_TITLE).value(BAD_REQUEST_MESSAGE))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(400))
                .andExpect(jsonPath(JSON_PATH_DETAIL).value(String.format(INVALID_ID_MESSAGE, "abc")));
    }

    @Test
    public void shouldReturn500WhenServerErrorOccurs() throws Exception {
        when(auditionPostService.getPostById(1, false, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)).thenThrow(new RuntimeException("Database connectivity issue"));

        mockMvc.perform(get(POSTS_URL + "/1")
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(JSON_PATH_TITLE).value(INTERNAL_SERVER_ERROR_TITLE))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(500))
                .andExpect(jsonPath(JSON_PATH_DETAIL).value(INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @Test
    void shouldReturnPostWithCommentsWhenPostExists() throws Exception {
        int postId = 1;
        AuditionPost post = createSamplePost(postId, SAMPLE_TITLE, SAMPLE_POST_BODY);

        when(auditionPostService.getPostById(postId, true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)).thenReturn(post);

        mockMvc.perform(get(POSTS_URL + "/" + ID_PATH_VARIABLE + COMMENT_PATH, postId)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_ID).value(postId))
                .andExpect(jsonPath(JSON_PATH_TITLE).value(SAMPLE_TITLE));
    }

    @Test
    void shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        when(auditionPostService.getPostById(NON_EXISTENT_POST_ID, true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)).thenReturn(null);

        mockMvc.perform(get(POSTS_URL + "/" + ID_PATH_VARIABLE + COMMENT_PATH, NON_EXISTENT_POST_ID)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(POST_NOT_FOUND_MESSAGE));
    }

    @Test
    void shouldReturnBadRequestWhenPostIdIsNegative() throws Exception {
        mockMvc.perform(get(POSTS_URL + "/" + ID_PATH_VARIABLE + COMMENT_PATH, -1)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(400))
                .andExpect(jsonPath(JSON_PATH_DETAIL).value(POST_ID_MUST_BE_GREATER_THAN_ZERO));
    }

    @Test
    void shouldHandleServiceExceptionGracefully() throws Exception {
        int postId = 1;
        when(auditionPostService.getPostById(postId, true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)).thenThrow(new RuntimeException("Database connectivity issue"));

        mockMvc.perform(get(POSTS_URL + "/" + ID_PATH_VARIABLE + COMMENT_PATH, postId)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(JSON_PATH_TITLE).value(INTERNAL_SERVER_ERROR_TITLE))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(500))
                .andExpect(jsonPath(JSON_PATH_DETAIL).value(INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidPostIdFormat() throws Exception {
        mockMvc.perform(get(POSTS_URL + "/" + ID_PATH_VARIABLE + COMMENT_PATH, INVALID_ID_FORMAT)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(400))
                .andExpect(jsonPath(JSON_PATH_DETAIL).value(String.format(INVALID_ID_MESSAGE, INVALID_ID_FORMAT)));
    }
}
