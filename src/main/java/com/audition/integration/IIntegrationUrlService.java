package com.audition.integration;

/**
 * Interface for building URLs for integration with the audition service.
 */
public interface IIntegrationUrlService {

    /**
     * Constructs the URL to retrieve posts with optional user ID and pagination parameters.
     *
     * @param userId the ID of the user (nullable)
     * @param page   the page number for pagination (nullable, 0-based)
     * @param size   the number of posts per page (nullable)
     * @return the constructed URL as a String
     */
    String getPosts(Integer userId, Integer page, Integer size);

    /**
     * Constructs the URL to retrieve a specific post by its ID.
     *
     * @param id the ID of the post
     * @return the constructed URL as a String
     */
    String getPostById(Integer id);

    /**
     * Constructs the URL to retrieve comments for a specific post with pagination.
     *
     * @param postId the ID of the post for which comments are to be retrieved
     * @param page   the page number for pagination (nullable, 0-based)
     * @param size   the number of comments per page (nullable)
     * @return the constructed URL as a String
     */
    String getCommentsUrl(int postId, Integer page, Integer size);

    /**
     * Constructs the URL to retrieve comments for a specific post without pagination.
     *
     * @param postId the ID of the post for which comments are to be retrieved
     * @return the constructed URL as a String
     */
    String getCommentsUrl(int postId);
}
