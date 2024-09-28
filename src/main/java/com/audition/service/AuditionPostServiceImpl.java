package com.audition.service;

import com.audition.integration.IAuditionIntegrationPostsClient;
import com.audition.model.AuditionPost;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of the IAuditionPostService interface.
 */
@Service
public class AuditionPostServiceImpl implements IAuditionPostService {

    private final transient IAuditionIntegrationPostsClient iAuditionIntegrationPostsClient;

    // Constructor to initialize the integration client
    public AuditionPostServiceImpl(final IAuditionIntegrationPostsClient iAuditionIntegrationPostsClient) {
        this.iAuditionIntegrationPostsClient = iAuditionIntegrationPostsClient;
    }

    /**
     * Retrieves a list of audition posts for a specific user with pagination support.
     *
     * @param userId the ID of the user (nullable)
     * @param page   the page number to retrieve (0-based, must be non-negative)
     * @param size   the number of posts per page (must be positive)
     * @return a list of audition posts (empty list if no posts exist)
     */
    @Override
    public List<AuditionPost> getPosts(
            Integer userId,
            @Min(0) Integer page,
            @Positive Integer size) {
        return iAuditionIntegrationPostsClient.getPosts(userId, page, size);
    }

    /**
     * Retrieves a specific audition post by its ID.
     *
     * @param postId         the ID of the post to retrieve
     * @param includeComments whether to include comments in the response
     * @param page           the page number for pagination (if comments are included)
     * @param size           the number of comments per page (if comments are included)
     * @return the requested audition post, or null if not found
     */
    @Override
    public AuditionPost getPostById(
            Integer postId,
            boolean includeComments,
            Integer page,
            Integer size) {
        return iAuditionIntegrationPostsClient.getPostById(postId, includeComments, page, size);
    }
}
