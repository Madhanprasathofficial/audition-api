package com.audition.web;

import com.audition.BaseIntegrationTest;
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

@SuppressWarnings("PMD")
@WebMvcTest(controllers = AuditionPostsController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuditionPostsControllerTest extends BaseIntegrationTest {

    @Autowired
    private final MockMvc mockMvc;

    @MockBean
    private final IAuditionPostService auditionPostService;

    private static final String POSTS_URL = "/posts";
    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String MEDIA_TYPE_ATOM_XML = MediaType.APPLICATION_ATOM_XML_VALUE;
    private static final String USER_ID_PARAM = "userId";
    private static final String SIZE_PARAM = "size";
    private static final String PAGE_PARAM = "page";
    private static final String INVALID_USER_ID = "-1";
    private static final String NON_INTEGER_USER_ID = "abc";
    private static final String SAMPLE_TITLE = "Sample Title";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Autowired
    AuditionPostsControllerTest(MockMvc mockMvc, IAuditionPostService auditionPostService) {
        this.mockMvc = mockMvc;
        this.auditionPostService = auditionPostService;
    }

    @Test
    void shouldFailForWrongMediaType() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .accept(MEDIA_TYPE_ATOM_XML)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldReturnAllPosts_WhenNoFilters() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnFilteredPostsByUserId() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .param(USER_ID_PARAM, "1")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnFilteredPostsByTitle() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .param("title", SAMPLE_TITLE)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void shouldFailForInvalidParameters() throws Exception {
        mockMvc.perform(get(POSTS_URL)
                        .param(SIZE_PARAM, "-1")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(POSTS_URL)
                        .param(USER_ID_PARAM, INVALID_USER_ID)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(POSTS_URL)
                        .param(USER_ID_PARAM, NON_INTEGER_USER_ID)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(POSTS_URL)
                        .param(USER_ID_PARAM, INVALID_USER_ID)
                        .param(SIZE_PARAM, "-5")
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get(POSTS_URL)
                        .param(PAGE_PARAM, "0")
                        .param(SIZE_PARAM, "10")
                        .param(USER_ID_PARAM, INVALID_USER_ID)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest());
    }
}
