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

    private Integer userId;
    private Integer id;
    private String title;
    private String body;
    private List<AuditionComment> auditionCommentsList = new ArrayList<>();

    /**
     * Constructs a new AuditionPost with the specified details.
     *
     * @param userId                the user ID of the post creator
     * @param id                    the unique identifier of the post
     * @param title                 the title of the post
     * @param body                  the body content of the post
     * @param auditionComments      the list of audition comments associated with the post
     */
    public AuditionPost(final Integer userId, final Integer id, final String title, final String body, final List<AuditionComment> auditionComments) {
        this.userId = userId;
        this.id = id;
        this.title = title;
        this.body = body;
        if (auditionComments != null) {
            this.auditionCommentsList = new ArrayList<>(auditionComments);
        }
    }

    /**
     * Sets the audition comments for this post.
     *
     * @param auditionComments the list of audition comments to set
     */
    public void setAuditionComments(final List<AuditionComment> auditionComments) {
        if (auditionComments != null) {
            this.auditionCommentsList = new ArrayList<>(auditionComments);
        } else {
            this.auditionCommentsList.clear();
        }
    }

    /**
     * Returns an unmodifiable list of audition comments for this post.
     *
     * @return an unmodifiable list of audition comments
     */
    public List<AuditionComment> getAuditionComments() {
        return Collections.unmodifiableList(auditionCommentsList);
    }
}
