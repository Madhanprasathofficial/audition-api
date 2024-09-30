package com.audition.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Service for constructing integration URLs for external API calls.
 * This class provides methods to generate URLs for posts and comments
 * based on user and pagination parameters.
 */
@Component
public class IntegrationUrlService implements IIntegrationUrlService {

    private static final String POSTS_ENDPOINT = "/posts";
    private static final String COMMENTS_ENDPOINT = "/comments";
    private static final String USER_ID = "&userId=";
    private static final String POST_ID = "&postId=";
    private static final String START = "&_start=";
    private static final String LIMIT = "&_limit=";

    private final transient String baseUrl;

    /**
     * Constructs an IntegrationUrlService with the specified base URL.
     *
     * @param baseUrl the base URL for the integration service
     */
    public IntegrationUrlService(@Value("${application.config.baseUrl}") final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /**
     * Gets the URL for retrieving posts, including pagination and user parameters.
     *
     * @param userId the ID of the user (optional)
     * @param page   the page number for pagination (optional)
     * @param size   the size of the page (optional)
     * @return the constructed URL for retrieving posts
     */
    @Override
    public String getPostsUrl(final Integer userId, final Integer page, final Integer size) {
        return baseUrl + POSTS_ENDPOINT + paginationParameters(page, size) + userParameter(userId);
    }

    /**
     * Gets the URL for retrieving a specific post by its ID.
     *
     * @param id the ID of the post
     * @return the constructed URL for retrieving the post
     */
    @Override
    public String getPostByIdUrl(final Integer id) {
        return baseUrl + POSTS_ENDPOINT + "/" + id;
    }

    /**
     * Gets the URL for retrieving comments, including pagination and post parameters.
     *
     * @param postId the ID of the post to retrieve comments for
     * @param page   the page number for pagination (optional)
     * @param size   the size of the page (optional)
     * @return the constructed URL for retrieving comments
     */
    @Override
    public String getCommentsUrl(final int postId, final Integer page, final Integer size) {
        return baseUrl + COMMENTS_ENDPOINT + paginationParameters(page, size) + postParameter(postId);
    }

    /**
     * Gets the URL for retrieving a specific comment by its ID.
     *
     * @param commentId the ID of the comment
     * @return the constructed URL for retrieving the comment
     */
    @Override
    public String getCommentUrl(final int commentId) {
        return baseUrl + COMMENTS_ENDPOINT + "/" + commentId;
    }

    /**
     * Constructs pagination parameters for the URL.
     *
     * @param page the page number (optional)
     * @param size the size of the page (optional)
     * @return the pagination parameters as a string
     */
    private String paginationParameters(final Integer page, final Integer size) {
        final var sb = new StringBuilder("?");
        if (Objects.nonNull(page)) {
            sb.append(START).append(page);
        }
        if (Objects.nonNull(size)) {
            sb.append(LIMIT).append(size);
        }
        return sb.toString();
    }

    /**
     * Constructs the user ID parameter for the URL.
     *
     * @param userId the ID of the user (optional)
     * @return the user ID parameter as a string, or an empty string if null
     */
    private String userParameter(final Integer userId) {
        return Objects.nonNull(userId) ? USER_ID + userId : "";
    }

    /**
     * Constructs the post ID parameter for the URL.
     *
     * @param postId the ID of the post (optional)
     * @return the post ID parameter as a string, or an empty string if null
     */
    private String postParameter(final Integer postId) {
        return Objects.nonNull(postId) ? POST_ID + postId : "";
    }
}
