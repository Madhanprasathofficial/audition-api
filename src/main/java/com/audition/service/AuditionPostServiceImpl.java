package com.audition.service;

import com.audition.common.exception.NoDataFoundException;
import com.audition.integration.IIntegrationUrlService;
import com.audition.model.AuditionPost;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of IAuditionIntegrationPostsClient to interact with external posts service.
 */
@Component
public class AuditionPostServiceImpl implements IAuditionPostService {

    private final transient RestTemplate restTemplate = new RestTemplate();
    private final transient IIntegrationUrlService integrationUrlService;
    private final transient IAuditionCommentsService auditionIntegrationCommentsClient;

    // Constants for error messages
    private static final String NO_POST_FOUND_MESSAGE = "No post found for ID: ";
    private static final String NO_POSTS_FOUND_MESSAGE = "No posts found for user ID: ";
    private static final String FETCHING_POSTS_ERROR_MESSAGE = "An error occurred while fetching posts";

    public AuditionPostServiceImpl(final IAuditionCommentsService auditionIntegrationCommentsClient,
                                   final IIntegrationUrlService integrationUrlService) {
        this.auditionIntegrationCommentsClient = auditionIntegrationCommentsClient;
        this.integrationUrlService = integrationUrlService;
    }

    /**
     * Retrieves a list of audition posts for a specific user with pagination support.
     *
     * @param userId the ID of the user (nullable)
     * @param page   the page number to retrieve (0-based)
     * @param size   the number of posts per page (must be positive)
     * @return a list of audition posts, or an empty list if none are found
     * @throws NoDataFoundException if no posts are found for the given user ID
     */
    @Override
    public List<AuditionPost> getPosts(final Integer userId, final Integer page, final Integer size) {
        final String url = integrationUrlService.getPostsUrl(userId, page, size);

        try {
            final ResponseEntity<AuditionPost[]> responseEntity = restTemplate.getForEntity(url, AuditionPost[].class);
            return Optional.ofNullable(responseEntity.getBody())
                    .map(Arrays::asList) // Convert array to List if body is not null
                    .orElseGet(List::of); // Return an empty list if body is null

        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NoDataFoundException(NO_POSTS_FOUND_MESSAGE + userId, HttpStatus.NOT_FOUND.value());
            }
            throw new HttpClientErrorException(exception.getStatusCode(), exception.getMessage());
        } catch (RestClientException exception) {
            throw new RestClientException(FETCHING_POSTS_ERROR_MESSAGE, exception);
        }
    }

    /**
     * Retrieves a specific audition post by its ID.
     *
     * @param id             the ID of the post (must be positive)
     * @param loadComments   whether to include comments in the response
     * @param page           the page number for pagination of comments (if included, must be non-negative)
     * @param size           the number of comments per page (if included, must be positive)
     * @return the requested audition post, or null if not found
     * @throws NoDataFoundException if no post is found for the given ID
     */
    @Override
    public AuditionPost getPostById(final Integer id, final boolean loadComments, final Integer page, final Integer size) {
        final String postUrl = integrationUrlService.getPostByIdUrl(id);

        try {
            final ResponseEntity<AuditionPost> responseEntity = restTemplate.getForEntity(postUrl, AuditionPost.class);
            return Optional.ofNullable(responseEntity.getBody())
                    .map(auditionPost -> {
                        if (loadComments) {
                            auditionPost.setAuditionComments(auditionIntegrationCommentsClient.getComments(auditionPost.getId(), page, size));
                        }
                        return auditionPost;
                    })
                    .orElseThrow(() -> new NoDataFoundException(NO_POST_FOUND_MESSAGE + id, HttpStatus.NOT_FOUND.value()));

        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NoDataFoundException(NO_POST_FOUND_MESSAGE + id, HttpStatus.NOT_FOUND.value());
            }
            throw new HttpClientErrorException(exception.getStatusCode(), exception.getMessage());
        }
    }
}
