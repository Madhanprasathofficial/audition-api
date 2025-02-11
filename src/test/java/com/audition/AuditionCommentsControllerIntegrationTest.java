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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("PMD")
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AuditionCommentsControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    protected transient MockMvc mockMvc;

    @RegisterExtension
    static final WireMockExtension WIREMOCK_SERVER = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort()).build();

    private static final String COMMENTS_ENDPOINT = "/comments";
    private static final String POST_ID_PARAM = "postId";
    private static final String START_PARAM = "_start";
    private static final String LIMIT_PARAM = "_limit";
    private static final int DEFAULT_START = 0;
    private static final int DEFAULT_LIMIT = 100;

    @DynamicPropertySource
    static void configureProperties(final DynamicPropertyRegistry propertyRegistry) {
        propertyRegistry.add("application.config.baseUrl", WIREMOCK_SERVER::baseUrl);
    }

    @Test
    void shouldReturnCommentsForPostId() throws Exception {
        final Integer postId = 1;
        final String responseType = "comments-id-post-stub.json";

        stubGetAuditionCommentsForPost(postId, responseType);

        this.mockMvc.perform(get(COMMENTS_ENDPOINT)
                        .param(POST_ID_PARAM, postId.toString())
                        .param(START_PARAM, String.valueOf(DEFAULT_START))
                        .param(LIMIT_PARAM, String.valueOf(DEFAULT_LIMIT))
                        .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].postId", is(postId)))
                .andExpect(jsonPath("$[0].name", is("id labore ex et quam laborum")));
    }

    @Test
    void shouldReturnCommentId() throws Exception {
        final Integer postId = 1;
        final String responseType = "comment-id-stub.json";

        stubGetAuditionCommentId(postId, responseType);

        this.mockMvc.perform(get(COMMENTS_ENDPOINT + "/" + postId)
                        .header(HttpHeaders.AUTHORIZATION, getBasicAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId", is(postId)))
                .andExpect(jsonPath("$.name", is("id labore ex et quam laborum")));
    }

    public static void stubGetAuditionCommentsForPost(final Integer postId, final String responseType) {
        WIREMOCK_SERVER.stubFor(WireMock.get(urlPathEqualTo(COMMENTS_ENDPOINT))
                .withQueryParam(POST_ID_PARAM, equalTo(postId.toString()))
                .withQueryParam(START_PARAM, equalTo(String.valueOf(DEFAULT_START)))
                .withQueryParam(LIMIT_PARAM, equalTo(String.valueOf(DEFAULT_LIMIT)))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile(responseType)));
    }

    public static void stubGetAuditionCommentId(final Integer postId, final String responseType) {
        WIREMOCK_SERVER.stubFor(WireMock.get(urlPathEqualTo(COMMENTS_ENDPOINT + "/" + postId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile(responseType)));
    }
}
