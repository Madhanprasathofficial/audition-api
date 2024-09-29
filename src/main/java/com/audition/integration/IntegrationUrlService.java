package com.audition.integration;

import com.audition.model.AuditionComment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class IntegrationUrlService implements IIntegrationUrlService {

    private static final String POSTS_ENDPOINT = "/posts";
    private static final String COMMENTS_ENDPOINT = "/comments";
    private static final String USER_ID= "&userId=";
    private static final String POST_ID= "&postId=";
    private static final String START = "&_start=";

    private static final String LIMIT = "&_limit=";

    private final String baseUrl;

    public IntegrationUrlService(@Value("${application.config.baseUrl}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getPostsUrl(Integer userId, Integer page, Integer size) {
       return baseUrl + POSTS_ENDPOINT+ paginationParameters(page,size)+userParameter(userId);
    }

    @Override
    public String getPostByIdUrl(Integer id) {
        return baseUrl + POSTS_ENDPOINT + "/" + id;
    }

    @Override
    public String getCommentsUrl(int postId, Integer page, Integer size) {
       return baseUrl+ COMMENTS_ENDPOINT +paginationParameters(page,size)+postParameter(postId);
    }

    private String postParameter(Integer postId) {
        return Objects.nonNull(postId) ? POST_ID + postId : "";
    }

    @Override
    public String getCommentUrl(int commentId) {
        return baseUrl + COMMENTS_ENDPOINT + "/" + commentId;
    }

    private String paginationParameters(Integer page, Integer size) {
        final var sb = new StringBuilder("?");
        if (Objects.nonNull(page)) {
            sb.append(START).append(page);
        }
        if (Objects.nonNull(size)) {
            sb.append(LIMIT).append(size);
        }
        return sb.toString();
    }

    private String userParameter(Integer userId) {
        return Objects.nonNull(userId) ? USER_ID + userId : "";
    }
}
