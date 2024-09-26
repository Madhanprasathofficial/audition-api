package com.audition.service;

import com.audition.model.AuditionComment;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface IAuditionCommentsService {
    List<AuditionComment> getComments(@Positive Integer postId, @Min(0) Integer page, @Positive @Max(MAX_PAGE_SIZE) Integer size);

    AuditionComment getComment(@Positive Integer postId, @Positive Integer commentId);

    List<AuditionComment> fetchCommentsForPost(@Positive Integer postId, boolean b, @Min(0) Integer page, @Positive @Max(MAX_PAGE_SIZE) Integer size);
}
