package com.audition.service;

import com.audition.integration.IAuditionIntegrationCommentsClient;
import com.audition.model.AuditionComment;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the IAuditionCommentsService interface.
 */
@Service
public class AuditionCommentsServiceImpl implements IAuditionCommentsService {

    private final transient IAuditionIntegrationCommentsClient auditionIntegrationCommentsClient;

    public AuditionCommentsServiceImpl(final IAuditionIntegrationCommentsClient auditionIntegrationCommentsClient) {
        this.auditionIntegrationCommentsClient = auditionIntegrationCommentsClient;
    }

    /**
     * Retrieves a list of comments for a specific audition post with pagination.
     *
     * @param postId the ID of the post for which comments are to be retrieved (must be positive)
     * @param page   the page number for pagination (0-based)
     * @param size   the number of comments per page
     * @return a list of comments for the specified post (empty list if no comments exist)
     */
    @Override
    public List<AuditionComment> getComments(@Positive final Integer postId, final Integer page, final Integer size) {
        return auditionIntegrationCommentsClient.getComments(postId, page, size);
    }

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param commentId the ID of the comment to retrieve (must be positive)
     * @return the requested comment, or null if not found
     */
    @Override
    public AuditionComment getComment(@Positive final Integer commentId) {
        return auditionIntegrationCommentsClient.getComment(commentId);
    }
}
