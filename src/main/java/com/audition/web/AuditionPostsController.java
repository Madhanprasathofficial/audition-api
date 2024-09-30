package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.dto.ErrorResponse;
import com.audition.model.AuditionPost;
import com.audition.service.IAuditionPostService;
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
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Tag(name = "Posts Controller", description = "APIs for retrieving posts and comments.")
@RestController
@Validated
@RequestMapping("/posts")
public class AuditionPostsController {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_PAGE_SIZE = "100";
    private static final long MAX_PAGE_SIZE = 100L;

    private final IAuditionPostService iAuditionPostService;
    private final AuditionLogger auditionLogger;
    private final Logger logger = LoggerFactory.getLogger(AuditionPostsController.class);

    public AuditionPostsController(final IAuditionPostService iAuditionPostService, AuditionLogger auditionLogger) {
        this.iAuditionPostService = iAuditionPostService;
        this.auditionLogger = auditionLogger;
    }

    @Operation(summary = "Get all posts", description = "Retrieve all posts with optional filtering by user ID and title.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionPost>> getPosts(
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE)
            @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Positive @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) Integer userId,
            @RequestParam(required = false) Optional<String> title) {

        auditionLogger.info(logger, "Received request to get posts with userId: {}", userId);

        // Validate userId
        if (userId != null && userId <= 0) {
            List<AuditionPost> errorResponse = new ArrayList<>();
            errorResponse.add(new AuditionPost(0, 0, "Invalid User ID", "User ID must be positive", Collections.emptyList()));
            auditionLogger.warn(logger, "Invalid userId provided: {}", userId);
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Fetch posts from the service layer with pagination and optional filtering
        List<AuditionPost> posts = iAuditionPostService.getPosts(userId, page, size);
        String filterTitle = title.map(String::toLowerCase).orElse("");
        if (!filterTitle.isEmpty()) {
            posts = posts.stream()
                    .filter(post -> post.getTitle() != null &&
                            post.getTitle().toLowerCase().contains(filterTitle))
                    .toList();
        }

        auditionLogger.info(logger, "Successfully retrieved {} posts", posts.size());
        return ResponseEntity.ok(posts);
    }

    @Operation(summary = "Get post by ID", description = "Retrieve a specific post by its ID.")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPostById(
            @Parameter(description = "ID of the post")
            @PathVariable("id") @Positive Integer postId) {

        auditionLogger.info(logger, "Received request to get post by ID: {}", postId);
        AuditionPost post = iAuditionPostService.getPostById(postId, false, Integer.parseInt(DEFAULT_PAGE), Integer.parseInt(DEFAULT_PAGE_SIZE));

        return (Objects.isNull(post)) ?
                ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(new ErrorResponse("Page not found")) :
                ResponseEntity.ok(post);
    }

    @Operation(summary = "Get post with comments", description = "Retrieve a specific post by its ID along with comments.")
    @GetMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPostWithComments(
            @Parameter(description = "ID of the post")
            @PathVariable("id") @Positive Integer postId,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE)
            @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Positive @Max(MAX_PAGE_SIZE) Integer size) {

        auditionLogger.info(logger, "Received request to get post with comments for post ID: {}", postId);
        AuditionPost postWithComments = iAuditionPostService.getPostById(postId, true, page, size);

        return Objects.isNull(postWithComments) ?
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Post not found")) :
                ResponseEntity.ok(postWithComments);
    }
}
