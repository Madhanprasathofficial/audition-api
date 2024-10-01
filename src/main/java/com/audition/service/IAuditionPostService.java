package com.audition.service;

import com.audition.model.AuditionPost;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Interface for interacting with the external posts integration service.
 */
public interface IAuditionPostService {

    /**
     * Retrieves a list of audition posts for a specific user with pagination support.
     *
     * @param userId the ID of the user (nullable)
     * @param page   the page number to retrieve (0-based, must be non-negative)
     * @param size   the number of posts per page (must be positive)
     * @return a list of audition posts (empty list if no posts exist)
     */
    List<AuditionPost> getPosts(Integer userId, @Min(0) Integer page, @Positive Integer size);

    /**
     * Retrieves a specific audition post by its ID.
     *
     * @param postId         the ID of the post to retrieve (must be positive)
     * @param includeComments whether to include comments in the response
     * @param page           the page number for pagination of comments (if included, must be non-negative)
     * @param size           the number of comments per page (if included, must be positive)
     * @return the requested audition post, or null if not found
     */
    AuditionPost getPostById(@Positive Integer postId, boolean includeComments, @Min(0) Integer page, @Positive Integer size);

}
