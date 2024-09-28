package com.audition.web;

import com.audition.dto.ErrorResponse;
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
import java.util.Objects;

@Tag(name = "Comments Controller", description = "Retrieve and manage comments")
@RestController
@Validated
@RequestMapping("comments")
public class AuditionCommentsController {

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "100";
    public static final long MAX_PAGE_SIZE = 100L;

    private final transient IAuditionCommentsService iAuditionCommentsService;

    public AuditionCommentsController(final IAuditionCommentsService iAuditionCommentsService) {
        this.iAuditionCommentsService = iAuditionCommentsService;
    }

    /**
     * Retrieves a list of comments for a specific audition post with pagination support.
     *
     * @param postId the ID of the post to retrieve comments for
     * @param page   the page number to retrieve (default is 0)
     * @param size   the number of comments per page (default is 100, max is 100)
     * @return a ResponseEntity containing a list of comments for the specified post
     */
    @Operation(summary = "Get comments for a specific post")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionComment>> getComments(
            @Parameter(description = "ID of the post")
            @RequestParam @Positive Integer postId,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE)
            @Min(0) Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Positive @Max(MAX_PAGE_SIZE) Integer size) {

        return ResponseEntity.ok(iAuditionCommentsService.getComments(postId, page, size));
    }

    /**
     * Retrieves a specific comment by its ID for a given audition post.
     *
     * @param commentId the ID of the comment to retrieve
     * @return a ResponseEntity containing the requested comment, or a 404 status if not found
     */
    @Operation(summary = "Get a specific comment by its ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCommentById(
            @Parameter(description = "ID of the comment")
            @PathVariable("id") @Positive Integer commentId) {

        AuditionComment comment = iAuditionCommentsService.getComment(commentId);

        return Objects.nonNull(comment) ?
                ResponseEntity.ok(comment) :
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Comment not found"));
    }
}
