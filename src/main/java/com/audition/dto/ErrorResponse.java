package com.audition.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for representing error responses.
 * This class encapsulates the error message returned to the client.
 */
@Setter
@Getter
public class ErrorResponse {

    private String message;

    /**
     * Constructs a new ErrorResponse with the specified message.
     *
     * @param message the error message to be included in the response
     */
    public ErrorResponse(final String message) {
        this.message = message;
    }
}
