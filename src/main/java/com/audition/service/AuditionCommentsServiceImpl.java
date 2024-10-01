package com.audition.service;

import com.audition.common.exception.NoDataFoundException;
import com.audition.integration.IIntegrationUrlService;
import com.audition.model.AuditionComment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link IAuditionCommentsService} to interact with an external comments service.
 * This service handles retrieval of audition comments for specific posts and individual comments.
 */
@Component
public class AuditionCommentsServiceImpl implements IAuditionCommentsService {

    private static final String NO_COMMENTS_FOUND_MESSAGE = "No comments found for post ID: ";
    private static final String NO_DATA_FOUND_MESSAGE = "No data found for comment ID: ";
    private static final String CLIENT_ERROR_MESSAGE = "Client error occurred while fetching comments: ";

    private final transient RestTemplate restTemplate;
    private final transient IIntegrationUrlService integrationUrlService;

    /**
     * Constructs an instance of {@link AuditionCommentsServiceImpl}.
     *
     * @param integrationUrlService the service to get integration URLs for comments
     */
    public AuditionCommentsServiceImpl(final IIntegrationUrlService integrationUrlService) {
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
     * @throws NoDataFoundException if no comments are found for the given post ID
     */
    @Override
    public List<AuditionComment> getComments(final int postId, final Integer page, final Integer size) {
        final String commentUrl = integrationUrlService.getCommentsUrl(postId, page, size);

        try {
            final ResponseEntity<AuditionComment[]> responseEntity = restTemplate.getForEntity(commentUrl, AuditionComment[].class);
            return Optional.ofNullable(responseEntity.getBody())
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList());

        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NoDataFoundException(NO_COMMENTS_FOUND_MESSAGE + postId, HttpStatus.NOT_FOUND.value());
            }
            throw new RuntimeException(CLIENT_ERROR_MESSAGE + exception.getMessage(), exception);
        }
    }

    /**
     * Retrieves a specific comment by its ID.
     *
     * @param commentId the ID of the comment
     * @return the requested audition comment
     * @throws NoDataFoundException if no comment is found for the given ID
     */
    @Override
    public AuditionComment getComment(final Integer commentId) {
        final String commentUrl = integrationUrlService.getCommentUrl(commentId);

        try {
            final ResponseEntity<AuditionComment> responseEntity = restTemplate.getForEntity(commentUrl, AuditionComment.class);
            return Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new NoDataFoundException(NO_DATA_FOUND_MESSAGE + commentId, HttpStatus.NO_CONTENT.value()));

        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NoDataFoundException(NO_DATA_FOUND_MESSAGE + commentId, HttpStatus.NOT_FOUND.value());
            }
            throw new RuntimeException(CLIENT_ERROR_MESSAGE + exception.getMessage(), exception);
        }
    }
}
