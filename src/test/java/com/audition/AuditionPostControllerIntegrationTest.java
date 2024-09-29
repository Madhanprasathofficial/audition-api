package com.audition;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

// Static import for all static methods
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.CoreMatchers.is;

/**
 * Integration tests for the AuditionPostController.
 * These tests verify the behavior of the controller's endpoints
 * using WireMock to stub external service calls.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuditionPostControllerIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("application.config.baseUrl", wireMockServer::baseUrl);
    }

    // Constants for repeated strings
    private static final String POSTS_ENDPOINT = "/posts";
    private static final String USER_ID_PARAM = "userId";
    private static final String START_PARAM = "_start";
    private static final String LIMIT_PARAM = "_limit";
    private static final String TITLE_INVALID_USER_ID = "Invalid User ID";
    private static final String BODY_INVALID_USER_ID = "User ID must be positive";
    private static final String VALID_TITLE = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";

    /**
     * Test case to verify the retrieval of audition posts for a specific user.
     * It stubs the response using WireMock and asserts the expected results.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldGetAuditionPostsForUser() throws Exception {
        Integer userId = 1;

        // Use the static method from WireMockStubs to stub the response
        stubGetAuditionPostsForUser(userId);

        // Performing the GET request and verifying the response
        this.mockMvc.perform(get(POSTS_ENDPOINT)
                        .param(USER_ID_PARAM, userId.toString())
                        .param(START_PARAM, "0")
                        .param(LIMIT_PARAM, "100")) // Ensure correct limit
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId", is(userId)))
                .andExpect(jsonPath("$[0].title", is(VALID_TITLE)));
    }

    /**
     * Test case to verify the response when a user has no posts.
     * It stubs the response to return an empty list and verifies the result.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldReturnEmptyListForUserWithNoPosts() throws Exception {
        Integer userId = 3; // Assuming this user has no posts

        // Stubbing the response to return an empty list
        stubGetNoAuditionPostsForUser(userId);

        this.mockMvc.perform(get(POSTS_ENDPOINT)
                        .param(USER_ID_PARAM, userId.toString())
                        .param(START_PARAM, "0")
                        .param(LIMIT_PARAM, "100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(Collections.emptyList())));
    }

    /**
     * Test case to verify the response when the posts API fails.
     * It stubs the failure response using WireMock and asserts the expected results.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldGetFailureResponseWhenPostsApiFails() throws Exception {
        int userId = 22;

        // Use the static method from WireMockStubs to stub the failure response
        stubGetPostsApiFailure(userId);

        // Performing the GET request and verifying the response
        this.mockMvc.perform(get(POSTS_ENDPOINT)
                        .param(USER_ID_PARAM, Integer.toString(userId))
                        .param(START_PARAM, "0")
                        .param(LIMIT_PARAM, "100"))
                .andExpect(status().is5xxServerError());
    }

    /**
     * Test case to verify the response when the user ID is negative.
     * It checks that a bad request is returned with the appropriate error message.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldGetPostsWhenUserIdIsNegative() throws Exception {
        this.mockMvc.perform(get(POSTS_ENDPOINT)
                        .param(USER_ID_PARAM, "-1")
                        .param(START_PARAM, "0")
                        .param(LIMIT_PARAM, "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].title", is(TITLE_INVALID_USER_ID)))
                .andExpect(jsonPath("$[0].body", is(BODY_INVALID_USER_ID)));
    }

    /**
     * Test case to verify that a bad request is returned for negative page size.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldFailForNegativePageSize() throws Exception {
        String path = POSTS_ENDPOINT + "?userId=1&page=1&size=-1"; // Construct the request path

        this.mockMvc.perform(get(path)
                        .accept(MediaType.APPLICATION_JSON)) // Setting the wrong media type
                .andExpect(status().isBadRequest()) // Expecting a BAD_REQUEST response
                .andReturn();
    }

    /**
     * Test case to verify that a bad request is returned for an invalid user ID format.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldFailForInvalidUserIdFormat() throws Exception {
        this.mockMvc.perform(get(POSTS_ENDPOINT)
                        .param(USER_ID_PARAM, "invalid")
                        .param(START_PARAM, "0")
                        .param(LIMIT_PARAM, "100"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Bad Request")))
                .andExpect(jsonPath("$.detail", is("Failed to convert 'userId' with value: 'invalid'")));
    }

    /**
     * Test case to verify that a NOT_ACCEPTABLE response is returned for the wrong media type.
     *
     * @throws Exception if the request fails
     */
    @Test
    void shouldFailForWrongMediaType() throws Exception {
        Integer userId = 3; // Assuming this user has no posts

        // Stubbing the response to return an empty list
        stubGetNoAuditionPostsForUser(userId);

        this.mockMvc.perform(get(POSTS_ENDPOINT)
                        .param(USER_ID_PARAM, userId.toString())
                        .param(START_PARAM, "0")
                        .param(LIMIT_PARAM, "100")
                        .accept(MediaType.APPLICATION_ATOM_XML)) // Set an incorrect media type
                .andExpect(status().isNotAcceptable()) // Expecting a NOT_ACCEPTABLE response
                .andReturn();
    }

    /**
     * Stubs the WireMock server to return audition posts for a specific user.
     *
     * @param userId the ID of the user to retrieve posts for
     */
    public static void stubGetAuditionPostsForUser(Integer userId) {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(POSTS_ENDPOINT))
                .withQueryParam(USER_ID_PARAM, equalTo(userId.toString()))
                .withQueryParam(START_PARAM, equalTo("0"))
                .withQueryParam(LIMIT_PARAM, equalTo("100"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("posts-stub.json"))); // Path to your stub file
    }

    /**
     * Stubs the WireMock server to return an empty list of audition posts for a specific user.
     *
     * @param userId the ID of the user to retrieve posts for
     */
    public static void stubGetNoAuditionPostsForUser(Integer userId) {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(POSTS_ENDPOINT))
                .withQueryParam(USER_ID_PARAM, equalTo(userId.toString()))
                .withQueryParam(START_PARAM, equalTo("0"))
                .withQueryParam(LIMIT_PARAM, equalTo("100"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(Collections.emptyList().toString()))); // Returns an empty list
    }

    /**
     * Stubs the WireMock server to simulate a failure response from the posts API.
     *
     * @param userId the ID of the user to simulate the failure for
     */
    public static void stubGetPostsApiFailure(int userId) {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo(POSTS_ENDPOINT))
                .withQueryParam(USER_ID_PARAM, equalTo(Integer.toString(userId)))
                .withQueryParam(START_PARAM, equalTo("0"))
                .withQueryParam(LIMIT_PARAM, equalTo("100"))
                .willReturn(aResponse().withStatus(500))); // Simulates server error
    }
}
