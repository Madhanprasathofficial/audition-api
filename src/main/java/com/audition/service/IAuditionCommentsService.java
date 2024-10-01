package com.audition.service;

import com.audition.model.AuditionComment;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Interface for interacting with audition comments in the integration layer.
 */
public interface IAuditionCommentsService {

    /**
     * Retrieves a list of comments for a specified audition post.
     *
     * @param postId the ID of the post (must be positive)
     * @param page   the page number for pagination (optional, can be null)
     * @param size   the number of comments per page (optional, can be null)
     * @return a list of audition comments, or an empty list if none are found
     */
    List<AuditionComment> getComments(@Positive(message = "Post ID must be positive.") int postId, Integer page, Integer size);

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param commentId the ID of the comment (must be positive)
     * @return the requested audition comment, or null if not found
     */
    AuditionComment getComment(@Positive(message = "Comment ID must be positive.") Integer commentId);
}
