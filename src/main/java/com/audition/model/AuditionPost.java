package com.audition.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an audition post.
 */
@Getter
@Setter
@NoArgsConstructor
public class AuditionPost {

    //@Positive(message = "User ID must be positive.")
    private Integer userId; // Change to Integer

    // @Positive(message = "Post ID must be positive.")
    private Integer id; // Change to Integer

    //@NotBlank(message = "Title cannot be blank.")
    private String title;

    // @NotBlank(message = "Body cannot be blank.")
    private String body;

    // Initialize to avoid null issues
    private List<AuditionComment> auditionComments = new ArrayList<>();

    public AuditionPost(Integer userId, Integer id, String title, String body, List<AuditionComment> auditionComments) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
        this.auditionComments = auditionComments != null ? auditionComments : new ArrayList<>();
    }

    public void setAuditionComments(List<AuditionComment> auditionComments) {
        // Create a new ArrayList to avoid exposing internal representation
        this.auditionComments = new ArrayList<>(auditionComments);
    }

    public List<AuditionComment> getAuditionComments() {
        // Return an unmodifiable view to prevent external modifications
        return Collections.unmodifiableList(auditionComments);
    }
}
