package com.audition.model;

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

    private List<AuditionComment> auditionCommentsList = new ArrayList<>(); // Retaining initializer

    public AuditionPost(final Integer userId, final Integer id, final String title, final String body, final List<AuditionComment> auditionComments) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
        if (auditionComments != null) {
            this.auditionCommentsList = new ArrayList<>(auditionComments);
        }
    }

    public void setAuditionComments(final List<AuditionComment> auditionComments) {
        if (auditionComments != null) {
            this.auditionCommentsList = new ArrayList<>(auditionComments);
        } else {
            this.auditionCommentsList.clear(); // Handle null assignment
        }
    }

    public List<AuditionComment> getAuditionComments() {
        // Return an unmodifiable view to prevent external modifications
        return Collections.unmodifiableList(auditionCommentsList);
    }
}
