package com.audition.common.exception;

import java.io.Serial;

public class ClientException extends SystemException {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final String TITLE = "Client error";

    public ClientException(final String detail, final Integer errorCode) {
        super(detail, TITLE, errorCode);
    }
}
