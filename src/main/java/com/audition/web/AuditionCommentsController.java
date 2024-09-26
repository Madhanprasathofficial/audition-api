package com.audition.web;

import com.audition.model.AuditionComment;
import com.audition.service.IAuditionCommentsService;
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

@Tag(
        name = "Comments Controller",
        description = "Retrieve and manage comments"
)
@RestController
@Validated
@RequestMapping("posts/{postId}/comments")
public class AuditionCommentsController {

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "100";
    public static final long MAX_PAGE_SIZE = 100L;

    private final IAuditionCommentsService iAuditionCommentsService;

    public AuditionCommentsController(final IAuditionCommentsService iAuditionCommentsService) {
        this.iAuditionCommentsService = iAuditionCommentsService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody List<AuditionComment> getComments(
            @PathVariable @Positive Integer postId,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE) @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Positive @Max(MAX_PAGE_SIZE) Integer size) {
        return iAuditionCommentsService.getComments(postId, page, size);
    }

    @GetMapping(value = "/{commentId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody AuditionComment getCommentById(
            @PathVariable @Positive Integer postId,
            @PathVariable("commentId") @Positive Integer commentId) {
        return iAuditionCommentsService.getComment(postId, commentId);
    }

    /**
     * Retrieve external comments for a post by its ID.
     *
     * @param postId the ID of the post
     * @param page   the page number for comments
     * @param size   the number of comments per page
     * @return List of AuditionComments from an external source
     */
    @Operation(summary = "Get external comments", description = "Retrieve external comments for a post by its ID.")
    @GetMapping(value = "/{id}/external-comments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionComment>> getExternalComments(
            @Parameter(description = "ID of the post") @PathVariable("id") @Positive Integer postId,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE) @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE) @Positive @Max(MAX_PAGE_SIZE) Integer size) {

        // Fetch external comments for the post
        List<AuditionComment> comments = iAuditionCommentsService.fetchCommentsForPost(postId,true, page, size);

        if (comments.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        return ResponseEntity.ok(comments);
    }

    // Additional methods could be added here for creating, updating, and deleting comments
}
