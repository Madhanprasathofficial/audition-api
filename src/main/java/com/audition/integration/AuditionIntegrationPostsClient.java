package com.audition.integration;

import com.audition.model.AuditionPost;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of IAuditionIntegrationPostsClient to interact with external posts service.
 */
@Component
public class AuditionIntegrationPostsClient implements IAuditionIntegrationPostsClient {


    private final transient RestTemplate restTemplate = new RestTemplate();
    private final transient IIntegrationUrlService integrationUrlService;
    private final transient IAuditionIntegrationCommentsClient auditionIntegrationCommentsClient;

    public AuditionIntegrationPostsClient(final IAuditionIntegrationCommentsClient auditionIntegrationCommentsClient,
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
     */
    @Override
    public List<AuditionPost> getPosts(final Integer userId, final Integer page, final Integer size) {
        final String url = integrationUrlService.getPostsUrl(userId, page, size);
        final ResponseEntity<AuditionPost[]> responseEntity = restTemplate.getForEntity(url, AuditionPost[].class);

        // Safely return an empty list if the response body is null
        return Optional.ofNullable(responseEntity.getBody())
                .map(Arrays::asList) // Convert array to List if body is not null
                .orElseGet(List::of); // Return an empty list if body is null
    }



    /**
     * Retrieves a specific audition post by its ID.
     *
     * @param id             the ID of the post (must be positive)
     * @param loadComments   whether to include comments in the response
     * @param page           the page number for pagination of comments (if included, must be non-negative)
     * @param size           the number of comments per page (if included, must be positive)
     * @return the requested audition post, or null if not found
     */
    @Override
    public AuditionPost getPostById(final Integer id, final boolean loadComments, final Integer page, final Integer size) {
        final String postUrl = integrationUrlService.getPostByIdUrl(id);
        final ResponseEntity<AuditionPost> responseEntity = restTemplate.getForEntity(postUrl, AuditionPost.class);
        final AuditionPost auditionPost = responseEntity.getBody();

        if (loadComments && Objects.nonNull(auditionPost)) {
            auditionPost.setAuditionComments(auditionIntegrationCommentsClient.getComments(auditionPost.getId(), page, size));
        }
        return auditionPost;
    }
}
