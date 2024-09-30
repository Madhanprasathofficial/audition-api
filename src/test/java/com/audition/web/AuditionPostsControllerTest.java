package com.audition.web;

import com.audition.service.IAuditionPostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for AuditionPostsController.
 */
@SuppressWarnings("PMD")
@WebMvcTest(controllers = AuditionPostsController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuditionPostsControllerTest {

    @Autowired
    private final MockMvc mockMvc;

    @MockBean
    private final IAuditionPostService auditionPostService;

    private static final String POSTS_URL = "/posts";
    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;

    // Constructor for dependency injection
    @Autowired
    AuditionPostsControllerTest(MockMvc mockMvc, IAuditionPostService auditionPostService) {
        this.mockMvc = mockMvc;
        this.auditionPostService = auditionPostService;
    }

    @Test
    void shouldFailForWrongMediaType() throws Exception {
        mockMvc.perform(get(POSTS_URL).accept(MediaType.APPLICATION_ATOM_XML))
                .andExpect(status().isNotAcceptable()); // Expect 406 Not Acceptable
    }

    @Test
    void shouldReturnAllPosts_WhenNoFilters() throws Exception {
        mockMvc.perform(get(POSTS_URL).accept(APPLICATION_JSON))
                .andExpect(status().isOk()); // Expect 200 OK
    }

    @Test
    void shouldReturnFilteredPostsByUserId() throws Exception {
        mockMvc.perform(get(POSTS_URL).param("userId", "1").accept(APPLICATION_JSON))
                .andExpect(status().isOk()); // Expect 200 OK
    }

    @Test
    void shouldReturnFilteredPostsByTitle() throws Exception {
        mockMvc.perform(get(POSTS_URL).param("title", "Sample Title").accept(APPLICATION_JSON))
                .andExpect(status().isOk()); // Expect 200 OK
    }

    @Test
    void shouldFailForInvalidParameters() throws Exception {
        // Invalid page size
        mockMvc.perform(get(POSTS_URL).param("size", "-1").accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Invalid userId
        mockMvc.perform(get(POSTS_URL).param("userId", "-1").accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Non-integer userId
        mockMvc.perform(get(POSTS_URL).param("userId", "abc").accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Multiple invalid parameters
        mockMvc.perform(get(POSTS_URL).param("userId", "-1").param("size", "-5").accept(APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request

        // Negative userId in a paginated request
        mockMvc.perform(get(POSTS_URL).param("page", "0").param("size", "10").param("userId", "-1"))
                .andExpect(status().isBadRequest()); // Expect 400 Bad Request
    }
}
