package com.audition.common.exception;

import java.io.Serial;

public class IntegrationException extends SystemException {
    @Serial
    private static final long serialVersionUID = 1L;
    
    private static final String TITLE = "Server error";

    public IntegrationException(final String detail, final Integer errorCode) {
        super(detail, TITLE, errorCode);
    }
}
