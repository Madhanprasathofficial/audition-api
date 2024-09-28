package com.audition.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a comment for an audition post.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditionComment {

    @Positive(message = "Post ID must be positive.")
    private int postId;

    @Positive(message = "Comment ID must be positive.")
    private int id;

    @NotBlank(message = "Name cannot be blank.")
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Body cannot be blank.")
    private String body;

    @Override
    public String toString() {
        return "AuditionComment{" +
                "postId=" + postId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
