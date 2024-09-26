package com.audition.web;

import com.audition.model.AuditionPost;
import com.audition.model.AuditionComment;
import com.audition.service.IAuditionPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Posts Controller", description = "APIs for retrieving posts and comments.")
@RestController
@Validated
@RequestMapping("/posts")
public class AuditionPostsController {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_PAGE_SIZE = "100";
    private static final long MAX_PAGE_SIZE = 100L;

    private final IAuditionPostService iAuditionPostService;

    public AuditionPostsController(final IAuditionPostService iAuditionPostService) {
        this.iAuditionPostService = iAuditionPostService;
    }

    /**
     * Retrieve paginated list of posts with optional filtering by userId and title.
     *
     * @param page   the page number (default 0)
     * @param size   the page size (default 100)
     * @param userId optional filter by user ID
     * @param title  optional filter by post title
     * @return a list of filtered or paginated AuditionPosts
     */
    @Operation(summary = "Get all posts", description = "Retrieve all posts with optional filtering by user ID and title.")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionPost>> getPosts(
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE) @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Positive @Max(MAX_PAGE_SIZE) Integer size,
            @RequestParam(required = false) @Positive Optional<Integer> userId,
            @RequestParam(required = false) Optional<String> title) {

        // Fetch posts from the service layer with pagination and optional filtering
        List<AuditionPost> posts = iAuditionPostService.getPosts(userId.orElse(null), page, size);

        // Filter by title if provided
        if (title.isPresent() && !title.get().isEmpty()) {
            posts = posts.stream()
                    .filter(post -> post.getTitle().toLowerCase().contains(title.get().toLowerCase()))
                    .toList();
        }

        return ResponseEntity.ok(posts);
    }

    /**
     * Retrieve a post by its ID.
     *
     * @param postId the ID of the post
     * @return the AuditionPost or error message if not found
     */
    @Operation(summary = "Get post by ID", description = "Retrieve a specific post by its ID.")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditionPost> getPostById(
            @Parameter(description = "ID of the post") @PathVariable("id") @Positive Integer postId) {

        // Fetch post by ID from the service layer
        AuditionPost post = iAuditionPostService.getPostById(postId, false, Integer.parseInt(DEFAULT_PAGE), Integer.parseInt(DEFAULT_PAGE_SIZE));

        // Handle post not found scenario
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Or custom error message as JSON
        }

        return ResponseEntity.ok(post);
    }

    /**
     * Retrieve a post along with its comments.
     *
     * @param postId the ID of the post
     * @param page   the page number for comments
     * @param size   the number of comments per page
     * @return the AuditionPost with its comments
     */
    @Operation(summary = "Get post with comments", description = "Retrieve a specific post by its ID along with comments.")
    @GetMapping(value = "/{id}/comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AuditionPost> getPostWithComments(
            @Parameter(description = "ID of the post") @PathVariable("id") @Positive Integer postId,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE) @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Positive @Max(MAX_PAGE_SIZE) Integer size) {

        // Fetch post with comments by ID from the service layer
        AuditionPost postWithComments = iAuditionPostService.getPostById(postId, true, page, size);

        // Handle post not found scenario
        if (postWithComments == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null); // Or custom error message as JSON
        }

        return ResponseEntity.ok(postWithComments);
    }

}
