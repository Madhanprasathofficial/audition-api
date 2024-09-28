package com.audition.service;

import com.audition.integration.IAuditionIntegrationPostsClient;
import com.audition.model.AuditionPost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test class for AuditionPostServiceImpl.
 */
class AuditionPostServiceImplTest {

    private transient AuditionPostServiceImpl auditionPostService;
    private transient IAuditionIntegrationPostsClient iAuditionIntegrationPostsClient;

    @BeforeEach
    void setUp() {
        iAuditionIntegrationPostsClient = mock(IAuditionIntegrationPostsClient.class);
        auditionPostService = new AuditionPostServiceImpl(iAuditionIntegrationPostsClient);
    }

    @Test
    void shouldGetListOfPosts() {
        // Given
        Integer userId = 1;
        Integer page = 0;
        Integer size = 5;
        List<AuditionPost> expectedPosts = List.of(new AuditionPost(), new AuditionPost()); // Mocked return data

        // When
        when(iAuditionIntegrationPostsClient.getPosts(userId, page, size)).thenReturn(expectedPosts);
        List<AuditionPost> actualPosts = auditionPostService.getPosts(userId, page, size);

        // Then
        verify(iAuditionIntegrationPostsClient).getPosts(userId, page, size); // Verifies if method was called
        assertEquals(expectedPosts, actualPosts); // Asserts that the returned posts match the expected posts
    }

    @Test
    void shouldGetPostById() {
        // Given
        Integer postId = 1;
        boolean includeComments = true;
        Integer page = 0;
        Integer size = 10;
        AuditionPost expectedPost = new AuditionPost(); // Mocked return data

        // When
        when(iAuditionIntegrationPostsClient.getPostById(postId, includeComments, page, size)).thenReturn(expectedPost);
        AuditionPost actualPost = auditionPostService.getPostById(postId, includeComments, page, size);

        // Then
        verify(iAuditionIntegrationPostsClient).getPostById(postId, includeComments, page, size); // Verifies method call
        assertEquals(expectedPost, actualPost); // Asserts the post returned is correct
    }

    @Test
    void shouldCaptureArgumentsForGetPosts() {
        // Given
        Integer userId = 123;
        Integer page = 1;
        Integer size = 10;

        // Capturing the arguments passed to getPosts method
        ArgumentCaptor<Integer> userIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> pageCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);

        // When
        auditionPostService.getPosts(userId, page, size);

        // Then
        verify(iAuditionIntegrationPostsClient).getPosts(userIdCaptor.capture(), pageCaptor.capture(), sizeCaptor.capture());
        assertEquals(userId, userIdCaptor.getValue());
        assertEquals(page, pageCaptor.getValue());
        assertEquals(size, sizeCaptor.getValue());
    }

    @Test
    void shouldCaptureArgumentsForGetPostById() {
        // Given
        Integer postId = 456;
        boolean includeComments = true;
        Integer page = 0;
        Integer size = 5;

        // Capturing the arguments passed to getPostById method
        ArgumentCaptor<Integer> postIdCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Boolean> includeCommentsCaptor = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Integer> pageCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> sizeCaptor = ArgumentCaptor.forClass(Integer.class);

        // When
        auditionPostService.getPostById(postId, includeComments, page, size);

        // Then
        verify(iAuditionIntegrationPostsClient).getPostById(postIdCaptor.capture(), includeCommentsCaptor.capture(), pageCaptor.capture(), sizeCaptor.capture());
        assertEquals(postId, postIdCaptor.getValue());
        assertEquals(includeComments, includeCommentsCaptor.getValue());
        assertEquals(page, pageCaptor.getValue());
        assertEquals(size, sizeCaptor.getValue());
    }
}
