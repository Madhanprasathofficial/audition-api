package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.dto.ErrorResponse;
import com.audition.service.IAuditionPostService;
import com.audition.model.AuditionPost;
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

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller for managing and retrieving audition posts.

 * This class contains APIs to retrieve all posts, get a post by ID,
 * and retrieve a post along with its comments by ID. The controller also
 * handles basic validation and error handling related to pagination and
 * post retrieval.
 */
@Tag(name = "Posts Controller", description = "APIs for retrieving posts and comments.")
@RestController
@Validated
@RequestMapping("/posts")
public class AuditionPostsController {

    // Constants for default pagination values
    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_PAGE_SIZE = "100";
    private static final long MAX_PAGE_SIZE = 100L;

    // Constants for log messages
    private static final String INVALID_USER_ID_MESSAGE = "Invalid User ID";
    private static final String INVALID_USER_ID_ERROR_MESSAGE = "User ID must be positive";
    private static final String SUCCESSFUL_RETRIEVAL_MESSAGE = "Successfully retrieved posts";
    private static final String GET_POST_BY_ID_MESSAGE = "Received request to get post by ID: {}";
    private static final String POST_NOT_FOUND_ERROR = "Post not found";
    private static final String PAGE_NOT_FOUND_ERROR = "Page not found";

    // Constant for error responses
    private static final AuditionPost INVALID_USER_ID_RESPONSE = new
            AuditionPost(0, 0, INVALID_USER_ID_MESSAGE, INVALID_USER_ID_ERROR_MESSAGE, Collections.emptyList());

    private final transient IAuditionPostService auditionPostService;
    private final transient AuditionLogger auditionLogger;
    private static final Logger POSTS_LOGGER = LoggerFactory.getLogger(AuditionPostsController.class);

    /**
     * Constructor for AuditionPostsController.
     *
     * @param auditionPostService The service layer for managing audition posts.
     * @param auditionLogger      Logger to log activity within the controller.
     */
    public AuditionPostsController(final IAuditionPostService auditionPostService, final AuditionLogger auditionLogger) {
        this.auditionPostService = auditionPostService;
        this.auditionLogger = auditionLogger;
    }

    /**
     * Retrieve all audition posts, with optional filtering by user ID and title.
     * Supports pagination, with page number and size specified as request parameters.
     *
     * @param page   The page number (zero-based) for pagination, default is 0.
     * @param size   The page size (number of items), default is 100, max is 100.
     * @param userId Optional user ID to filter posts by user.
     * @param title  Optional post title filter.
     * @return A ResponseEntity containing a list of filtered posts.
     */
    @Operation(summary = "Get all posts", description = "Retrieve all posts with optional filtering by user ID and title.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionPost>> getPosts(
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE) @Min(0) final Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Positive @Max(MAX_PAGE_SIZE) final Integer size,
            @RequestParam(required = false) final Optional<Integer> userId,
            @RequestParam(required = false) final Optional<String> title) {

        // Validate userId if present
        if (userId.isPresent() && userId.get() <= 0) {
            auditionLogger.debug(POSTS_LOGGER, "Invalid userId provided");
            return ResponseEntity.badRequest().body(Collections.singletonList(INVALID_USER_ID_RESPONSE));
        }

        // Fetch posts with pagination and optional filtering
        List<AuditionPost> posts = auditionPostService.getPosts(userId.orElse(null), page, size);
        final String filterTitle = title.map(String::toLowerCase).orElse("");
        if (!filterTitle.isEmpty()) {
            posts = posts.stream()
                    .filter(post -> post.getTitle() != null
                            && post.getTitle().toLowerCase(Locale.ENGLISH).contains(filterTitle))
                    .toList();
        }

        auditionLogger.debug(POSTS_LOGGER, SUCCESSFUL_RETRIEVAL_MESSAGE);
        return ResponseEntity.ok(posts);
    }

    /**
     * Retrieve a specific post by its ID.

     * If the post does not exist, returns a 204 NO CONTENT status.
     *
     * @param postId The ID of the post to retrieve.
     * @return A ResponseEntity containing the post or an error response if not found.
     */
    @Operation(summary = "Get post by ID", description = "Retrieve a specific post by its ID.")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPostById(
            @Parameter(description = "ID of the post") @PathVariable("id") @Positive final Integer postId) {

        auditionLogger.info(POSTS_LOGGER, GET_POST_BY_ID_MESSAGE, postId);
        final AuditionPost post = auditionPostService.getPostById(postId, false, Integer.parseInt(DEFAULT_PAGE), Integer.parseInt(DEFAULT_PAGE_SIZE));

        return Objects.isNull(post)
                ? ResponseEntity.status(HttpStatus.NO_CONTENT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorResponse(PAGE_NOT_FOUND_ERROR))
                : ResponseEntity.ok(post);
    }

    /**
     * Retrieve a specific post by its ID along with its comments.

     * If the post or its comments are not found, returns a 404 NOT FOUND status.
     *
     * @param postId The ID of the post to retrieve along with comments.
     * @param page   The page number (zero-based) for pagination, default is 0.
     * @param size   The page size (number of items), default is 100, max is 100.
     * @return A ResponseEntity containing the post with comments or an error response if not found.
     */
    @Operation(summary = "Get post with comments", description = "Retrieve a specific post by its ID along with comments.")
    @GetMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPostWithComments(
            @Parameter(description = "ID of the post")
            @PathVariable("id") @Positive final Integer postId,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE)
            @Min(0) final Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Positive @Max(MAX_PAGE_SIZE) final Integer size) {

        auditionLogger.info(POSTS_LOGGER, "Received request to get post with comments for post ID: {}", postId);
        final AuditionPost postWithComments = auditionPostService.getPostById(postId, true, page, size);

        return Objects.isNull(postWithComments)
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(POST_NOT_FOUND_ERROR))
                : ResponseEntity.ok(postWithComments);
    }
}
