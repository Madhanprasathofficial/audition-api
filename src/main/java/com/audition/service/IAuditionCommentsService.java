package com.audition.service;

import com.audition.model.AuditionComment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.util.List;

import static com.audition.web.AuditionCommentsController.MAX_PAGE_SIZE;

public interface IAuditionCommentsService {

    /**
     * Retrieves a list of comments for a specific post with pagination support.
     *
     * @param postId the ID of the post (must be positive)
     * @param page   the page number to retrieve (must be non-negative)
     * @param size   the number of comments per page (must be positive and less than or equal to MAX_PAGE_SIZE)
     * @return a list of audition comments
     */
    List<AuditionComment> getComments(
            @Positive Integer postId,
            @Min(0) Integer page,
            @Positive @Max(MAX_PAGE_SIZE) Integer size
    );

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param commentId the ID of the comment to retrieve (must be positive)
     * @return the requested AuditionComment or null if not found
     */
    AuditionComment getComment(@Positive Integer commentId);
}
