package com.audition.service;

import com.audition.model.AuditionPost;

import java.util.List;

public interface IAuditionPostService {
    List<AuditionPost> getPosts(Integer userId, Integer page, Integer size);
    AuditionPost getPostById(Integer postId, boolean includeComments, Integer page, Integer size);
}
