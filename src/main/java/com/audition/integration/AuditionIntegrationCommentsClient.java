package com.audition.integration;

import com.audition.model.AuditionComment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of IAuditionIntegrationCommentsClient to interact with external comments service.
 */
@Component
public class AuditionIntegrationCommentsClient implements IAuditionIntegrationCommentsClient {

    private final transient RestTemplate restTemplate;
    private final transient IIntegrationUrlService integrationUrlService;

    public AuditionIntegrationCommentsClient(final IIntegrationUrlService integrationUrlService) {
        this.integrationUrlService = integrationUrlService;
        this.restTemplate = new RestTemplate(); // Moved initialization to constructor
    }

    /**
     * Retrieves a list of comments for a specific post with pagination support.
     *
     * @param postId the ID of the post (must be positive)
     * @param page   the page number to retrieve (must be non-negative)
     * @param size   the number of comments per page (must be positive)
     * @return a list of audition comments, or an empty list if none are found
     */
    @Override
    public List<AuditionComment> getComments(final int postId, final Integer page, final Integer size) {
        final String commentUrl = integrationUrlService.getCommentsUrl(postId, page, size);
        final ResponseEntity<AuditionComment[]> responseEntity = restTemplate.getForEntity(commentUrl, AuditionComment[].class);

        // Use Optional to safely handle null values
        return Optional.ofNullable(responseEntity)
                .map(ResponseEntity::getBody)
                .map(Arrays::asList)
                .orElse(Collections.emptyList()); // Changed to Collections.emptyList() for clarity
    }

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param commentId the ID of the comment
     * @return the requested audition comment, or null if not found
     */
    @Override
    public AuditionComment getComment(final Integer commentId) {
        final String commentUrl = integrationUrlService.getCommentUrl(commentId);
        final ResponseEntity<AuditionComment> responseEntity = restTemplate.getForEntity(commentUrl, AuditionComment.class);

        // Use Optional to safely handle null values and return null if the body is null
        return Optional.ofNullable(responseEntity)
                .map(ResponseEntity::getBody)
                .orElse(null);
    }
}
