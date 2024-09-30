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

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the AuditionPostByIdController.
 */
@SuppressWarnings("PMD")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class AuditionPostByIdControllerIntegrationTest {

    @Autowired
    protected transient MockMvc mockMvc;

    @RegisterExtension
    static WireMockExtension wireMockServer = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("application.config.baseUrl", wireMockServer::baseUrl);
    }

    private static final String POSTS_ENDPOINT = "/posts";
    private static final String COMMENTS_ENDPOINT = "/comments";
    private static final String EMPTY_LIST_RESPONSE = "{\"message\": \"Page not found\"}";
    private static final String VALID_TITLE = "sunt aut facere repellat provident occaecati excepturi optio reprehenderit";

    @Test
    void shouldReturnAuditionPostById() throws Exception {
        int postId = 1;
        String responseType = "post-by-id-stub.json";

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(POSTS_ENDPOINT + "/" + postId))
                .willReturn(WireMock.aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile(responseType)));

        this.mockMvc.perform(get(POSTS_ENDPOINT + "/" + postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(postId)))
                .andExpect(jsonPath("$.title", is(VALID_TITLE)));
    }

    @Test
    void shouldReturnEmptyListForUserWithNoPosts() throws Exception {
        int postId = 3;

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo(POSTS_ENDPOINT + "/" + postId))
                .willReturn(WireMock.aResponse()
                        .withStatus(204)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBody(EMPTY_LIST_RESPONSE)));

        this.mockMvc.perform(get(POSTS_ENDPOINT + "/" + postId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message", is("Page not found")));
    }

    @Test
    void shouldFailForWrongMediaTypeWithComments() throws Exception {
        int postId = 1;

        this.mockMvc.perform(get(POSTS_ENDPOINT + "/" + postId + COMMENTS_ENDPOINT)
                        .accept(MediaType.APPLICATION_ATOM_XML))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void shouldFailForWrongMediaType() throws Exception {
        int postId = 1;

        this.mockMvc.perform(get(POSTS_ENDPOINT + "/" + postId)
                        .accept(MediaType.APPLICATION_ATOM_XML))
                .andExpect(status().isNotAcceptable());
    }
}
