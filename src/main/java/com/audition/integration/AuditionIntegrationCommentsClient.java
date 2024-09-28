package com.audition.integration;

import com.audition.model.AuditionComment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of IAuditionIntegrationCommentsClient to interact with external comments service.
 */
@Component
public class AuditionIntegrationCommentsClient implements IAuditionIntegrationCommentsClient {

    private final transient RestTemplate restTemplate = new RestTemplate();
    private final transient IIntegrationUrlService iIntegrationUrlService;

    public AuditionIntegrationCommentsClient( IIntegrationUrlService iIntegrationUrlService) {


        this.iIntegrationUrlService = iIntegrationUrlService;
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
    public List<AuditionComment> getComments(int postId, Integer page, Integer size) {
        String commentUrl = iIntegrationUrlService.getCommentsUrl(postId, page, size);
        ResponseEntity<AuditionComment[]> responseEntity = restTemplate.getForEntity(commentUrl, AuditionComment[].class);

        // Use Optional to safely handle null values
        return Optional.ofNullable(responseEntity)
                .map(ResponseEntity::getBody)  // Get the body from the response
                .map(Arrays::asList)  // Convert to List if body is not null
                .orElse(List.of());  // Return an empty list if the body is null
    }

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param commentId the ID of the comment
     * @return the requested audition comment, or null if not found
     */
    @Override
    public AuditionComment getComment(Integer commentId) {
        String commentUrl = iIntegrationUrlService.getCommentUrl(commentId);
        ResponseEntity<AuditionComment> responseEntity = restTemplate.getForEntity(commentUrl, AuditionComment.class);

        // Use Optional to safely handle null values and return null if the body is null
        return Optional.ofNullable(responseEntity)
                .map(ResponseEntity::getBody)
                .orElse(null);
    }

}
