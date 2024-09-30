package com.audition.web;

import com.audition.model.AuditionComment;
import com.audition.service.IAuditionCommentsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Test class for AuditionCommentsController.
 */
@SuppressWarnings("PMD")
@WebMvcTest(controllers = AuditionCommentsController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuditionCommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAuditionCommentsService auditionCommentsService;

    private static final String COMMENTS_URL = "/comments";
    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String APPLICATION_PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    // Constants for repeated strings
    private static final String COMMENT_NOT_FOUND_MESSAGE = "Comment not found";
    private static final String INVALID_ID_MESSAGE = "Failed to convert 'id' with value: '%s'";
    private static final String BAD_REQUEST_MESSAGE = "Bad Request";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred";
    private static final String COMMENT_ID_PATH = "/{id}";

    private AuditionComment createSampleComment(final int id, final String name, final String email, final String body) {
        final AuditionComment comment = new AuditionComment();
        comment.setId(id);
        comment.setPostId(1); // Set a default postId
        comment.setName(name);
        comment.setEmail(email);
        comment.setBody(body);
        return comment;
    }

    @Test
    void testShouldReturnAllComments_WhenValidPostIdIsProvided() throws Exception {
        final int postId = 1;
        final List<AuditionComment> comments = List.of(
                createSampleComment(1, "John Doe", "john.doe@example.com", "First comment"),
                createSampleComment(2, "Jane Doe", "jane.doe@example.com", "Second comment")
        );

        when(auditionCommentsService.getComments(postId, 0, 100)).thenReturn(comments);

        mockMvc.perform(get(COMMENTS_URL)
                        .param("postId", String.valueOf(postId))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(comments.size()));
    }

    @Test
    void testShouldReturnComment_WhenValidCommentIdIsProvided() throws Exception {
        final int commentId = 1;
        final AuditionComment comment = createSampleComment(commentId, "Sample User", "sample.user@example.com", "Sample comment");

        when(auditionCommentsService.getComment(commentId)).thenReturn(comment);

        mockMvc.perform(get(COMMENTS_URL + COMMENT_ID_PATH, commentId)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.name").value("Sample User"))
                .andExpect(jsonPath("$.email").value("sample.user@example.com"))
                .andExpect(jsonPath("$.body").value("Sample comment"));
    }

    @Test
    void testShouldReturn404_WhenCommentNotFound() throws Exception {
        final int commentId = 999;
        when(auditionCommentsService.getComment(commentId)).thenReturn(null);

        mockMvc.perform(get(COMMENTS_URL + COMMENT_ID_PATH, commentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(COMMENT_NOT_FOUND_MESSAGE));
    }

    @Test
    void testShouldReturn400_WhenInvalidCommentIdFormatIsProvided() throws Exception {
        mockMvc.perform(get(COMMENTS_URL + COMMENT_ID_PATH, "invalidId"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST_MESSAGE))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(String.format(INVALID_ID_MESSAGE, "invalidId")));
    }

    @Test
    void testShouldReturn400_WhenPostIdIsNegative() throws Exception {
        mockMvc.perform(get(COMMENTS_URL)
                        .param("postId", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("getComments.postId: must be greater than 0"));
    }

    @Test
    void testShouldHandleServiceExceptionGracefully() throws Exception {
        final int commentId = 1;
        when(auditionCommentsService.getComment(commentId)).thenThrow(new RuntimeException("Database connectivity issue"));

        mockMvc.perform(get(COMMENTS_URL + COMMENT_ID_PATH, commentId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value(INTERNAL_SERVER_ERROR_MESSAGE));
    }
}
