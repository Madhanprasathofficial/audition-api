package com.audition.service;

import com.audition.model.AuditionPost;
import jakarta.validation.constraints.Positive;

import java.util.List;

/**
 * Service interface for managing audition posts.
 */
public interface IAuditionPostService {

    /**
     * Retrieves a paginated list of audition posts for a specific user.
     *
     * @param userId the ID of the user (nullable)
     * @param page   the page number to retrieve (0-based)
     * @param size   the number of posts per page
     * @return a list of audition posts
     */
    List<AuditionPost> getPosts(Integer userId, Integer page, Integer size);

    /**
     * Retrieves a specific audition post by its ID.
     *
     * @param postId         the ID of the post to retrieve
     * @param includeComments whether to include comments in the response
     * @param page           the page number for pagination (if comments are included)
     * @param size           the number of comments per page (if comments are included)
     * @return the requested audition post, or null if not found
     */
    AuditionPost getPostById(Integer postId, boolean includeComments, Integer page, Integer size);

}

