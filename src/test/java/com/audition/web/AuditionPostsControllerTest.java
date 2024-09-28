package com.audition.web;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuditionPostsController.
 */
@WebMvcTest(controllers = AuditionPostsController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuditionPostsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAuditionPostService iAuditionPostService;

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

    private AuditionPost createSamplePost(int id, String title, String body) {
        AuditionPost post = new AuditionPost();
        post.setId(id);
        post.setTitle(title);
        post.setUserId(1); // Assume a user ID for testing
        post.setBody(body);
        post.setAuditionComments(new ArrayList<>());
        return post;
    }

    @Test
    void shouldFailForWrongMediaType() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .accept(MediaType.APPLICATION_ATOM_XML))
                .andExpect(status().isNotAcceptable()); // Expect 406 Not Acceptable
    }

    @Test
    void shouldReturnAllPosts_WhenNoFilters() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk()); // Expect 200 OK
    }

    @Test
    void shouldReturnFilteredPostsByUserId() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .param("userId", "1")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk()); // Expect 200 OK
    }

    @Test
    void shouldReturnFilteredPostsByTitle() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .param("title", "Sample Title")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk()); // Expect 200 OK
    }

    @Test
    void shouldFailForInvalidParameters() throws Exception {
        // Invalid page size
        mockMvc.perform(get(POSTS_URL)
                        .param("size", "-1")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Invalid userId
        mockMvc.perform(get(POSTS_URL)
                        .param("userId", "-1")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Non-integer userId
        mockMvc.perform(get(POSTS_URL)
                        .param("userId", "abc")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Multiple invalid parameters
        mockMvc.perform(get(POSTS_URL)
                        .param("userId", "-1")
                        .param("size", "-5")
                        .accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Negative userId in a paginated request
        mockMvc.perform(get(POSTS_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .param("userId", "-1"))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request
    }

    @Test
    void shouldReturnPost_WhenValidIdIsProvided() throws Exception {
        AuditionPost post = createSamplePost(1, "Sample Post", "Random body");

        when(iAuditionPostService.getPostById(1, false, DEFAULT_PAGE, DEFAULT_PAGE_SIZE))
                .thenReturn(post); // Mock the service response

        mockMvc.perform(get(POSTS_URL + "/1")
                        .accept(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Sample Post"))
                .andExpect(jsonPath("$.body").value("Random body"))
                .andExpect(status().isOk()); // Expect 200 OK
    }

    @Test
    public void shouldReturn404_WhenPostNotFound() throws Exception {
        when(iAuditionPostService.getPostById(999, false, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)).thenReturn(null);

        mockMvc.perform(get(POSTS_URL + "/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(PAGE_NOT_FOUND_MESSAGE));
    }

    @Test
    public void shouldReturn400_WhenInvalidIdFormatIsProvided() throws Exception {
        mockMvc.perform(get(POSTS_URL + "/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST_MESSAGE))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(String.format(INVALID_ID_MESSAGE, "abc")));
    }

    @Test
    public void shouldReturn500_WhenServerErrorOccurs() throws Exception {
        when(iAuditionPostService.getPostById(1, false, DEFAULT_PAGE, DEFAULT_PAGE_SIZE))
                .thenThrow(new RuntimeException("Database connectivity issue"));

        mockMvc.perform(get(POSTS_URL + "/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value(INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @Test
    void shouldReturnPostWithCommentsWhenPostExists() throws Exception {
        int postId = 1;
        AuditionPost post = createSamplePost(postId, "Sample Title", "Random body");

        when(iAuditionPostService.getPostById(postId, true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE))
                .thenReturn(post); // Mock the service response

        mockMvc.perform(get(POSTS_URL + "/{id}/comments", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.title").value("Sample Title"));
    }

    @Test
    void shouldReturnNotFoundWhenPostDoesNotExist() throws Exception {
        Integer postId = 999; // Non-existent post ID
        when(iAuditionPostService.getPostById(postId, true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE)).thenReturn(null);

        mockMvc.perform(get(POSTS_URL + "/{id}/comments", postId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(POST_NOT_FOUND_MESSAGE));
    }

    @Test
    void shouldReturnBadRequestWhenPostIdIsNegative() throws Exception {
        mockMvc.perform(get(POSTS_URL + "/{id}/comments", -1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("getPostWithComments.postId: must be greater than 0"));
    }

    @Test
    void shouldHandleServiceExceptionGracefully() throws Exception {
        Integer postId = 1;
        when(iAuditionPostService.getPostById(postId, true, DEFAULT_PAGE, DEFAULT_PAGE_SIZE))
                .thenThrow(new RuntimeException("Database connectivity issue"));

        mockMvc.perform(get(POSTS_URL + "/{id}/comments", postId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value(INTERNAL_SERVER_ERROR_MESSAGE));
    }

    @Test
    void shouldReturnBadRequestWhenInvalidPostIdFormat() throws Exception {
        mockMvc.perform(get(POSTS_URL + "/{id}/comments", "invalidId"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(String.format(INVALID_ID_MESSAGE, "invalidId")));
    }
}
