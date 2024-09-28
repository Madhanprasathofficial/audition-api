package com.audition.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class IntegrationUrlService implements IIntegrationUrlService {

    private static final String POSTS_ENDPOINT = "/posts";
    private static final String COMMENTS_ENDPOINT = "/comments";
    private static final String USER_ID_PARAM = "?userId=";
    private static final String PAGE_PARAM = "&_start=";
    private static final String SIZE_PARAM = "&_limit=";

    private final String baseUrl;

    public IntegrationUrlService(@Value("${application.config.baseUrl}") String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public String getPosts(Integer userId, Integer page, Integer size) {
        StringBuilder url = new StringBuilder(baseUrl + POSTS_ENDPOINT);
        userParameter(userId).ifPresent(url::append);
        paginationParameters(page, size).ifPresent(url::append);
        return url.toString();
    }

    @Override
    public String getPostById(Integer id) {
        return baseUrl + POSTS_ENDPOINT + "/" + id;
    }

    @Override
    public String getCommentsUrl(int postId, Integer page, Integer size) {
        StringBuilder url = new StringBuilder(getPostById(postId) + COMMENTS_ENDPOINT);
        paginationParameters(page, size).ifPresent(url::append);
        return url.toString();
    }

    @Override
    public String getCommentsUrl(int postId) {
        return getCommentsUrl(postId, null, null);
    }

    private Optional<String> paginationParameters(Integer page, Integer size) {
        StringBuilder params = new StringBuilder();
        Optional.ofNullable(page).ifPresent(p -> params.append(PAGE_PARAM).append(p));
        Optional.ofNullable(size).ifPresent(s -> params.append(SIZE_PARAM).append(s));
        return !params.toString().isEmpty() ? Optional.of(params.toString()) : Optional.empty();
    }

    private Optional<String> userParameter(Integer userId) {
        return Optional.ofNullable(userId).map(id -> USER_ID_PARAM + id);
    }
}
