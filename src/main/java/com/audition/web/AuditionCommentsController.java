package com.audition.web;

import com.audition.common.logging.AuditionLogger;
import com.audition.dto.ErrorResponse;
import com.audition.model.AuditionComment;
import com.audition.service.IAuditionCommentsService;
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

@Tag(name = "Comments Controller", description = "Retrieve and manage comments")
@RestController
@Validated
@RequestMapping("comments")
public class AuditionCommentsController {

    public static final String DEFAULT_PAGE = "0";
    public static final String DEFAULT_PAGE_SIZE = "100";
    public static final long MAX_PAGE_SIZE = 100L;

    private final transient IAuditionCommentsService auditionCommentsService;
    private final transient AuditionLogger auditionLogger;
    private static final Logger COMMENTS_LOGGER = LoggerFactory.getLogger(AuditionCommentsController.class);

    public AuditionCommentsController(final IAuditionCommentsService auditionCommentsService, final AuditionLogger auditionLogger) {
        this.auditionCommentsService = auditionCommentsService;
        this.auditionLogger = auditionLogger;
    }

    @Operation(summary = "Get comments for a specific post")
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AuditionComment>> getComments(
            @Parameter(description = "ID of the post")
            @RequestParam @Positive final Integer postId,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE)
            @Min(0) final Integer page,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Positive @Max(MAX_PAGE_SIZE) final Integer size) {

        auditionLogger.info(COMMENTS_LOGGER, "Fetching comments for post ID: {}", postId);

        final List<AuditionComment> comments = auditionCommentsService.getComments(postId, page, size);
        COMMENTS_LOGGER.info("Retrieved  comments for post ID: {}", postId);
        return ResponseEntity.ok(comments);
    }

    @Operation(summary = "Get a specific comment by its ID")
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getCommentById(
            @Parameter(description = "ID of the comment")
            @PathVariable("id") @Positive final Integer commentId) {

        COMMENTS_LOGGER.info("Fetching comment by ID: {}", commentId);
        final AuditionComment comment = auditionCommentsService.getComment(commentId);

        if (Objects.nonNull(comment)) {
            COMMENTS_LOGGER.info("Retrieved comment: {}", comment);
            return ResponseEntity.ok(comment);
        } else {
            COMMENTS_LOGGER.warn("Comment not found for ID: {}", commentId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Comment not found"));
        }
    }
}
