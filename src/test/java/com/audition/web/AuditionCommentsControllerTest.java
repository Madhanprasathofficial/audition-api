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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for AuditionCommentsController.
 */
@WebMvcTest(controllers = AuditionCommentsController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AuditionCommentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IAuditionCommentsService iAuditionCommentsService;

    private static final String COMMENTS_URL = "/comments";
    private static final String APPLICATION_JSON = MediaType.APPLICATION_JSON_VALUE;
    private static final String APPLICATION_PROBLEM_JSON = MediaType.APPLICATION_PROBLEM_JSON_VALUE;

    // Constants for repeated strings
    private static final String COMMENT_NOT_FOUND_MESSAGE = "Comment not found";
    private static final String INVALID_ID_MESSAGE = "Failed to convert 'id' with value: '%s'";
    private static final String BAD_REQUEST_MESSAGE = "Bad Request";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "An unexpected error occurred";

    private AuditionComment createSampleComment(int id, String name, String email, String body) {
        AuditionComment comment = new AuditionComment();
        comment.setId(id);
        comment.setPostId(1); // Set a default postId
        comment.setName(name);
        comment.setEmail(email);
        comment.setBody(body);
        return comment;
    }

    @Test
    void shouldReturnAllComments_WhenValidPostIdIsProvided() throws Exception {
        int postId = 1;
        List<AuditionComment> comments = List.of(
                createSampleComment(1, "John Doe", "john.doe@example.com", "First comment"),
                createSampleComment(2, "Jane Doe", "jane.doe@example.com", "Second comment")
        );

        when(iAuditionCommentsService.getComments(postId, 0, 100)).thenReturn(comments);

        mockMvc.perform(get(COMMENTS_URL)
                        .param("postId", String.valueOf(postId))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(comments.size()));
    }

    @Test
    void shouldReturnComment_WhenValidCommentIdIsProvided() throws Exception {
        int commentId = 1;
        AuditionComment comment = createSampleComment(commentId, "Sample User", "sample.user@example.com", "Sample comment");

        when(iAuditionCommentsService.getComment(commentId)).thenReturn(comment);

        mockMvc.perform(get(COMMENTS_URL + "/{id}", commentId)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.name").value("Sample User"))
                .andExpect(jsonPath("$.email").value("sample.user@example.com"))
                .andExpect(jsonPath("$.body").value("Sample comment"));
    }

    @Test
    void shouldReturn404_WhenCommentNotFound() throws Exception {
        int commentId = 999;
        when(iAuditionCommentsService.getComment(commentId)).thenReturn(null);

        mockMvc.perform(get(COMMENTS_URL + "/{id}", commentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(COMMENT_NOT_FOUND_MESSAGE));
    }

    @Test
    void shouldReturn400_WhenInvalidCommentIdFormatIsProvided() throws Exception {
        mockMvc.perform(get(COMMENTS_URL + "/{id}", "invalidId"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value(BAD_REQUEST_MESSAGE))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value(String.format(INVALID_ID_MESSAGE, "invalidId")));
    }

    @Test
    void shouldReturn400_WhenPostIdIsNegative() throws Exception {
        mockMvc.perform(get(COMMENTS_URL)
                        .param("postId", "-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("getComments.postId: must be greater than 0"));
    }

    @Test
    void shouldHandleServiceExceptionGracefully() throws Exception {
        int commentId = 1;
        when(iAuditionCommentsService.getComment(commentId)).thenThrow(new RuntimeException("Database connectivity issue"));

        mockMvc.perform(get(COMMENTS_URL + "/{id}", commentId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Internal Server Error"))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.detail").value(INTERNAL_SERVER_ERROR_MESSAGE));
    }
}
