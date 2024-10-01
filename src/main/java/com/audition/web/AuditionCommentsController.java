package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.dto.ErrorResponse;
import com.audition.service.IAuditionCommentsService;
import com.audition.model.AuditionComment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

/**
 * Controller for managing and retrieving audition comments.

 * This class provides APIs for retrieving comments associated with specific posts
 * and fetching individual comments by their ID.
 */
@Tag(name = "Comments Controller", description = "Retrieve and manage comments")
@RestController
@Validated
@RequestMapping("comments")
public class AuditionCommentsController {

    // Constants for default pagination values
    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "100";
    public static final long MAX_PAGE_SIZE = 100L;

    // Constants for log messages
    private static final String FETCHING_COMMENTS_MESSAGE = "Fetching comments for post ID: {}";
    private static final String RETRIEVED_COMMENTS_MESSAGE = "Retrieved comments for post ID: {}";
    private static final String FETCHING_COMMENT_BY_ID_MESSAGE = "Fetching comment by ID: {}";
    private static final String RETRIEVED_COMMENT_MESSAGE = "Retrieved comment: {}";
    private static final String COMMENT_NOT_FOUND_MESSAGE = "Comment not found for ID: {}";

    // Constants for error response
    private static final String COMMENT_NOT_FOUND_ERROR = "Comment not found";

    private final transient IAuditionCommentsService auditionCommentsService;
    private final transient AuditionLogger auditionLogger;
    private static final Logger COMMENTS_LOGGER = LoggerFactory.getLogger(AuditionCommentsController.class);

    /**
     * Constructor for AuditionCommentsController.
     *
     * @param auditionCommentsService The service layer for managing audition comments.
     * @param auditionLogger          Logger to log activity within the controller.
     */
    public AuditionCommentsController(final IAuditionCommentsService auditionCommentsService, final AuditionLogger auditionLogger) {
        this.auditionCommentsService = auditionCommentsService;
        this.auditionLogger = auditionLogger;
    }

    /**
     * Retrieve all comments associated with a specific post ID.
     * Supports pagination, with page number and size specified as request parameters.
     *
     * @param postId The ID of the post whose comments need to be retrieved.
     * @param page   The page number (zero-based) for pagination, default is 0.
     * @param size   The page size (number of items), default is 100, max is 100.
     * @return A ResponseEntity containing a list of comments associated with the post ID.
     */
    @Operation(summary = "Get comments for a specific post")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionComment>> getComments(
            @Parameter(description = "ID of the post")
            @RequestParam @Positive final Integer postId,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE)
            @Min(0) final Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Positive @Max(MAX_PAGE_SIZE) final Integer size) {

        auditionLogger.info(COMMENTS_LOGGER, FETCHING_COMMENTS_MESSAGE, postId);

        final List<AuditionComment> comments = auditionCommentsService.getComments(postId, page, size);
        COMMENTS_LOGGER.info(RETRIEVED_COMMENTS_MESSAGE, postId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Retrieve a specific comment by its ID.
     * If the comment does not exist, returns a 404 NOT FOUND status.
     *
     * @param commentId The ID of the comment to retrieve.
     * @return A ResponseEntity containing the comment if found, or an error response if not found.
     */
    @Operation(summary = "Get a specific comment by its ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCommentById(
            @Parameter(description = "ID of the comment")
            @PathVariable("id") @Positive final Integer commentId) {

        COMMENTS_LOGGER.info(FETCHING_COMMENT_BY_ID_MESSAGE, commentId);
        final AuditionComment comment = auditionCommentsService.getComment(commentId);

        if (Objects.nonNull(comment)) {
            COMMENTS_LOGGER.info(RETRIEVED_COMMENT_MESSAGE, comment);
            return ResponseEntity.ok(comment);
        } else {
            COMMENTS_LOGGER.warn(COMMENT_NOT_FOUND_MESSAGE, commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(COMMENT_NOT_FOUND_ERROR));
        }
    }
}
