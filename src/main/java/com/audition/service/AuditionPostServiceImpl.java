package com.audition.service;

import com.audition.integration.AuditionIntegrationClient;
import com.audition.model.AuditionComment;
import com.audition.model.AuditionPost;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditionPostServiceImpl implements IAuditionPostService {

    @Autowired
    private AuditionIntegrationClient auditionIntegrationClient;


    public List<AuditionPost> getPosts() {
        return auditionIntegrationClient.getPosts();
    }

    public AuditionPost getPostById(final String postId) {
        return auditionIntegrationClient.getPostById(postId);
    }

    @Override
    public List<AuditionPost> getPosts(Integer userId, Integer page, Integer size) {
        return List.of();
    }

    @Override
    public AuditionPost getPostById(Integer postId, boolean includeComments, Integer page, Integer size) {
        return null;
    }

    public List<AuditionComment> fetchExternalCommentsForPost(@Positive Integer postId, @Min(0) Integer page, @Positive @Max(MAX_PAGE_SIZE) Integer size) {
    }
}
