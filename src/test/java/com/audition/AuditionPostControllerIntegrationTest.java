package com.audition;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AuditionPostController.
 */
@SuppressWarnings("PMD")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuditionPostControllerIntegrationTest extends BaseIntegrationTest {

    // Constants for repeated strings
    private static final String POSTS_ENDPOINT = "/posts";
    private static final String USER_ID_PARAM = "userId";
    private static final String START_PARAM = "_start";
    private static final String LIMIT_PARAM = "_limit";
    private static final String TITLE_INVALID_USER_ID = "Invalid User ID";
    private static final String BODY_INVALID_USER_ID = "User ID must be positive";
    private static final String VALID_TITLE =
            "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";

    @Autowired
    protected MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer =
            WireMockExtension.newInstance()
                    .options(wireMockConfig().dynamicPort())
                    .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("application.config.baseUrl", wireMockServer::baseUrl);
    }

    @Test
    void shouldGetAuditionPostsForUser() throws Exception {
        final Integer userId = 1;
        stubGetAuditionPostsForUser(userId);

        this.mockMvc.perform(
                        get(POSTS_ENDPOINT)
                                .param(USER_ID_PARAM, userId.toString())
                                .param(START_PARAM, "0")
                                .param(LIMIT_PARAM, "100")
                                .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader())) // Add Basic Auth header
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(userId)))
                .andExpect(jsonPath("$[0].title", is(VALID_TITLE)));
    }

    @Test
    void shouldReturnEmptyListForUserWithNoPosts() throws Exception {
        final Integer userId = 3; // Assuming this user has no posts
        stubGetNoAuditionPostsForUser(userId);

        this.mockMvc.perform(
                        get(POSTS_ENDPOINT)
                                .param(USER_ID_PARAM, userId.toString())
                                .param(START_PARAM, "0")
                                .param(LIMIT_PARAM, "100")
                                .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader())) // Add Basic Auth header
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Collections.emptyList())));
    }

    @Test
    void shouldGetFailureResponseWhenPostsApiFails() throws Exception {
        final int userId = 22;
        stubGetPostsApiFailure(userId);

        this.mockMvc.perform(
                        get(POSTS_ENDPOINT)
                                .param(USER_ID_PARAM, Integer.toString(userId))
                                .param(START_PARAM, "0")
                                .param(LIMIT_PARAM, "100")
                                .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader())) // Add Basic Auth header
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldGetPostsWhenUserIdIsNegative() throws Exception {
        this.mockMvc.perform(
                        get(POSTS_ENDPOINT)
                                .param(USER_ID_PARAM, "-1")
                                .param(START_PARAM, "0")
                                .param(LIMIT_PARAM, "100")
                                .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader())) // Add Basic Auth header
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].title", is(TITLE_INVALID_USER_ID)))
                .andExpect(jsonPath("$[0].body", is(BODY_INVALID_USER_ID)));
    }

    @Test
    void shouldFailForNegativePageSize() throws Exception {
        final String path = POSTS_ENDPOINT + "?userId=1&page=1&size=-1";

        this.mockMvc.perform(get(path)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader())) // Add Basic Auth header
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void shouldFailForInvalidUserIdFormat() throws Exception {
        this.mockMvc.perform(
                        get(POSTS_ENDPOINT)
                                .param(USER_ID_PARAM, "invalid")
                                .param(START_PARAM, "0")
                                .param(LIMIT_PARAM, "100")
                                .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader())) // Add Basic Auth header
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.detail",
                        is("Failed to convert 'userId' with value: 'invalid'")));
    }

    @Test
    void shouldFailForWrongMediaType() throws Exception {
        final Integer userId = 3; // Assuming this user has no posts
        stubGetNoAuditionPostsForUser(userId);

        this.mockMvc.perform(
                        get(POSTS_ENDPOINT)
                                .param(USER_ID_PARAM, userId.toString())
                                .param(START_PARAM, "0")
                                .param(LIMIT_PARAM, "100")
                                .accept(MediaType.APPLICATION_ATOM_XML)
                                .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader())) // Add Basic Auth header
                .andExpect(status().isNotAcceptable())
                .andReturn();
    }

    public static void stubGetAuditionPostsForUser(final Integer userId) {
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo(POSTS_ENDPOINT))
                        .withQueryParam(USER_ID_PARAM, equalTo(userId.toString()))
                        .withQueryParam(START_PARAM, equalTo("0"))
                        .withQueryParam(LIMIT_PARAM, equalTo("100"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile("posts-stub.json"))); // Path to your stub file
    }

    public static void stubGetNoAuditionPostsForUser(final Integer userId) {
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo(POSTS_ENDPOINT))
                        .withQueryParam(USER_ID_PARAM, equalTo(userId.toString()))
                        .withQueryParam(START_PARAM, equalTo("0"))
                        .withQueryParam(LIMIT_PARAM, equalTo("100"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody("[]"))); // Returns an empty list
    }

    public static void stubGetPostsApiFailure(final int userId) {
        wireMockServer.stubFor(
                WireMock.get(urlPathEqualTo(POSTS_ENDPOINT))
                        .withQueryParam(USER_ID_PARAM, equalTo(Integer.toString(userId)))
                        .withQueryParam(START_PARAM, equalTo("0"))
                        .withQueryParam(LIMIT_PARAM, equalTo("100"))
                        .willReturn(aResponse().withStatus(500))); // Simulate server error
    }
}
