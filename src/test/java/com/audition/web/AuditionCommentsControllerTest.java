package com.audition.web;

import com.audition.BaseIntegrationTest;
import com.audition.model.AuditionComment;
import com.audition.service.IAuditionCommentsService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import java.util.List;

import static com.github.tomakehurst.wiremock.common.ContentTypes.APPLICATION_JSON;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test class for AuditionCommentsController.
 */
@WebMvcTest(controllers = AuditionCommentsController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuditionCommentsControllerTest extends BaseIntegrationTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    @MockBean
    private transient IAuditionCommentsService auditionCommentsService;

    private static final String COMMENTS_URL = "/comments";
    private static final String APPLICATION_PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    // Constants for repeated strings
    private static final String COMMENT_NOT_FOUND_MESSAGE = "Comment not found";
    private static final String INVALID_ID_MESSAGE = "Failed to convert 'id' with value: '%s'";
    private static final String BAD_REQUEST_MESSAGE = "BAD_REQUEST";
    private static final String BAD_REQUEST = "Bad Request";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred";
    private static final String COMMENT_ID_PATH = "/{id}";
    private static final String INVALID_ID = "invalidId";
    private static final String POST_ID_VALUE = "postId";

    // Sample user constants
    private static final String SAMPLE_USER_NAME = "Sample User";
    private static final String SAMPLE_USER_EMAIL = "sample.user@example.com";
    private static final String SAMPLE_COMMENT_BODY = "Sample comment";

    // New Constants for comments
    private static final int POST_ID = 1; // Constant for postId
    private static final String FIRST_COMMENT_NAME = "John Doe";
    private static final String FIRST_COMMENT_EMAIL = "john.doe@example.com";
    private static final String FIRST_COMMENT_BODY = "First comment";

    private static final String SECOND_COMMENT_NAME = "Jane Doe";
    private static final String SECOND_COMMENT_EMAIL = "jane.doe@example.com";
    private static final String SECOND_COMMENT_BODY = "Second comment";

    // Error messages and titles
    private static final String POST_ID_MUST_BE_GREATER_THAN_ZERO = "getComments.postId: must be greater than 0";
    private static final String INTERNAL_SERVER_ERROR_TITLE = "Internal Server Error";

    // JSON Path Constants
    private static final String JSON_PATH_TITLE = "$.title";
    private static final String JSON_PATH_STATUS = "$.status";
    private static final String JSON_PATH_DETAIL = "$.detail";
    private static final String JSON_PATH_MESSAGE = "$.message";
    private static final String JSON_PATH_ID = "$.id"; // Added constant for id
    private static final String JSON_PATH_NAME = "$.name"; // Added constant for name
    private static final String JSON_PATH_EMAIL = "$.email"; // Added constant for email
    private static final String JSON_PATH_BODY = "$.body"; // Added constant for body

    private AuditionComment createSampleComment(final int id, final String name, final String email, final String body) {
        final AuditionComment comment = new AuditionComment();
        comment.setId(id);
        comment.setPostId(POST_ID); // Use the constant postId
        comment.setName(name);
        comment.setEmail(email);
        comment.setBody(body);
        return comment;
    }

    @Test
    void shouldReturnAllCommentswhenValidPostIdIsProvided() throws Exception {
        final List<AuditionComment> comments = List.of(
                createSampleComment(1, FIRST_COMMENT_NAME, FIRST_COMMENT_EMAIL, FIRST_COMMENT_BODY),
                createSampleComment(2, SECOND_COMMENT_NAME, SECOND_COMMENT_EMAIL, SECOND_COMMENT_BODY)
        );

        when(auditionCommentsService.getComments(POST_ID, 0, 100)).thenReturn(comments);

        mockMvc.perform(get(COMMENTS_URL)
                        .param(POST_ID_VALUE, String.valueOf(POST_ID))
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(comments.size()));
    }

    @Test
    void testShouldReturnCommentWhenValidCommentIdIsProvided() throws Exception {
        final int commentId = 1;
        final AuditionComment comment = createSampleComment(commentId, SAMPLE_USER_NAME, SAMPLE_USER_EMAIL, SAMPLE_COMMENT_BODY);

        when(auditionCommentsService.getComment(commentId)).thenReturn(comment);

        mockMvc.perform(get(COMMENTS_URL + COMMENT_ID_PATH, commentId)
                        .accept(APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_ID).value(commentId))
                .andExpect(jsonPath(JSON_PATH_NAME).value(SAMPLE_USER_NAME))
                .andExpect(jsonPath(JSON_PATH_EMAIL).value(SAMPLE_USER_EMAIL))
                .andExpect(jsonPath(JSON_PATH_BODY).value(SAMPLE_COMMENT_BODY));
    }

    @Test
    void shouldReturn404whenCommentNotFound() throws Exception {
        final int commentId = 999;
        when(auditionCommentsService.getComment(commentId)).thenReturn(null);

        mockMvc.perform(get(COMMENTS_URL + COMMENT_ID_PATH, commentId)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath(JSON_PATH_MESSAGE).value(COMMENT_NOT_FOUND_MESSAGE));
    }

    @Test
    void shouldReturn400WhenInvalidCommentIdFormatIsProvided() throws Exception {
        mockMvc.perform(get(COMMENTS_URL + COMMENT_ID_PATH, INVALID_ID)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(JSON_PATH_TITLE).value(BAD_REQUEST))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(400))
                .andExpect(jsonPath(JSON_PATH_DETAIL).value(String.format(INVALID_ID_MESSAGE, INVALID_ID)));
    }

    @Test
    void shouldReturn400WhenPostIdIsNegative() throws Exception {
        mockMvc.perform(get(COMMENTS_URL)
                        .param(POST_ID_VALUE, "-1")
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(JSON_PATH_TITLE).value(BAD_REQUEST_MESSAGE))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(400))
                .andExpect(jsonPath(JSON_PATH_DETAIL).value(POST_ID_MUST_BE_GREATER_THAN_ZERO));
    }

    @Test
    void shouldHandleServiceExceptionGracefully() throws Exception {
        final int commentId = 1;
        when(auditionCommentsService.getComment(commentId)).thenThrow(new RuntimeException("Database connectivity issue"));

        mockMvc.perform(get(COMMENTS_URL + COMMENT_ID_PATH, commentId)
                        .header(AUTHORIZATION_HEADER, getBasicAuthHeader()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath(JSON_PATH_TITLE).value(INTERNAL_SERVER_ERROR_TITLE))
                .andExpect(jsonPath(JSON_PATH_STATUS).value(500))
                .andExpect(jsonPath(JSON_PATH_DETAIL).value(INTERNAL_SERVER_ERROR_MESSAGE));
    }
}
